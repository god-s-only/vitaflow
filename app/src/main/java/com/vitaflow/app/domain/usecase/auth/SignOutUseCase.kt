package com.vitaflow.app.domain.usecase.auth

import com.vitaflow.app.common.Resource
import com.vitaflow.app.data.local.VitaFlowSession
import com.vitaflow.app.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val vitaFlowSession: VitaFlowSession
) {
    suspend operator fun invoke(): Resource<Unit> {
        return try {
            val result = authRepository.signOut()
            if (result is Resource.Success) {
                vitaFlowSession.clearAll()
            }
            result
        } catch (e: Exception) {
            Resource.Error("Sign out failed: ${e.message}")
        }
    }
}