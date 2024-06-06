package com.project.vetpet.view.clinic

import android.content.Context
import com.project.vetpet.adapters.ClinicsAdapter
import com.project.vetpet.adapters.MoreClinicButtonClickListener
import com.project.vetpet.model.Clinics
import com.project.vetpet.model.service.ClinicsService
import com.project.vetpet.view.BaseViewModel

class FindClinicsViewModel(
    val clinicsService: ClinicsService
): BaseViewModel() {



    fun createClinicsAdapter(listener: MoreClinicButtonClickListener, list:MutableList<Clinics>, context: Context): ClinicsAdapter {
        return ClinicsAdapter(listener,list,context)
    }

}