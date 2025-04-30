package com.example.healthcheck.viewmodel


import androidx.lifecycle.ViewModel
import com.example.healthcheck.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    fun register(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        authRepository.register(email, password, onResult)
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        authRepository.login(email, password, onResult)
    }

    fun getCurrentUserId(): String? = authRepository.getCurrentUserId()

    fun resetPassword(email: String, onResult: (Boolean, String?) -> Unit) {
        authRepository.resetPassword(email, onResult)
    }
}
