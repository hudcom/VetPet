package com.project.vetpet

import android.app.Application
import com.google.firebase.FirebaseApp
import com.project.vetpet.model.service.PetService
import com.project.vetpet.model.service.ScheduleService

import com.project.vetpet.model.service.UserService
import com.project.vetpet.model.service.VeterinarianService

class App : Application() {

    val petService = PetService()
    val userService = UserService()
    val veterinarianService  = VeterinarianService()
    val scheduleService = ScheduleService()

    override fun onCreate() {
        super.onCreate()
        ScheduleService.instance
        FirebaseApp.initializeApp(this)
    }
}