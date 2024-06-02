package com.project.vetpet.model.service

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.vetpet.model.Pet
import com.project.vetpet.model.User
import com.project.vetpet.model.Veterinarian
import com.project.vetpet.view.TAG
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
                    if (workplace != null) {
                        Veterinarian(docName, workplace)
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

    companion object{
        const val REFERENCE = "veterinarian"
    }
}