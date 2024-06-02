package com.project.vetpet.model.service

import android.provider.Settings.System.DATE_FORMAT
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.project.vetpet.model.AppointmentSlot
import com.project.vetpet.model.MyAppointment
import com.project.vetpet.view.TAG
import kotlinx.coroutines.tasks.await
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ScheduleService {

    suspend fun getCompleteAppointmentsForClient(clientEmail: String): List<MyAppointment> {
        val appointments = getAppointmentsForClient(clientEmail)
        return appointments.map { appointment ->
            val address = getVetAddress(appointment.doctor)
            appointment.copy(address = address)
        }
    }

    suspend fun getAppointmentsForClient(clientEmail: String): List<MyAppointment> {
        return suspendCoroutine { cont ->
            val database = Firebase.database
            val myRef = database.getReference("schedule")

            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val appointments = mutableListOf<MyAppointment>()

                    for (vetSnapshot in dataSnapshot.children) {
                        val vetName = vetSnapshot.key ?: continue
                        for (daySnapshot in vetSnapshot.children) {
                            val date = daySnapshot.key ?: continue
                            for (timeSnapshot in daySnapshot.children) {
                                val client = timeSnapshot.getValue(String::class.java)
                                if (client == clientEmail) {
                                    val time = timeSnapshot.key ?: continue
                                    val appointment = MyAppointment(
                                        doctor = vetName,
                                        address = "", // Address will be set later
                                        date = date,
                                        time = time
                                    )
                                    appointments.add(appointment)
                                }
                            }
                        }
                    }

                    cont.resume(appointments)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    cont.resumeWith(Result.failure(databaseError.toException()))
                }
            })
        }
    }

    private suspend fun getVetAddress(vetName: String): String {
        val firestore = FirebaseFirestore.getInstance()
        val document = firestore.collection("veterinarian").document(vetName).get().await()
        return document.getString("workplace") ?: "Адреса не знайдена"
    }




    suspend fun updateAppointmentSlotInDatabase(slot: AppointmentSlot, vetName: String): Boolean = suspendCoroutine { cont ->
        // Отримуємо посилання на базу даних
        val myRef = Firebase.database.getReference("schedule")
            .child(vetName)
            .child(slot.day)

        // Оновлюємо значення поля client для відповідного слоту
        myRef.child(slot.time).setValue(slot.client)
            .addOnSuccessListener {
                // Запис успішно оновлено
                Log.d(TAG, "Appointment slot successfully updated in database")
                cont.resume(true)
            }
            .addOnFailureListener { exception ->
                // Помилка при оновленні запису
                Log.d(TAG, "Failed to update appointment slot in database", exception)
                cont.resume(false)
            }
    }

    suspend fun getScheduleFromDatabase(vetName: String, date: String): List<AppointmentSlot>? = suspendCoroutine { cont ->
        val database = Firebase.database
        val myRef = database.getReference("schedule").child(vetName).child(date)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val scheduleList = mutableListOf<AppointmentSlot>()

                for (slotSnapshot in dataSnapshot.children) {
                    val time = slotSnapshot.key
                    val client = slotSnapshot.value as? String

                    // Перевірка, чи є клієнт для цього слоту
                    val isBooked = client != ""


                    // Створення AppointmentSlot з отриманими даними
                    val slot = AppointmentSlot(day = date, time = time ?: "", isBooked = isBooked, client = client)

                    // Додавання AppointmentSlot до списку
                    scheduleList.add(slot)
                }

                // Повернення списку через continuation
                cont.resume(scheduleList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // У випадку скасування операції, повертаємо null
                cont.resume(null)
            }
        })
    }


    suspend fun createVetSchedule(vetName: String, date: String): Boolean = suspendCoroutine { cont ->
        // Отримуємо посилання на базу даних
        val myRef = Firebase.database.getReference("schedule")

        // Створюємо слоти
        val slots = generateTimeSlots()

        // Створюємо структуру
        val schedule = mutableMapOf<String, Any?>()
        for (slot in slots) {
            schedule[slot] = ""
        }

        // Додаємо до бази даних
        myRef.child(vetName).child(date).setValue(schedule)
            .addOnSuccessListener {
                // Дані успішно збережені
                Log.d(TAG, "Schedule successfully created for $vetName on $date")
                cont.resume(true)
            }
            .addOnFailureListener { exception ->
                // Помилка при збереженні даних
                Log.d(TAG, "Failed to create schedule for $vetName on $date", exception)
                cont.resume(false)
            }
    }

    private fun generateTimeSlots(): List<String> {
        val slots = mutableListOf<String>()
        var hour = 8
        var minute = 0
        val endHour = 17

        while (hour < endHour) {
            val startTime = String.format("%02d:%02d", hour, minute)
            minute += 30
            if (minute >= 60) {
                hour++
                minute -= 60
            }
            val endTime = String.format("%02d:%02d", hour, minute)
            slots.add("$startTime - $endTime")
        }

        return slots
    }


    companion object {
        private const val DATE_FORMAT = "dd:MM:yyyy"

        val instance: ScheduleService by lazy {
            ScheduleService().apply {
                initialize()
            }
        }
    }

    private fun initialize() {
        // Отримуємо поточну дату
        val currentDate = DateTime.now().withTimeAtStartOfDay()
        val currentDateString = currentDate.toString(DATE_FORMAT)

        // Отримуємо посилання на базу даних
        val databaseReference = Firebase.database.getReference("schedule")

        // Виконуємо запит до БД, щоб отримати всі записи
        databaseReference.get().addOnSuccessListener { dataSnapshot ->
            for (vetSnapshot in dataSnapshot.children) {
                val vetName = vetSnapshot.key ?: continue

                for (dateSnapshot in vetSnapshot.children) {
                    val dateString = dateSnapshot.key ?: continue

                    // Перевіряємо, чи дата запису вже минула
                    val appointmentDate = DateTime.parse(dateString, DateTimeFormat.forPattern(DATE_FORMAT))
                    if (appointmentDate.isBefore(currentDate)) {
                        // Видаляємо записи, які вже минули
                        databaseReference.child(vetName).child(dateString).removeValue()
                            .addOnSuccessListener {
                                // Успішно видалено
                                Log.d(TAG,"Old appointment on $dateString for $vetName removed")
                            }
                            .addOnFailureListener { exception ->
                                // Помилка видалення
                                Log.d(TAG,"Failed to remove old appointment on $dateString for $vetName: ${exception.message}")
                            }
                    }
                }
            }
        }.addOnFailureListener { exception ->
            // Помилка запиту до БД
            Log.d(TAG,"Failed to fetch appointments: ${exception.message}")
        }
    }
}