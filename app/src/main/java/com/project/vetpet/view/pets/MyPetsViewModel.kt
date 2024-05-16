package com.project.vetpet.view.pets

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.vetpet.adapters.EditButtonClickListener
import com.project.vetpet.adapters.PetAdapter
import com.project.vetpet.model.Pet
import com.project.vetpet.model.service.PetService
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    fun getPetsList(callback: () -> Unit){
        //Створення корутини
        viewModelScope.launch {
            async { pets = petService.getPetList() }.await()
            delay(500)
            callback()
        }
    }

    fun getPetAdapter(listener:EditButtonClickListener, viewModel: MyPetsViewModel, context: Context): PetAdapter{
        return PetAdapter(listener,pets ?: mutableListOf(),viewModel, context)
    }


}