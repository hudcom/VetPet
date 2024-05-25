package com.project.vetpet.view.tabs.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.vetpet.model.User

import com.project.vetpet.model.service.UserService
import com.project.vetpet.utils.ToastNotifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel(
    private val userService: UserService
): ViewModel() {

    var toastNotifier: ToastNotifier? = null

    fun registerUser(email: String, name: String, password: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val firestoreCheck = withContext(Dispatchers.IO) { userService.checkEmailInFirestore(email) }
            val authCheck = withContext(Dispatchers.IO) { userService.checkEmailInAuth(email) }

            if (!firestoreCheck && !authCheck) {
                withContext(Dispatchers.IO) {
                    userService.writeNewUser(createUser(email, name, password)){
                        User.currentUser = it
                    }
                }
                showToast("Користувач успішно зареєстрований")

                if (User.currentUser != null) {
                    val isEmailVerified = withContext(Dispatchers.IO) { userService.checkEmailVerification() }
                    if (isEmailVerified) showToast("Повідомлення відправлене")
                    else {
                        showToast("Повідомлення не відправлене")
                        withContext(Dispatchers.IO) { userService.sendEmailVerification() }
                    }
                    callback(true)
                } else {
                    showToast("Email або пароль не підходять, будь-ласка перевірте їх правильність")
                    callback(false)
                }
            } else {
                showToast("Користувач з такою поштою вже існує")
                callback(false)
            }
        }
    }

    private fun createUser( email: String, name:String, password:String): User{
        return User(email,password,name, null,null)
    }
    private fun showToast(text:String) {
        // Виклик інтерфейсу для показу Toast
        toastNotifier?.showToast(text)
    }
}