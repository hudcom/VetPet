package com.project.vetpet.view

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.vetpet.model.service.APP_PREFERENCES
import com.project.vetpet.model.service.Preferences
import com.project.vetpet.model.User
import com.project.vetpet.model.service.ScheduleService
import com.project.vetpet.model.service.UserService
import com.project.vetpet.model.service.VeterinarianService

class MainActivityViewModel(
    private val firebaseService: UserService,
    private val veterinarianService: VeterinarianService,
    private val scheduleService: ScheduleService
): ViewModel() {

    fun initSharedPreferences(context: Context){
        Preferences.preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun saveSharedPreferences(){
        Preferences.saveSharedPreferences(User.currentUser)
    }

    private fun getSavedUser(): User? {
        return Preferences.getSharedPreferencesFromAppStorage()
    }

    fun initUser(){
        User.currentUser = getSavedUser()
    }

    fun initFirebase(){
        firebaseService.db = Firebase.firestore
        firebaseService.auth = Firebase.auth
        veterinarianService.db = firebaseService.db
    }

    fun hasCurrentUser(): Boolean{
        return User.hasCurrentUser()
    }

    fun getTopBarLocation():String {
       if (User.currentUser?.city != "null" && User.currentUser?.city != null && User.currentUser?.city != "")
           return User.currentUser?.city.toString()

        return "Ваше місто"
    }

}