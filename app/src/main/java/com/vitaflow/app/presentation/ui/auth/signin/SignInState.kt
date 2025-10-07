package com.vitaflow.app.presentation.ui.auth.signin

import com.vitaflow.app.domain.models.User

data class SignInState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isSignInSuccessful: Boolean = false
)