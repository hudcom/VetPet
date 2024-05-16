package com.project.vetpet.view.pets

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.vetpet.R
import com.project.vetpet.model.Pet
import com.project.vetpet.model.service.PetService
import com.project.vetpet.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EditPetViewModel(
    private val petService: PetService
): ViewModel()  {

    private var selectedItem: Pet? = null
    private var oldName: String? = null

    fun convertBundle(bundle: Bundle) {
        selectedItem = Pet.fromBundle(bundle)
        oldName = selectedItem?.name
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

    fun editElement(name:String, age: String, type: String, breed: String, callback: () -> Unit): Boolean{
        if ((selectedItem?.name == name) && (selectedItem?.age == age.toInt()) && (selectedItem?.type == type) && (selectedItem?.breed == breed)) return false

        val newPet = Pet(name,age.toInt(),type,breed,User.currentUser?.email)
        //Створення корутини
        viewModelScope.launch {
            async { petService.editPet(oldName, newPet) }.await()
            delay(2000)
            callback()
        }
        return true
    }

    fun deleteElement(name:String, callback: () -> Unit){
        //Створення корутини
        viewModelScope.launch {
            async { petService.deletePet(name) }.await()
            delay(2000)
            callback()
        }
    }


    fun getName(): String? {
        return selectedItem?.name
    }

    fun getBreed():String?{
        return selectedItem?.breed
    }

    fun getAge(): Int? {
        return selectedItem?.age
    }

    fun getType(): String?{
        return selectedItem?.type
    }
}