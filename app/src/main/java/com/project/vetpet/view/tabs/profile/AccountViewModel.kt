package com.project.vetpet.view.tabs.profile

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.project.vetpet.model.AppointmentSlot
import com.project.vetpet.model.MyAppointment

import com.project.vetpet.utils.MenuItem
import com.project.vetpet.model.service.Preferences
import com.project.vetpet.model.User
import com.project.vetpet.model.service.ClinicsService
import com.project.vetpet.model.service.ScheduleService
import com.project.vetpet.model.service.UserService
import com.project.vetpet.view.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountViewModel(
    private val userService: UserService,
    private val scheduleService: ScheduleService,
    private val clinicsService: ClinicsService
): BaseViewModel() {

    fun deleteUser(){
        User.currentUser = null
        userService.logOut()
        Preferences.deleteSharedPreferences()
    }

    fun getUserFullName(): String {
        return User.currentUser?.fullName ?: "NULL"
    }

    fun getUserEmail(): String {
        return User.currentUser?.email ?: "NULL"
    }

    fun getMenuItemsList(): ArrayList<MenuItem>{
        return MenuItem.addAccountMenuItems()
    }

    fun getUser(): Bundle {
        return User.currentUser!!.toBundle()
    }

    fun getAllUsersAppointment(callback: (List<MyAppointment>) -> Unit){
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) { scheduleService.getCompleteAppointmentsForClient(getUserEmail()) }
            callback(list)
        }
    }

    fun findClinicNearby(callback: (List<String>) -> Unit){
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) { clinicsService.findAllClinicAddresses() }
            delay(1000)
            callback(list)
        }

    }

    fun checkVerification():Boolean = userService.checkEmailVerification()
}