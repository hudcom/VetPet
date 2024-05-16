package com.project.vetpet.model.service

import android.content.SharedPreferences
import android.util.Log
import com.project.vetpet.model.User
import com.project.vetpet.view.TAG

const val APP_PREFERENCES = "APP_PREFERENCES"

class Preferences {
    companion object {
        lateinit var preferences: SharedPreferences

        private const val ShP_EMAIL = "EMAIL"
        private const val ShP_PASSWORD = "PASSWORD"
        private const val ShP_FULL_NAME = "FULL_NAME"
        private const val ShP_NUMBER = "NUMBER"
        private const val ShP_CITY = "CITY"

        fun getSharedPreferencesFromAppStorage(): User?{
            val user = User(
                preferences.getString(ShP_EMAIL,"").toString(),
                preferences.getString(ShP_PASSWORD,"").toString(),
                preferences.getString(ShP_FULL_NAME,"").toString(),
                preferences.getString(ShP_NUMBER,"").toString(),
                preferences.getString(ShP_CITY,"").toString(),
            )
            if (user.email == "" || user.password == "" || user.fullName == "") return null

            Log.d(TAG,"Get SharedPreferences")
            return user
        }

        fun saveSharedPreferences(user: User?){
            if (user != null){
                preferences.edit().
                    putString(ShP_EMAIL,user.email).
                    putString(ShP_PASSWORD, user.password).
                    putString(ShP_FULL_NAME, user.fullName).
                    putString(ShP_NUMBER, user.number).
                    putString(ShP_CITY, user.city)
                        .apply()
            }
            Log.d(TAG,"Save SharedPreferences")
        }

        fun deleteSharedPreferences(){
            preferences.edit().clear().apply();
            Log.d(TAG,"Delete SharedPreferences")
        }
    }
}
