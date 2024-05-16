package com.project.vetpet.model

class User(var email: String, val password: String, val fullName: String?, val number: String?, val city: String?){
    companion object{
        var currentUser:User? = null

        fun hasCurrentUser():Boolean{
            return currentUser != null
        }
    }
}