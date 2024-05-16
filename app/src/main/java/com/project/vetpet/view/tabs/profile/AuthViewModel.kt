package com.project.vetpet.view.tabs.profile

import androidx.lifecycle.viewModelScope
import com.project.vetpet.model.User
import com.project.vetpet.model.service.UserService
import com.project.vetpet.view.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel(
    private val firebaseService: UserService,
): BaseViewModel() {

    fun hasCurrentUser():Boolean{
        return User.hasCurrentUser()
    }

    fun loginUser(email: String, password:String, callback: () -> Unit){
        //Створення корутини
        viewModelScope.launch {
            async {
                firebaseService.authUser(email,password)
            }.await()
            delay(2000)
            callback()
        }
    }
}