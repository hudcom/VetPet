package com.project.vetpet.view

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.vetpet.App
import com.project.vetpet.view.tabs.profile.AccountViewModel
import com.project.vetpet.view.pets.AddPetViewModel
import com.project.vetpet.view.pets.EditPetViewModel
import com.project.vetpet.view.tabs.profile.AuthViewModel
import com.project.vetpet.view.pets.MyPetsViewModel
import com.project.vetpet.view.tabs.profile.RegisterViewModel

class ViewModelFactory(
    private val app: App
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            MainActivityViewModel::class.java -> { MainActivityViewModel(app.userService) }
            AuthViewModel::class.java -> { AuthViewModel(app.userService) }
            AccountViewModel::class.java -> { AccountViewModel(app.userService) }
            RegisterViewModel::class.java -> { RegisterViewModel(app.userService) }
            MyPetsViewModel::class.java -> { MyPetsViewModel(app.petService) }
            AddPetViewModel::class.java -> { AddPetViewModel(app.petService) }
            EditPetViewModel::class.java -> { EditPetViewModel(app.petService) }
            else -> {
                throw IllegalStateException("Unknown view model class")
            }
        }
        return viewModel as T
    }

}

fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as App)
fun Activity.factory() = ViewModelFactory(applicationContext.applicationContext as App)
