package com.project.vetpet.view.veterinarian


import com.project.vetpet.model.Veterinarian
import com.project.vetpet.model.service.VeterinarianService
import com.project.vetpet.utils.MenuItem
import com.project.vetpet.view.BaseViewModel

class VeterinarianViewModel(
    veterinarianService: VeterinarianService
): BaseViewModel() {

    private var currentVet:Veterinarian? = null

    fun getMenuItemsList():ArrayList<MenuItem>{
        return MenuItem.addVeterinarianMenuItems()
    }

    fun extractDataFromBundle(veterinarian: Veterinarian?){
        currentVet = veterinarian
    }


    fun getVeterinarian(): Veterinarian? = currentVet
    fun getFullName():String = currentVet?.fullName ?: ""
    fun getWorkplace():String = currentVet?.workplace ?: ""
    fun getAddress():String = currentVet?.address ?: ""

}