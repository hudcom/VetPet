package com.project.vetpet.view.pets

import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.project.vetpet.R
import com.project.vetpet.model.Pet
import com.project.vetpet.model.service.PetService
import com.project.vetpet.model.User

class AddPetViewModel(
    private val petService: PetService,
): ViewModel() {

    private lateinit var pet: Pet

    fun addPet(name:String, age:Int, type:String, breed:String){
        createPet(name,age,type,breed)
        petService.addPet(pet)
    }

    private fun createPet(name:String, age:Int, type:String, breed:String){
        pet = Pet(name, age, type, breed, User.currentUser?.email ?: "NULL")
    }

    fun createTypeSpinner(activity: FragmentActivity): ArrayAdapter<CharSequence> {
        return ArrayAdapter.createFromResource(
            activity,
            R.array.pets_type_array,
            R.layout.custom_spinner_adapter
        )
    }

    fun createAgeSpinner(activity: FragmentActivity): ArrayAdapter<CharSequence> {
        return ArrayAdapter.createFromResource(
            activity,
            R.array.numbers_array,
            R.layout.custom_spinner_adapter
        )
    }

}