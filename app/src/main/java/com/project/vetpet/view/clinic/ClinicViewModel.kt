package com.project.vetpet.view.clinic

import com.project.vetpet.model.Clinics
import com.project.vetpet.utils.MenuItem
import com.project.vetpet.view.BaseViewModel

class ClinicViewModel(

): BaseViewModel() {

    private var currentClinic: Clinics? = null

    fun extractDataFromBundle(clinics: Clinics){
        currentClinic = clinics
    }

    fun getMenuItemsList():ArrayList<MenuItem>{
        return MenuItem.addClinicMenuItem()
    }

    fun getClinicName() = currentClinic?.name ?: ""
    fun getClinicAddress() = currentClinic?.address ?: ""
    fun getClinicListVet() = currentClinic?.employee ?: mutableListOf()

}