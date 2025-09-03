package com.example.taxi_app.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("register/client/")
    suspend fun registerClient(@Body request: RegistrationRequest): Response<RegistrationResponse>

    @POST("login")
    suspend fun loginClient(@Body request: LoginRequest): Response<LoginResponse>

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<LogoutResponse>
}