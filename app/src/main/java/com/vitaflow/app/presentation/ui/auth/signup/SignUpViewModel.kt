package com.vitaflow.app.presentation.ui.auth.signup

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SignUpViewModel: ViewModel() {
    var name = mutableStateOf("")
        private set
    var email = mutableStateOf("")
        private set
    var confirmPassword = mutableStateOf("")
        private set

}