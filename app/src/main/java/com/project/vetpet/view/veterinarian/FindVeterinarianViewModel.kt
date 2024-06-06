package com.project.vetpet.view.veterinarian

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.project.vetpet.adapters.MoreButtonClickListener
import com.project.vetpet.adapters.VeterinarianAdapter
import com.project.vetpet.model.User
import com.project.vetpet.model.Veterinarian
import com.project.vetpet.model.service.Preferences
import com.project.vetpet.model.service.VeterinarianService
import com.project.vetpet.view.BaseViewModel
import com.project.vetpet.view.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FindVeterinarianViewModel(
    private val veterinarianService: VeterinarianService
): BaseViewModel() {

    fun createVetAdapter(listener: MoreButtonClickListener, list:MutableList<Veterinarian>, context:Context): VeterinarianAdapter{
        return VeterinarianAdapter(listener,list,context)
    }

    fun getVetList(workplace: String, callback: (List<Veterinarian>) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                veterinarianService.findVeterinariansByWorkplace(workplace)
            }
            callback(result)
        }
    }
}