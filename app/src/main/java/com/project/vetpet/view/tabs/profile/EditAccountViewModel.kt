package com.project.vetpet.view.tabs.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.vetpet.model.User
import com.project.vetpet.model.service.PetService
import com.project.vetpet.model.service.Preferences
import com.project.vetpet.model.service.UserService
import com.project.vetpet.utils.ToastNotifier
import com.project.vetpet.view.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditAccountViewModel(
    private val userService: UserService,
    private val petService: PetService
): ViewModel() {

    private var user: User? = null

    var toastNotifier: ToastNotifier? = null

    fun convertBundle(bundle: Bundle) {
        user = User.fromBundle(bundle)
    }

    fun checkEmailVerification(): Boolean = userService.checkEmailVerification()

    fun deleteUserAccount(email: String, currentPassword: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val authResult = withContext(Dispatchers.IO) {
                userService.deleteUserFromAuthDb(currentPassword)
            }
            if (authResult) {
                val firestoreResult = withContext(Dispatchers.IO) {
                    userService.deleteUserFromFirestore(email)
                }
                if (firestoreResult) {
                    val petResult = withContext(Dispatchers.IO) {
                        petService.deleteAllPetsOwnedBy(email)
                    }
                    callback(petResult)
                } else{
                    callback(false)
                }
            } else {
                callback(false)
            }
            User.currentUser = null
            Preferences.deleteSharedPreferences()

            delay(1000)
        }
    }

    fun editElement(email: String, password: String, name: String, number: String, city: String, callback: () -> Unit): Boolean {

        // Перевірка чи змінені поля чи ні
        if ((user?.email == email) && (user?.password == password) && (user?.fullName == name) && (user?.number == number) && (user?.city == city)) {
            return false
        }
        // Оновлюємо інші дані про користувача
        val newUser = User(email, password, name, number, city)

        // Оновлення даних користувача
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userService.editUser(email = getEmail(), newUser)
            }
            showToast("Дані про аккаунт змінено")
            delay(1000)
        }

        User.currentUser = newUser
        saveSharedPreferences(newUser)
        callback()

        return true
    }

    fun changeEmailAndPassword(email:String, password: String): Boolean{
        if(getEmail() != email || getPassword() != password){
            // Перевірка відповідності email. Якщо email було змінено, то змінюємо записи 'Owner' у всіх домашніх улюбленцях та у таблиці авторизації змінюємо email
            if (userService.checkEmailVerification()) {
                viewModelScope.launch {
                    // Перевірка відповідності email
                    if (getEmail() != email) {
                        withContext(Dispatchers.IO) {
                            petService.editAllUsersPet(email = user!!.email, newEmail = email)
                            userService.editEmailCurrentUser(newEmail = email, currentPassword = user!!.password)
                        }
                    }

                    // Перевірка відповідності password
                    if (getPassword() != password) {
                        withContext(Dispatchers.IO) {
                            userService.editPasswordCurrentUser(newPassword = password, currentPassword = user!!.password)
                        }
                    }
                    delay(1000)
                }
                return true
            } else {
                Log.d(TAG, "Ваш email не верифіковано ви не можете змінити email або пароль")
            }
        }
        return false
    }

    private fun saveSharedPreferences(user:User){
        Preferences.saveSharedPreferences(user)
    }
    fun getEmail(): String = user?.email ?: ""
    fun getPassword(): String = user?.password ?: ""
    fun getName(): String = user?.fullName ?: ""
    fun getNumber():String = user?.number ?: ""
    fun getLocation():String = user?.city ?: ""

    private fun showToast(text:String) {
        // Виклик інтерфейсу для показу Toast
        toastNotifier?.showToast(text)
    }
}

