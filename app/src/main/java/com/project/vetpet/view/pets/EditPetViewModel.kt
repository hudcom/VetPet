package com.project.vetpet.view.pets

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.vetpet.R
import com.project.vetpet.model.Pet
import com.project.vetpet.model.service.PetService
import com.project.vetpet.model.User
import com.project.vetpet.view.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    fun editElement(name: String, age: String, type: String, breed: String, callback: () -> Unit): Boolean {
        if ((selectedItem?.name == name) && (selectedItem?.age == age.toInt()) && (selectedItem?.type == type) && (selectedItem?.breed == breed)) return false

        val newPet = Pet(name, age.toInt(), type, breed, User.currentUser?.email)
        // Створення корутини
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { petService.editPet(selectedItem?.name ?: "", newPet) }
                callback()
            } catch (e: Exception) {
                Log.e(TAG, "Exception while editing pet", e)
            }
        }
        return true
    }

    fun deleteElement(name: String, callback: () -> Unit) {
        // Створення корутини
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { petService.deletePet(name) }
                delay(2000)
                callback()
            } catch (e: Exception) {
                Log.e(TAG, "Exception while deleting pet", e)
            }
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