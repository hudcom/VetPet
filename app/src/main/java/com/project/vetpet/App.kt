package com.project.vetpet

import android.app.Application
import com.google.firebase.FirebaseApp
import com.project.vetpet.model.service.PetService

import com.project.vetpet.model.service.UserService

class App : Application() {

    val petService = PetService()
    val userService = UserService()

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}