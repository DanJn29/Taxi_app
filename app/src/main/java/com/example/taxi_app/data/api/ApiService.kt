package com.example.taxi_app.data.api

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("register")
    suspend fun registerClient(@Body request: RegistrationRequest): Response<RegistrationResponse>

    @POST("register/driver")
    suspend fun registerDriver(@Body request: RegistrationRequest): Response<RegistrationResponse>

    @POST("register/company")
    suspend fun registerCompany(@Body request: CompanyRegistrationRequest): Response<RegistrationResponse>

    @POST("login")
    suspend fun loginClient(@Body request: LoginRequest): Response<LoginResponse>

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<LogoutResponse>
}
