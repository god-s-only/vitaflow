package com.vitaflow.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.vitaflow.app.common.Resource
import com.vitaflow.app.data.local.VitaFlowSession
import com.vitaflow.app.domain.models.User
import com.vitaflow.app.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val vitaFlowSession: VitaFlowSession
) : AuthRepository {

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Resource<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                Resource.Success(firebaseUser.toUser())
            } else {
                Resource.Error("Sign in failed. Please try again.")
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            Resource.Error("No account found with this email address.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Resource.Error("Invalid email or password.")
        } catch (e: Exception) {
            Resource.Error("Sign in failed: ${e.message}")
        }
    }

    override suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String
    ): Resource<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                Resource.Success(firebaseUser.toUser())
            } else {
                Resource.Error("Account creation failed. Please try again.")
            }
        } catch (e: FirebaseAuthWeakPasswordException) {
            Resource.Error("Password is too weak. Please choose a stronger password.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Resource.Error("Invalid email format.")
        } catch (e: FirebaseAuthUserCollisionException) {
            Resource.Error("An account with this email already exists.")
        } catch (e: Exception) {
            Resource.Error("Account creation failed: ${e.message}")
        }
    }

    override suspend fun signOut(): Resource<Unit> {
        return try {
            firebaseAuth.signOut()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Sign out failed: ${e.message}")
        }
    }

    override suspend fun getCurrentUser(): User? {
        return firebaseAuth.currentUser?.toUser()
    }

    override suspend fun sendPasswordResetEmail(email: String): Resource<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Resource.Success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {
            Resource.Error("No account found with this email address.")
        } catch (e: Exception) {
            Resource.Error("Failed to send password reset email: ${e.message}")
        }
    }

    override suspend fun getToken(): String {
        return withContext(Dispatchers.Default){
            vitaFlowSession.getToken() ?: ""
        }
    }

    private fun FirebaseUser.toUser(): User {
        return User(
            uid = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoUrl?.toString(),
            isEmailVerified = isEmailVerified
        )
    }
}