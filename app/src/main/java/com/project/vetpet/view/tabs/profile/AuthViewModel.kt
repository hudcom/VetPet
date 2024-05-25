package com.project.vetpet.view.tabs.profile

import androidx.lifecycle.viewModelScope
import com.project.vetpet.model.User
import com.project.vetpet.model.service.UserService
import com.project.vetpet.utils.ToastNotifier
import com.project.vetpet.utils.TypeLogs
import com.project.vetpet.view.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(
    private val userService: UserService,
): BaseViewModel() {

    var toastNotifier: ToastNotifier? = null

    fun hasCurrentUser():Boolean{
        return User.hasCurrentUser()
    }


    fun loginUser(email: String, password: String, callback: () -> Unit) {
        viewModelScope.launch {
            val isAuthenticated = withContext(Dispatchers.IO) { userService.authUser(email, password) }
            if (isAuthenticated) {
                val isEmailVerified = withContext(Dispatchers.IO) { userService.checkEmailVerification() }
                if (!isEmailVerified) {
                    withContext(Dispatchers.IO) { userService.sendEmailVerification() }
                }
                withContext(Dispatchers.IO) {
                    val user = userService.readUserFromDb(email)
                    if (user != null) {
                        User.currentUser = user
                    }
                }
            }
            callback()
        }
    }

    private fun showToast(message:String) {
        // Виклик інтерфейсу для показу Toast
        toastNotifier?.showToast(message)
    }

    private fun writeLogs(message: String){
        toastNotifier?.writeLogs(message)
    }
}