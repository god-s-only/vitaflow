package com.vitaflow.app.presentation.ui.auth.signup

sealed class SignUpScreenEvent {
    data class OnSignUpButtonClicked(
        val name: String,
        val email: String,
        val password: String,
        val confirmPassword: String
    ) : SignUpScreenEvent()

    object OnSignInButtonClicked : SignUpScreenEvent()
    data class OnNameChanged(val name: String) : SignUpScreenEvent()
    data class OnEmailChanged(val email: String) : SignUpScreenEvent()
    data class OnPasswordChanged(val password: String) : SignUpScreenEvent()
    data class OnConfirmPasswordChanged(val confirmPassword: String) : SignUpScreenEvent()
    data object OnPasswordVisibilityChanged : SignUpScreenEvent()
    data object OnConfirmPasswordVisibilityChanged : SignUpScreenEvent()
    data class OnTermsAcceptedChanged(val accepted: Boolean) : SignUpScreenEvent()
}