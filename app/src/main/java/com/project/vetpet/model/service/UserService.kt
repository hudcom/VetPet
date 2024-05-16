package com.project.vetpet.model.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.project.vetpet.model.User
import com.project.vetpet.view.TAG

class UserService {

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    fun hasCurrentUser():Boolean{
        return Firebase.auth.currentUser != null
    }

    fun writeNewUser(user:User){
        val hashMapUser = convertUser(user)
        val email = user.email

        writeUserToDb(email,hashMapUser)
        writeUserToAuthDb(email, user.password)
        User.currentUser = user
    }

    fun authUser(email:String, password:String){
        signInUser(email,password)
    }


    fun logOut(){
        auth.signOut()
    }



    private fun writeUserToDb(documentId:String, user: HashMap<String,String?>){
        db.collection(USERS_KEY)
            .document(documentId)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: $documentId")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun readUserFromDb(email:String) {
        val docRef = db.collection(USERS_KEY).document(email)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    User.currentUser = createUser(document)
                    Log.d(TAG, "[Firebase] Have such document")
                    Log.d(TAG, " ${User.currentUser?.fullName ?: "NULL"} ${User.currentUser?.email ?: "NULL"}" )

                } else {
                    Log.d(TAG, "[Firebase] No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "[Firebase] get failed with ", exception)
            }
    }

    private fun writeUserToAuthDb(email:String, password:String){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
            if (it.isSuccessful){
                Log.d(TAG,"[Firebase] Create user success")
            } else Log.d(TAG,"[Firebase] Create user ERROR")
        }
    }

    private fun signInUser(email:String, password:String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
            if (it.isSuccessful){
                readUserFromDb(email)
                Log.d(TAG,"[Firebase] Sign in Success")
            } else
                Log.d(TAG,"[Firebase] Sign in ERROR")
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
    private fun createUser(document: DocumentSnapshot): User{
        return User(
            document.get(EMAIL).toString(),
            document.get(PASSWORD).toString(),
            document.get(FULL_NAME).toString(),
            document.get(NUMBER).toString(),
            document.get(CITY).toString()
        )
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


