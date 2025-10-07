package com.vitaflow.app.domain.usecase.auth

import com.vitaflow.app.common.Resource
import com.vitaflow.app.data.local.VitaFlowSession
import com.vitaflow.app.domain.models.User
import com.vitaflow.app.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val vitaFlowSession: VitaFlowSession
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Resource<User> {
        return try {
            // Validate input
            if (name.isBlank()) {
                return Resource.Error("Name cannot be empty")
            }

            if (email.isBlank()) {
                return Resource.Error("Email cannot be empty")
            }

            if (password.isBlank()) {
                return Resource.Error("Password cannot be empty")
            }

            if (confirmPassword.isBlank()) {
                return Resource.Error("Please confirm your password")
            }

            if (!isValidEmail(email)) {
                return Resource.Error("Please enter a valid email address")
            }

            if (password.length < 6) {
                return Resource.Error("Password must be at least 6 characters long")
            }

            if (password != confirmPassword) {
                return Resource.Error("Passwords do not match")
            }

            if (name.length < 2) {
                return Resource.Error("Name must be at least 2 characters long")
            }

            // Attempt sign up
            val result = authRepository.signUpWithEmailAndPassword(email, password)

            if (result is Resource.Success && result.data != null) {
                // Store user session
                vitaFlowSession.storeToken(result.data.uid)
            }

            result
        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred: ${e.message}")
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}