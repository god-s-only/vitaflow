package com.vitaflow.app.presentation.ui.auth.signin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.common.Routes.SIGNUPSCREEN
import com.vitaflow.app.common.UIEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SignInViewModel: ViewModel() {
    var email = mutableStateOf("")
        private set
    var password = mutableStateOf("")
        private set

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEvent(event: SignInScreenEvent) {
        when (event) {
            is SignInScreenEvent.OnSignUpButtonClicked -> {
                emitUIEvent(UIEvent.Navigate(SIGNUPSCREEN))
            }

            is SignInScreenEvent.OnSignInButtonClicked -> {

            }

            is SignInScreenEvent.OnPasswordChanged -> {
                password.value = event.password
            }

            is SignInScreenEvent.OnEmailChanged -> {
                email.value = event.email
            }
        }
    }

    private fun emitUIEvent(event: UIEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}
