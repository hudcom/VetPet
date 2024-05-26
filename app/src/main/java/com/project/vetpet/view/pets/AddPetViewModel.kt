package com.project.vetpet.view.pets

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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPetViewModel(
    private val petService: PetService,
): ViewModel() {

    fun addPet(name: String, age: Int, type: String, breed: String, callback: (Boolean)-> Unit) {
        val pet = createPet(name, age, type, breed)
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO){ petService.addPet(pet) }
                if (result) {
                    callback(true)
                    Log.d(TAG, "Pet successfully added")
                } else {
                    callback(false)
                    Log.e(TAG, "Failed to add pet")
                }
            } catch (e: Exception) {
                callback(false)
                Log.e(TAG, "Exception while adding pet", e)
            }
        }
    }


    private fun createPet(name:String, age:Int, type:String, breed:String): Pet{
        return Pet(name, age, type, breed, User.currentUser?.email ?: "NULL")
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