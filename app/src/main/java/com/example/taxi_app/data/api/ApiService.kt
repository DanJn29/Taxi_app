package com.example.taxi_app.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("register/client")
    suspend fun registerClient(@Body request: RegistrationRequest): Response<RegistrationResponse>

    @POST("register/driver")
    suspend fun registerDriver(@Body request: RegistrationRequest): Response<RegistrationResponse>

    @POST("register/company")
    suspend fun registerCompany(@Body request: CompanyRegistrationRequest): Response<RegistrationResponse>

    @POST("login")
    suspend fun loginClient(@Body request: LoginRequest): Response<LoginResponse>

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<LogoutResponse>
    
    @GET("trips")
    suspend fun getTrips(@Header("Authorization") token: String): Response<TripsResponse>
    
    @POST("trips/{trip}/requests")
    suspend fun bookTrip(
        @Header("Authorization") token: String,
        @Path("trip") tripId: String,
        @Body request: BookingRequest
    ): Response<BookingResponse>
    
    @GET("my/requests")
    suspend fun getMyRequests(
        @Header("Authorization") token: String,
        @Query("per_page") perPage: Int = 20
    ): Response<RequestsResponse>
    
    @Multipart
    @POST("driver/vehicle")
    suspend fun registerVehicle(
        @Header("Authorization") token: String,
        @Part("brand") brand: RequestBody,
        @Part("model") model: RequestBody,
        @Part("seats") seats: RequestBody,
        @Part("color") color: RequestBody,
        @Part("plate") plate: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Response<VehicleResponse>
}
