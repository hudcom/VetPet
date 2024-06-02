package com.project.vetpet.view.pets

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.vetpet.adapters.EditButtonClickListener
import com.project.vetpet.adapters.MoreButtonClickListener
import com.project.vetpet.adapters.PetAdapter
import com.project.vetpet.model.Pet
import com.project.vetpet.model.service.PetService
import com.project.vetpet.view.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPetsViewModel(
    private val petService: PetService,

    ): ViewModel() {

    private var pets: MutableList<Pet>? = null
    private var selectItem: Pet? = null


    fun setSelectedItem(pet: Pet) {
       selectItem = pet
    }

    fun getSelectedItem(): Bundle {
        return selectItem!!.toBundle()
    }

    fun getPetsList(callback: () -> Unit) {
        viewModelScope.launch {
            try {
                pets = withContext(Dispatchers.IO) { petService.getPetList() }
                callback()
            } catch (e: Exception) {
                Log.e(TAG, "Exception while getting pet list", e)
            }
        }
    }

    fun getPetAdapter(listener:EditButtonClickListener, viewModel: MyPetsViewModel, context: Context): PetAdapter{
        return PetAdapter(listener,pets ?: mutableListOf(),viewModel, context)
    }


}