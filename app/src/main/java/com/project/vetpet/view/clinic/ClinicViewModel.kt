package com.project.vetpet.view.clinic

import androidx.lifecycle.viewModelScope
import com.project.vetpet.model.Clinics
import com.project.vetpet.model.Veterinarian
import com.project.vetpet.model.service.ClinicsService
import com.project.vetpet.model.service.VeterinarianService
import com.project.vetpet.utils.MenuItem
import com.project.vetpet.view.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClinicViewModel(
    private val clinicsService: ClinicsService,
    private val veterinarianService: VeterinarianService
): BaseViewModel() {

    private var currentClinic: Clinics? = null

    fun extractDataFromBundle(clinics: Clinics){
        currentClinic = clinics
    }

    fun deleteClinicObject(){
        Clinics.currentClinic = null
    }

    fun getMenuItemsList():ArrayList<MenuItem>{
        return MenuItem.addClinicMenuItems()
    }

    fun getClinic() = currentClinic
    fun getClinicName() = currentClinic?.name ?: ""
    fun getClinicAddress() = currentClinic?.address ?: ""
    fun getClinicEmail() = currentClinic?.email ?: ""
    fun hasCurrentClinic() = Clinics.currentClinic != null


    fun addEmployee(name:String,callback: (Boolean) -> Unit){
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { veterinarianService.addVeterinarian(
                Veterinarian(name,getClinicName(),getClinicAddress())
            ) }
            delay(1000)
            callback(result)
        }
    }


    fun addClinic(clinic: Clinics,callback: (Boolean) -> Unit){
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { clinicsService.addClinic(clinic) }
            delay(1000)
            callback(result)
        }
    }

    fun authClinic(email:String, password:String, callback: (Boolean,Clinics?) -> Unit){
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { clinicsService.checkClinicInDB(email,password) }
            var clinic:Clinics? = null
            if (result){
                clinic = withContext(Dispatchers.IO) { clinicsService.getClinicByEmailAndPassword(email,password) }
            }
            delay(2000)
            callback(result,clinic)
        }
    }

    fun getClinicListVeterinarian(callback: (List<Veterinarian>) -> Unit){
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) { veterinarianService.findVeterinariansByWorkplace(getClinicName()) }
            delay(1000)
            callback(list)
        }
    }


}