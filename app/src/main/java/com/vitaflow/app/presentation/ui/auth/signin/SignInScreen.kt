package com.vitaflow.app.presentation.ui.auth.signin

import com.vitaflow.app.presentation.ui.auth.AuthBrandColor
import com.vitaflow.app.presentation.ui.auth.AuthFieldBackground
import com.vitaflow.app.presentation.ui.auth.AuthOnBrandColor
import com.vitaflow.app.presentation.ui.auth.AuthTextPrimary
import com.vitaflow.app.presentation.ui.auth.AuthTextSecondary
import com.vitaflow.app.presentation.ui.auth.AuthTextFieldBorder
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.vitaflow.app.R
import com.vitaflow.app.common.UIEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { result ->
            when(result){
                is UIEvent.Navigate -> {
                    navController.navigate(result.route)
                }
                is UIEvent.ShowSnackBar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(message = result.message)
                    }
                }

                UIEvent.PopBackStack -> TODO()
                is UIEvent.ShowQuantityDialog -> TODO()
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, AuthBrandColor, RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "V",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = AuthBrandColor
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Title
            Text(
                text = "Sign in your account",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = AuthTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Email Field
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Email",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AuthTextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = viewModel.email.value,
                        onValueChange = { viewModel.onEvent(SignInScreenEvent.OnEmailChanged(it)) },
                        placeholder = {
                            Text(
                                text = "ex: jon.smith@email.com",
                                color = AuthTextSecondary,
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = AuthTextFieldBorder,
                            focusedBorderColor = AuthBrandColor,
                            unfocusedContainerColor = AuthFieldBackground,
                            focusedContainerColor = AuthFieldBackground,
                            focusedTextColor = AuthTextPrimary,
                            unfocusedTextColor = AuthTextPrimary
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                }

                Spacer(modifier = Modifier.height(24.dp))

                // Password Field
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Password",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AuthTextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = viewModel.password.value,
                        onValueChange = { viewModel.onEvent(SignInScreenEvent.OnPasswordChanged(it)) },
                        placeholder = {
                            Text(
                                text = "••••••••••",
                                color = AuthTextSecondary,
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = AuthTextFieldBorder,
                            focusedBorderColor = AuthBrandColor,
                            unfocusedContainerColor = AuthFieldBackground,
                            focusedContainerColor = AuthFieldBackground,
                            focusedTextColor = AuthTextPrimary,
                            unfocusedTextColor = AuthTextPrimary
                        ),
                        visualTransformation = if (viewModel.isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            if (viewModel.isPasswordVisible.value)
                                Image(
                                    painter = painterResource(id = R.drawable.protected_eye),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(25.dp)
                                        .clickable {
                                            viewModel.isPasswordVisible.value =
                                                !viewModel.isPasswordVisible.value
                                        }
                                )
                            else
                                Image(
                                    painter = painterResource(id = R.drawable.eye),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(25.dp)
                                        .clickable {
                                            viewModel.isPasswordVisible.value =
                                                !viewModel.isPasswordVisible.value
                                        }
                                )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Sign In Button
                Button(
                    onClick = {
                        viewModel.onEvent(
                            SignInScreenEvent.OnSignInButtonClicked(
                                email = viewModel.email.value,
                                password = viewModel.password.value
                            )
                        )
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AuthBrandColor
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = AuthBrandColor,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "SIGN IN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AuthOnBrandColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Or sign in with
                Text(
                    text = "or sign in with",
                    fontSize = 14.sp,
                    color = AuthTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Social Login Buttons
                Button(
                    onClick = {},
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Sign in with Google",
                            fontSize = 16.sp,
                            color = AuthTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sign Up Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account? ",
                        fontSize = 14.sp,
                        color = AuthTextSecondary
                    )
                    Text(
                        text = "SIGN UP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AuthBrandColor,
                        modifier = Modifier.clickable {
                            viewModel.onEvent(SignInScreenEvent.OnSignUpButtonClicked)
                        }
                    )
                }
            }
        }
    }

}

@Preview
@Composable
private fun Preview() {
    SignInScreen(rememberNavController())
}