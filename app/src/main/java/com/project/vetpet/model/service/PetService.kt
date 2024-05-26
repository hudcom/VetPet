
package com.project.vetpet.model.service

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.project.vetpet.model.Pet
import com.project.vetpet.model.User
import com.project.vetpet.view.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PetService {

    private val petList: MutableList<Pet> = mutableListOf()

    private fun getDatabase(): FirebaseDatabase = Firebase.database

    /**
     * Add Pet
     */
    suspend fun addPet(pet: Pet): Boolean = suspendCoroutine { cont ->
        val myRef = getDatabase().getReference(REFERENCE)
        myRef.child(pet.name!!).setValue(createObject(pet))
            .addOnSuccessListener {
                cont.resume(true)
            }
            .addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
    }
    private fun createObject(pet: Pet): Map<String,Any?> {
        // Створення об'єкта, який ви хочете додати до таблиці
        return mapOf(
            PET_AGE to pet.age,
            PET_TYPE to pet.type,
            PET_BREED to pet.breed,
            OWNER to pet.owner)
    }

    /**
     * Read Pet
     */
    suspend fun getPetList(): MutableList<Pet> {
        petList.clear()
        readPetList()
        return petList
    }
    private suspend fun readPetList() {
        val myRef = getDatabase().getReference(REFERENCE)
        val query = myRef.orderByChild(OWNER).equalTo(User.currentUser?.email)

        suspendCoroutine<Unit> { cont ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataChange(dataSnapshot)
                    cont.resume(Unit)
                }

                override fun onCancelled(error: DatabaseError) {
                    cont.resumeWithException(error.toException())
                }
            })
        }
    }
    private fun dataChange(dataSnapshot: DataSnapshot){
        // Цей метод буде викликаний при зміні даних відповідно до запиту
        for (snapshot in dataSnapshot.children) {
            // Отримуємо дані з кожного запису
            val value = snapshot.getValue<Pet>()
            if (value != null) {
                value.name = snapshot.key
                petList.add(value) // Додаємо елемент до petList
            }
        }
    }


    /**
     * Edit Pet
     */
    suspend fun editPet(oldName: String?, pet: Pet): Boolean = suspendCoroutine { cont ->
        val myRef = getDatabase().getReference("$REFERENCE/$oldName")

        myRef.updateChildren(getUpdates(oldName, pet))
            .addOnSuccessListener {
                cont.resume(true)
            }
            .addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
    }
    private fun getUpdates(oldName:String?, pet: Pet): HashMap<String, Any>{
        val updates = HashMap<String, Any>()
        //Оновлюємо поля запису
        updates[PET_AGE] = pet.age!!
        updates[PET_TYPE] = pet.type!!
        updates[PET_BREED] = pet.breed!!

        //Викликаємо метод  для створення нового та видалення старого запису
        if (oldName != pet.name){
            editPetInRealtimeDB(oldName!!,pet.name!!)
        }

        Log.d(TAG,"EDIT ELEMENT $oldName")
        return updates
    }
    private fun editPetInRealtimeDB(oldName: String, newName: String){
        val oldRef = getDatabase().getReference("$REFERENCE/$oldName")

        oldRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Зчитуємо дані з запису
                val data = dataSnapshot.value
                //Видалення старого запису
                oldRef.removeValue()
                //Створення нового запису
                val newRef = getDatabase().getReference("$REFERENCE/$newName")
                newRef.setValue(data)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG,"Error while create new file: $databaseError")
            }
        })
    }
    suspend fun editAllUsersPet(email: String, newEmail: String): Boolean = suspendCoroutine { cont ->
        val myRef = getDatabase().getReference(REFERENCE)
        val query = myRef.orderByChild(OWNER).equalTo(email)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val pet = snapshot.getValue(Pet::class.java)
                    if (pet != null) {
                        val petKey = snapshot.key
                        if (petKey != null) {
                            myRef.child(petKey).child(OWNER).setValue(newEmail)
                                .addOnSuccessListener {
                                    Log.d(TAG, "Owner updated for pet: $petKey")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Failed to update owner for pet: $petKey", e)
                                }
                        }
                    }
                }
                cont.resume(true)
            }

            override fun onCancelled(error: DatabaseError) {
                cont.resumeWithException(error.toException())
            }
        })
    }


    /**
     * Delete Pet
     */
    suspend fun deletePet(name: String): Boolean = suspendCoroutine { cont ->
        val myRef = getDatabase().getReference(REFERENCE)
        myRef.child(name).removeValue()
            .addOnSuccessListener {
                cont.resume(true)
            }
            .addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
    }
    suspend fun deleteAllPetsOwnedBy(email: String): Boolean = suspendCoroutine { cont ->
        val myRef = getDatabase().getReference(REFERENCE)
        val query = myRef.orderByChild(OWNER).equalTo(email)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val petKey = snapshot.key
                    if (petKey != null) {
                        myRef.child(petKey).removeValue()
                            .addOnSuccessListener {
                                Log.d(TAG, "Pet with key $petKey deleted")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Failed to delete pet with key $petKey", e)
                            }
                    }
                }
                cont.resume(true)
            }

            override fun onCancelled(error: DatabaseError) {
                cont.resumeWithException(error.toException())
            }
        })
    }


    companion object{
        private const val REFERENCE = "pets"

        private const val PET_NAME = "name"
        private const val PET_AGE = "age"
        private const val PET_TYPE = "type"
        private const val PET_BREED = "breed"
        private const val OWNER = "owner"
    }
}
