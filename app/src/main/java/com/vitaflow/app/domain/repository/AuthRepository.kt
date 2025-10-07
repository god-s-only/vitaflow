package com.vitaflow.app.domain.repository

import com.vitaflow.app.common.Resource
import com.vitaflow.app.domain.models.User

interface AuthRepository {
    suspend fun signInWithEmailAndPassword(email: String, password: String): Resource<User>
    suspend fun signUpWithEmailAndPassword(email: String, password: String): Resource<User>
    suspend fun signOut(): Resource<Unit>
    suspend fun getCurrentUser(): User?
    suspend fun sendPasswordResetEmail(email: String): Resource<Unit>
}