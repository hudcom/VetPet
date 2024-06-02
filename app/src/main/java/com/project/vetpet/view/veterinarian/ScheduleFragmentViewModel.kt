package com.project.vetpet.view.veterinarian

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.project.vetpet.model.AppointmentSlot
import com.project.vetpet.model.service.ScheduleService
import com.project.vetpet.view.BaseViewModel
import com.project.vetpet.view.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleFragmentViewModel(
    private val scheduleService: ScheduleService
): BaseViewModel() {

    fun writeAppointmentSlot(slot: AppointmentSlot,vetName:String,callback: (Boolean) -> Unit){
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    scheduleService.updateAppointmentSlotInDatabase(slot,vetName)
                }
                Log.d(TAG, "Result: $result")
                callback(result)

            } catch (e: Exception) {
                Log.d(TAG, "Error: ${e.message}")
            }
        }

    }

    fun getSchedule(name: String, date: String, callback: (List<AppointmentSlot>) -> Unit) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    scheduleService.getScheduleFromDatabase(name, date)
                }
                if (result != null) {
                    callback(result)
                }

            } catch (e: Exception) {
                Log.d(TAG, "Error: ${e.message}")
            }
        }
    }

    fun crateSchedule(name: String, date: String) {
        viewModelScope.launch {
            try {
                val success = scheduleService.createVetSchedule(name, date)
                if (success) {
                   Log.d(TAG,"Success")
                } else {
                    Log.d(TAG,"Not success")
                }
            } catch (e: Exception) {
                Log.d(TAG,"Error")
            }
        }
    }
}