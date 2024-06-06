package com.project.vetpet.view

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.vetpet.App
import com.project.vetpet.view.clinic.ClinicViewModel
import com.project.vetpet.view.clinic.FindClinicsViewModel
import com.project.vetpet.view.tabs.profile.EditAccountViewModel
import com.project.vetpet.view.tabs.profile.AccountViewModel
import com.project.vetpet.view.pets.AddPetViewModel
import com.project.vetpet.view.pets.EditPetViewModel
import com.project.vetpet.view.tabs.profile.AuthViewModel
import com.project.vetpet.view.pets.MyPetsViewModel
import com.project.vetpet.view.tabs.home.MainFragmentViewModel
import com.project.vetpet.view.tabs.profile.MyAppointmentViewModel
import com.project.vetpet.view.tabs.profile.RegisterViewModel
import com.project.vetpet.view.veterinarian.FindVeterinarianViewModel
import com.project.vetpet.view.schedule.ScheduleFragmentViewModel
import com.project.vetpet.view.tabs.map.MapsViewModel
import com.project.vetpet.view.veterinarian.VeterinarianViewModel

class ViewModelFactory(
    private val app: App
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            MainActivityViewModel::class.java -> { MainActivityViewModel(app.userService, app.veterinarianService,app.scheduleService) }
            MainFragmentViewModel::class.java -> { MainFragmentViewModel(app.veterinarianService, app.clinicsService) }
            FindVeterinarianViewModel::class.java -> { FindVeterinarianViewModel(app.veterinarianService) }
            FindClinicsViewModel::class.java -> { FindClinicsViewModel(app.clinicsService) }
            ClinicViewModel::class.java -> { ClinicViewModel() }
            MapsViewModel::class.java -> { MapsViewModel() }
            VeterinarianViewModel::class.java -> { VeterinarianViewModel(app.veterinarianService) }
            ScheduleFragmentViewModel::class.java -> { ScheduleFragmentViewModel(app.scheduleService) }
            MyAppointmentViewModel::class.java -> { MyAppointmentViewModel() }
            AuthViewModel::class.java -> { AuthViewModel(app.userService) }
            AccountViewModel::class.java -> { AccountViewModel(app.userService,app.scheduleService, app.clinicsService) }
            RegisterViewModel::class.java -> { RegisterViewModel(app.userService) }
            MyPetsViewModel::class.java -> { MyPetsViewModel(app.petService) }
            AddPetViewModel::class.java -> { AddPetViewModel(app.petService) }
            EditPetViewModel::class.java -> { EditPetViewModel(app.petService) }
            EditAccountViewModel::class.java -> { EditAccountViewModel(app.userService, app.petService) }
            else -> {
                throw IllegalStateException("Unknown view model class")
            }
        }
        return viewModel as T
    }

}

fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as App)
fun Activity.factory() = ViewModelFactory(applicationContext.applicationContext as App)
