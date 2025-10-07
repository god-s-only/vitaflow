package com.vitaflow.app.presentation.ui.auth.signup

import com.vitaflow.app.domain.models.User

data class SignUpState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isSignUpSuccessful: Boolean = false
)