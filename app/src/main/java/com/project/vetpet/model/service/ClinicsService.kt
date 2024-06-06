package com.project.vetpet.model.service

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.project.vetpet.model.Clinics
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ClinicsService {

    suspend fun findClinicsByName(name: String): MutableList<Clinics> = suspendCoroutine { cont ->
        val db = FirebaseFirestore.getInstance()
        db.collection("clinics")
            .whereGreaterThanOrEqualTo(FieldPath.documentId(), name)
            .whereLessThanOrEqualTo(FieldPath.documentId(), name + '\uf8ff')
            .get()
            .addOnSuccessListener { querySnapshot ->
                val clinics = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Clinics::class.java)
                }
                cont.resume(clinics.toMutableList())
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
    }

    suspend fun findAllClinicAddresses(): MutableList<String> = suspendCancellableCoroutine { cont ->
        val db = FirebaseFirestore.getInstance()
        db.collection("clinics")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val addresses = querySnapshot.documents.mapNotNull { document ->
                    document.getString("address")
                }
                cont.resume(addresses.toMutableList())
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
    }

}