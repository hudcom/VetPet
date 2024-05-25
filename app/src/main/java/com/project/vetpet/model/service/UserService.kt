package com.project.vetpet.model.service

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.project.vetpet.model.User
import com.project.vetpet.view.TAG
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserService {

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    /**
    * Register
     */

    suspend fun checkEmailInAuth(email: String): Boolean = suspendCoroutine { cont ->
        Firebase.auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    cont.resume(!signInMethods.isNullOrEmpty())
                } else {
                    Log.w(TAG, "Error fetching sign-in methods for email", task.exception)
                    cont.resume(false)
                }
            }
    }
    suspend fun checkEmailInFirestore(email: String): Boolean = suspendCoroutine { cont ->
        val documentRef = db.collection(USERS_KEY).document(email)
        documentRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    cont.resume(document != null && document.exists())
                } else {
                    Log.w(TAG, "Error fetching document", task.exception)
                    cont.resume(false)
                }
            }
    }
    suspend fun writeNewUser(user: User, callback: (User?) -> Unit) {
        val hashMapUser = convertUser(user)
        val email = user.email

        val authSuccess = writeUserToAuthDb(email, user.password)
        if (authSuccess) {
            val dbSuccess = writeUserToDb(email, hashMapUser)
            if (dbSuccess) {
                callback(user)
            } else callback(null)
        } else{
            callback(null)
        }
    }
    private suspend fun writeUserToDb(documentId: String, user: HashMap<String, String?>): Boolean = suspendCoroutine { cont ->
        FirebaseFirestore.getInstance().collection(USERS_KEY)
            .document(documentId)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "[Firestore] DocumentSnapshot added with ID: $documentId")
                cont.resume(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "[Firestore] Error adding document", e)
                cont.resume(false)
            }
    }
    private suspend fun writeUserToAuthDb(email: String, password: String): Boolean = suspendCoroutine { cont ->
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "[Authentication] Create user success")
                cont.resume(true)
            } else {
                Log.w(TAG, "[Authentication] Create user ERROR", task.exception)
                cont.resume(false)
            }
        }
    }
    private fun convertUser(user:User): HashMap<String,String?>{
        return hashMapOf(
            EMAIL to user.email,
            PASSWORD to user.password,
            FULL_NAME to user.fullName,
            NUMBER to user.number,
            CITY to user.city
        )
    }


    /**
    * Authentication
     */

    suspend fun authUser(email: String, password: String): Boolean = suspendCoroutine { cont ->
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "[Authentication] Sign in Success")
                cont.resume(true)
            } else {
                Log.d(TAG, "[Authentication] Sign in ERROR")
                cont.resume(false)
            }
        }
    }
    suspend fun readUserFromDb(email: String): User? = suspendCoroutine { cont ->
        val docRef = db.collection(USERS_KEY).document(email)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val createdUser = createUser(document)
                    Log.d(TAG, "[Firebase] Have such document: ${createdUser.fullName} ${createdUser.email}")
                    cont.resume(createdUser)
                } else {
                    Log.w(TAG, "[Firebase] No such document")
                    cont.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "[Firebase] get failed with ", exception)
                cont.resume(null)
            }
    }
    private fun createUser(document: DocumentSnapshot): User{
        return User(
            document.get(EMAIL).toString(),
            document.get(PASSWORD).toString(),
            document.get(FULL_NAME).toString(),
            document.get(NUMBER).toString(),
            document.get(CITY).toString()
        )
    }


    /**
    * Verification
     */

    fun checkEmailVerification(): Boolean {
        val user = auth.currentUser
        return user?.isEmailVerified ?: false
    }
    suspend fun sendEmailVerification(): Boolean = suspendCoroutine { cont ->
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "[Firebase] Verification email sent to ${user.email}")
                    cont.resume(true)
                } else {
                    Log.w(TAG, "[Firebase] Error sending verification email", task.exception)
                    cont.resume(false)
                }
            }
    }


    /**
     * Edit user
     */

    suspend fun editUser(email: String, newUser: User): Boolean = suspendCoroutine { cont ->
        val documentRef = db.collection(USERS_KEY).document(email)

        documentRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                if (email == newUser.email) {
                    documentRef.set(newUser)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot successfully updated for email: $email")
                            cont.resume(true)
                        }
                        .addOnFailureListener { e ->
                            Log.d(TAG, "Error updating document for email: $email", e)
                            cont.resumeWithException(e)
                        }
                } else {
                    val oldDocumentRef = db.collection(USERS_KEY).document(email)
                    val newDocumentRef = db.collection(USERS_KEY).document(newUser.email)

                    newDocumentRef.set(newUser)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot successfully updated for new email: ${newUser.email}")
                            oldDocumentRef.delete()
                                .addOnSuccessListener {
                                    Log.d(TAG, "Old document successfully deleted for old email: $email")
                                    cont.resume(true)
                                }
                                .addOnFailureListener { e ->
                                    Log.d(TAG, "Error deleting old document: $e")
                                    cont.resumeWithException(e)
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.d(TAG, "Error updating document for new email: ${newUser.email}", e)
                            cont.resumeWithException(e)
                        }
                }
            } else {
                Log.d(TAG, "Document does not exist for email: $email")
                cont.resumeWithException(Exception("Document does not exist"))
            }
        }.addOnFailureListener { e ->
            Log.d(TAG, "Error getting document for email: $email", e)
            cont.resumeWithException(e)
        }
    }
    suspend fun editEmailCurrentUser(newEmail: String, currentPassword: String): Boolean = suspendCoroutine { cont ->
        val user = auth.currentUser
        if (user != null) {
            if (user.isEmailVerified) {
                val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

                user.reauthenticate(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            user.updateEmail(newEmail)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "User email address updated.")
                                        cont.resume(true)
                                    } else {
                                        Log.w(TAG, "Error updating email", task.exception)
                                        cont.resumeWithException(task.exception ?: Exception("Unknown error"))
                                    }
                                }
                        } else {
                            Log.w(TAG, "Error re-authenticating", authTask.exception)
                            cont.resumeWithException(authTask.exception ?: Exception("Unknown error"))
                        }
                    }
            } else {
                Log.w(TAG, "User email is not verified.")
                cont.resumeWithException(Exception("User email is not verified"))
            }
        } else {
            cont.resumeWithException(Exception("No current user"))
        }
    }
    suspend fun editPasswordCurrentUser(newPassword: String, currentPassword: String): Boolean = suspendCoroutine { cont ->
        val user = auth.currentUser
        if (user != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            user.reauthenticate(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "[Authentication] User password updated.")
                                    cont.resume(true)
                                } else {
                                    Log.w(TAG, "[Authentication] Error updating password", task.exception)
                                    cont.resumeWithException(task.exception ?: Exception("Unknown error"))
                                }
                            }
                    } else {
                        Log.w(TAG, "[Authentication] Error re-authenticating", authTask.exception)
                        cont.resumeWithException(authTask.exception ?: Exception("Unknown error"))
                    }
                }
        } else {
            cont.resumeWithException(Exception("No current user"))
        }
    }

    /*private fun getUpdates(newUser: User): Map<String,String?>{
        return mapOf(
            EMAIL to newUser.email,
            PASSWORD to newUser.password,
            FULL_NAME to newUser.fullName,
            NUMBER to newUser.number,
            CITY to newUser.city
        )
    }*/




    private fun hasCurrentUser():Boolean {
        return Firebase.auth.currentUser != null
    }

    fun logOut(){
        auth.signOut()
    }



    suspend fun deleteUserFromAuthDb(currentPassword: String): Boolean = suspendCoroutine { cont ->
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val email = user.email
            if (email != null) {
                val credential = EmailAuthProvider.getCredential(email, currentPassword)
                // Реаутентифікація користувача
                user.reauthenticate(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            // Якщо реаутентифікація успішна, видаляємо користувача
                            user.delete()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "[Authentication] Delete user success")
                                        cont.resume(true)
                                    } else {
                                        Log.d(TAG, "[Authentication] Delete user ERROR: ${task.exception}")
                                        cont.resume(false)
                                    }
                                }
                        } else {
                            Log.d(TAG, "[Authentication] Re-authentication failed: ${authTask.exception}")
                            cont.resume(false)
                        }
                    }
            } else {
                Log.d(TAG, "[Authentication] User email is null")
                cont.resume(false)
            }
        } else {
            Log.d(TAG, "[Authentication] No user signed in")
            cont.resume(false)
        }
    }
    suspend fun deleteUserFromFirestore(email: String): Boolean = suspendCoroutine { cont ->
        val documentRef = FirebaseFirestore.getInstance().collection(USERS_KEY).document(email)
        documentRef.delete()
            .addOnSuccessListener {
                Log.d(TAG, "[Firestore] Document successfully deleted with email: $email")
                cont.resume(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "[Firestore] Error deleting document", e)
                cont.resume(false)
            }
    }

    companion object{
        private const val USERS_KEY = "users"

        private const val EMAIL = "Email"
        private const val PASSWORD = "Password"
        private const val FULL_NAME = "Full name"
        private const val NUMBER = "Number"
        private const val CITY = "City"
    }
}


