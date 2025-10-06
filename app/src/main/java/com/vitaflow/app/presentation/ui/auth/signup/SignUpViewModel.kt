package com.vitaflow.app.presentation.ui.auth.signup

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(): ViewModel() {
    var name = mutableStateOf("")
        private set
    var email = mutableStateOf("")
        private set
    var confirmPassword = mutableStateOf("")
        private set

}