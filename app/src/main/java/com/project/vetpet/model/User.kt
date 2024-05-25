package com.project.vetpet.model

import android.os.Bundle

class User(var email: String, val password: String, val fullName: String?, val number: String?, val city: String?){

    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString("email", email)
        bundle.putString("password", password)
        bundle.putString("fullName", fullName ?: "")
        bundle.putString("number", number ?: "")
        bundle.putString("city", city ?: "")
        return bundle
    }

    constructor(): this("","",null,null,null)

    companion object{
        var currentUser:User? = null

        fun hasCurrentUser():Boolean{
            return currentUser != null
        }

        fun fromBundle(bundle: Bundle?): User {
            return if (bundle != null) {
                User(
                    bundle.getString("email") ?: "",
                    bundle.getString("password") ?: "",
                    bundle.getString("fullName"),
                    bundle.getString("number"),
                    bundle.getString("city")
                )
            } else {
                User()
            }
        }
    }
}