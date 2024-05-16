package com.project.vetpet.view

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.vetpet.model.service.APP_PREFERENCES
import com.project.vetpet.model.service.Preferences
import com.project.vetpet.model.User
import com.project.vetpet.model.service.UserService

class MainActivityViewModel(
    private val firebaseService: UserService
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
    }

    fun hasCurrentUser(): Boolean{
        return User.hasCurrentUser()
    }

    fun getTopBarLocation():String {
        return if (User.hasCurrentUser() && User.currentUser?.city != "null")
            User.currentUser?.city.toString()
        else
            "Ваше місто"
    }

}