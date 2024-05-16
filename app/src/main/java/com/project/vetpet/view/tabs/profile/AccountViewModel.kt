package com.project.vetpet.view.tabs.profile

import androidx.lifecycle.ViewModel

import com.project.vetpet.utils.MenuItem
import com.project.vetpet.model.service.Preferences
import com.project.vetpet.model.User
import com.project.vetpet.model.service.UserService

class AccountViewModel(
    private val firebaseService: UserService
): ViewModel() {

    fun deleteUser(){
        User.currentUser = null
        firebaseService.logOut()
        Preferences.deleteSharedPreferences()
    }

    fun getUserFullName(): String {
        return User.currentUser?.fullName ?: "NULL"
    }

    fun getUserEmail(): String {
        return User.currentUser?.email ?: "NULL"
    }

    fun getMenuItemsList(): ArrayList<MenuItem>{
        return MenuItem.addMenuItems()
    }

}