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

    suspend fun addClinic(clinic: Clinics): Boolean = suspendCancellableCoroutine { cont ->
        val db = FirebaseFirestore.getInstance()
        db.collection("clinics")
            .document(clinic.name)
            .set(clinic)
            .addOnSuccessListener {
                cont.resume(true)
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
    }

    suspend fun checkClinicInDB(email: String, password: String): Boolean = suspendCancellableCoroutine { cont ->
        val db = FirebaseFirestore.getInstance()
        db.collection("clinics")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    cont.resume(true)
                } else {
                    cont.resume(false)
                }
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
    }

    suspend fun getClinicByEmailAndPassword(email: String, password: String): Clinics? = suspendCancellableCoroutine { cont ->
        val db = FirebaseFirestore.getInstance()
        db.collection("clinics")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val clinic = if (!querySnapshot.isEmpty) {
                    querySnapshot.documents[0].toObject(Clinics::class.java)
                } else {
                    null
                }
                cont.resume(clinic)
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
    }

}