package com.vitaflow.app.presentation.ui.auth.signin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.common.Resource
import com.vitaflow.app.common.Routes
import com.vitaflow.app.common.Routes.SIGNUPSCREEN
import com.vitaflow.app.common.UIEvent
import com.vitaflow.app.domain.usecase.auth.SignInUseCase
import com.vitaflow.app.presentation.ui.auth.signin.SignInScreenEvent
import com.vitaflow.app.presentation.ui.auth.signin.SignInState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    var email = mutableStateOf("")
        private set
    var password = mutableStateOf("")
        private set
    var isPasswordVisible = mutableStateOf(false)
        private set

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEvent(event: SignInScreenEvent) {
        when (event) {
            is SignInScreenEvent.OnSignUpButtonClicked -> {
                emitUIEvent(UIEvent.Navigate(SIGNUPSCREEN))
            }

            is SignInScreenEvent.OnSignInButtonClicked -> {
                signIn()
            }

            is SignInScreenEvent.OnPasswordChanged -> {
                password.value = event.password
                clearError()
            }

            is SignInScreenEvent.OnEmailChanged -> {
                email.value = event.email
                clearError()
            }

            is SignInScreenEvent.OnPasswordVisibilityChanged -> {
                isPasswordVisible.value = !isPasswordVisible.value
            }
        }
    }

    private fun signIn() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )

            when (val result = signInUseCase(email.value.trim(), password.value)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        user = result.data,
                        isSignInSuccessful = true,
                        error = null
                    )
                    emitUIEvent(UIEvent.ShowSnackBar("Sign in successful!"))
                    emitUIEvent(UIEvent.Navigate(Routes.HOMESCREEN))
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message,
                        isSignInSuccessful = false
                    )
                    emitUIEvent(UIEvent.ShowSnackBar(result.message ?: "Sign in failed"))
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
