package com.vitaflow.app.domain.models

data class User(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean = false
)