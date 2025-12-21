package com.vitaflow.app.domain.usecase.auth

import com.vitaflow.app.data.local.VitaFlowSession
import com.vitaflow.app.domain.repository.AuthRepository
import javax.inject.Inject

class GetTokenUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): String{
        return authRepository.getToken()
    }
}