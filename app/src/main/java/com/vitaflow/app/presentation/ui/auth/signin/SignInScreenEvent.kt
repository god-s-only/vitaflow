package com.vitaflow.app.presentation.ui.auth.signin

sealed class SignInScreenEvent {
    data class OnSignInButtonClicked(val email: String, val password: String) : SignInScreenEvent()
    object OnSignUpButtonClicked : SignInScreenEvent()
    data class OnEmailChanged(val email: String) : SignInScreenEvent()
    data class OnPasswordChanged(val password: String) : SignInScreenEvent()
    data object OnPasswordVisibilityChanged : SignInScreenEvent()
}