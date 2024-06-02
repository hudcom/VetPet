package com.project.vetpet.view.veterinarian

import android.content.Context
import com.project.vetpet.adapters.MoreButtonClickListener
import com.project.vetpet.adapters.VeterinarianAdapter
import com.project.vetpet.model.Veterinarian
import com.project.vetpet.model.service.VeterinarianService
import com.project.vetpet.view.BaseViewModel

class FindVeterinarianViewModel(
    private val veterinarianService: VeterinarianService
): BaseViewModel() {

    fun createVetAdapter(listener: MoreButtonClickListener, list:MutableList<Veterinarian>, context:Context): VeterinarianAdapter{
        return VeterinarianAdapter(listener,list,context)
    }

}