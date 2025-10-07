package com.vitaflow.app.presentation.ui.auth.signup

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.common.Resource
import com.vitaflow.app.common.Routes
import com.vitaflow.app.common.UIEvent
import com.vitaflow.app.domain.usecase.auth.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    var name = mutableStateOf("")
        private set
    var email = mutableStateOf("")
        private set
    var password = mutableStateOf("")
        private set
    var confirmPassword = mutableStateOf("")
        private set
    var isPasswordVisible = mutableStateOf(false)
        private set
    var isConfirmPasswordVisible = mutableStateOf(false)
        private set
    var termsAccepted = mutableStateOf(false)
        private set

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEvent(event: SignUpScreenEvent) {
        when (event) {
            is SignUpScreenEvent.OnSignInButtonClicked -> {
                emitUIEvent(UIEvent.Navigate(Routes.SIGNINSCREEN))
            }

            is SignUpScreenEvent.OnSignUpButtonClicked -> {
                signUp()
            }

            is SignUpScreenEvent.OnNameChanged -> {
                name.value = event.name
                clearError()
            }

            is SignUpScreenEvent.OnEmailChanged -> {
                email.value = event.email
                clearError()
            }

            is SignUpScreenEvent.OnPasswordChanged -> {
                password.value = event.password
                clearError()
            }

            is SignUpScreenEvent.OnConfirmPasswordChanged -> {
                confirmPassword.value = event.confirmPassword
                clearError()
            }

            is SignUpScreenEvent.OnPasswordVisibilityChanged -> {
                isPasswordVisible.value = !isPasswordVisible.value
            }

            is SignUpScreenEvent.OnConfirmPasswordVisibilityChanged -> {
                isConfirmPasswordVisible.value = !isConfirmPasswordVisible.value
            }

            is SignUpScreenEvent.OnTermsAcceptedChanged -> {
                termsAccepted.value = event.accepted
            }
        }
    }

    private fun signUp() {
        if (!termsAccepted.value) {
            emitUIEvent(UIEvent.ShowSnackBar("Please accept the terms and conditions"))
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )

            when (val result = signUpUseCase(
                name.value.trim(),
                email.value.trim(),
                password.value,
                confirmPassword.value
            )) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        user = result.data,
                        isSignUpSuccessful = true,
                        error = null
                    )
                    emitUIEvent(UIEvent.ShowSnackBar("Account created successfully!"))
                    emitUIEvent(UIEvent.Navigate(Routes.SIGNINSCREEN))
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message,
                        isSignUpSuccessful = false
                    )
                    emitUIEvent(UIEvent.ShowSnackBar(result.message ?: "Sign up failed"))
                }

                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        isLoading = true,
                        error = null
                    )
                }
            }
        }
    }

    private fun clearError() {
        if (_state.value.error != null) {
            _state.value = _state.value.copy(error = null)
        }
    }

    private fun emitUIEvent(event: UIEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}