package com.example.taxi_app.data.api

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val user: UserResponse?,
    val token: String?,
    val message: String?
)

data class LogoutResponse(
    val message: String?
)