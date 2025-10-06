package com.vitaflow.app.presentation.ui.auth.signin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SignInViewModel: ViewModel() {
    var email = mutableStateOf("")
        private set
    var password = mutableStateOf("")
        private set


}