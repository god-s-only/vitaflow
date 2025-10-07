package com.vitaflow.app.domain.usecase

import com.vitaflow.app.common.Resource
import com.vitaflow.app.data.local.VitaFlowSession
import com.vitaflow.app.domain.models.User
import com.vitaflow.app.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val vitaFlowSession: VitaFlowSession
) {
    suspend operator fun invoke(email: String, password: String): Resource<User> {
        return try {
            // Validate input
            if (email.isBlank()) {
                return Resource.Error("Email cannot be empty")
            }

            if (password.isBlank()) {
                return Resource.Error("Password cannot be empty")
            }

            if (!isValidEmail(email)) {
                return Resource.Error("Please enter a valid email address")
            }

            if (password.length < 6) {
                return Resource.Error("Password must be at least 6 characters long")
            }

            // Attempt sign in
            val result = authRepository.signInWithEmailAndPassword(email, password)

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