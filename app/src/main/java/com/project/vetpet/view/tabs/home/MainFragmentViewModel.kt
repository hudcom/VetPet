package com.project.vetpet.view.tabs.home

import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.project.vetpet.R
import com.project.vetpet.model.Clinics
import com.project.vetpet.model.Veterinarian
import com.project.vetpet.model.service.ClinicsService
import com.project.vetpet.model.service.VeterinarianService
import com.project.vetpet.view.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragmentViewModel(
    private val veterinarianService: VeterinarianService,
    private val clinicsService: ClinicsService
): BaseViewModel() {

    fun searchVeterinarianByName(name: String, onResult: (List<Veterinarian>?) -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = veterinarianService.searchVeterinarianByName(name)
                onResult(result)

            } catch (e: Exception) {
                onError(e)
            }
            delay(1000)
        }
    }

    fun searchClinicByName(name:String,onResult: (List<Clinics>) -> Unit, onError: (Exception) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = clinicsService.findClinicsByName(name)
                onResult(result)

            } catch (e: Exception) {
                onError(e)
            }
            delay(1000)
        }
    }

    fun createSpinner(activity: FragmentActivity): ArrayAdapter<CharSequence>{
        return ArrayAdapter.createFromResource(
            activity,
            R.array.find_category_items,
            R.layout.custom_spinner_adapter
        )
    }
}