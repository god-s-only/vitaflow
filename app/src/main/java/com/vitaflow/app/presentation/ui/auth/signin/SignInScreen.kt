package com.vitaflow.app.presentation.ui.auth.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.vitaflow.app.R

@Composable
fun SignInScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // Logo
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFF00C853), RoundedCornerShape(16.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "V",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00C853)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Title
        Text(
            text = "Sign in your account",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Email Field
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Email",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text(
                        text = "ex: jon.smith@email.com",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedBorderColor = Color(0xFF00C853),
                    unfocusedContainerColor = Color(0xFFF8F9FA),
                    focusedContainerColor = Color(0xFFF8F9FA)
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
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text(
                        text = "••••••••••",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedBorderColor = Color(0xFF00C853),
                    unfocusedContainerColor = Color(0xFFF8F9FA),
                    focusedContainerColor = Color(0xFFF8F9FA)
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    if (passwordVisible)
                        Image(
                            painter = painterResource(id = R.drawable.protected_eye),
                            contentDescription = null,
                            modifier = Modifier
                                .size(25.dp)
                                .clickable{ passwordVisible = !passwordVisible }
                        )
                    else
                        Image(
                            painter = painterResource(id = R.drawable.eye),
                            contentDescription = null,
                            modifier = Modifier
                                .size(25.dp)
                                .clickable{ passwordVisible = !passwordVisible }
                        )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Sign In Button
        Button(
            onClick = { /* Handle sign in */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00C853)
            )
        ) {
            Text(
                text = "SIGN IN",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Or sign in with
        Text(
            text = "or sign in with",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Social Login Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Google
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable { /* Handle Google sign in */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "G",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4285F4)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Sign Up Link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account? ",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "SIGN UP",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF00C853),
                modifier = Modifier.clickable { /* Handle sign up navigation */ }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    SignInScreen()
}