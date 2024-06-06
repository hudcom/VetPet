package com.project.vetpet.model.service


import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.project.vetpet.model.Veterinarian
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class VeterinarianService {

    lateinit var db: FirebaseFirestore

    suspend fun searchVeterinarianByName(name: String): MutableList<Veterinarian> = suspendCoroutine { cont ->
        db.collection(REFERENCE)
            .whereGreaterThanOrEqualTo(FieldPath.documentId(), name)
            .whereLessThanOrEqualTo(FieldPath.documentId(), name + '\uf8ff')
            .get()
            .addOnSuccessListener { querySnapshot ->
                val veterinarians = querySnapshot.documents.mapNotNull { document ->
                    val docName = document.id // Ім'я документа
                    val workplace = document.getString("workplace")
                    val address = document.getString("address")
                    if (workplace != null) {
                        Veterinarian(docName, workplace, address.toString())
                    } else {
                        null
                    }
                }
                cont.resume(veterinarians.toMutableList())
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
    }

    suspend fun findVeterinariansByWorkplace(workplace: String): List<Veterinarian> = suspendCancellableCoroutine { continuation ->
        val db = FirebaseFirestore.getInstance()
        db.collection("veterinarian")
            .whereEqualTo("workplace", workplace)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val veterinarians = querySnapshot.documents.mapNotNull { document ->
                    val fullName = document.id // Отримуємо ім'я файлу
                    val workplace = document.getString("workplace")
                    val address = document.getString("address")
                    if (workplace != null && address != null) {
                        Veterinarian(fullName, workplace, address)
                    } else {
                        null
                    }
                }
                continuation.resume(veterinarians)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    companion object{
        const val REFERENCE = "veterinarian"
    }
}