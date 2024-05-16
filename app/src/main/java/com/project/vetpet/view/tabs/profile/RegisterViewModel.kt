package com.project.vetpet.view.tabs.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.vetpet.model.User

import com.project.vetpet.model.service.UserService
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val firebaseService: UserService
): ViewModel() {

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


    fun registerUser(email: String, password:String, callback: () -> Unit){
        viewModelScope.launch {
            async {
                firebaseService.writeNewUser(createUser(email,password))
            }.await()
            delay(2000)
            callback()
        }
    }

    private fun createUser( email: String, password:String): User{
        return User(email,password,"Ім'я користувача", null,null)
    }
}