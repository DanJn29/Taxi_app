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
    
    @GET("driver/vehicle")
    suspend fun getDriverVehicle(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String = "application/json"
    ): Response<VehicleResponse>
    
    @GET("driver/trips")
    suspend fun getDriverTrips(
        @Header("Authorization") token: String,
        @Query("status") status: String = "published",
        @Query("per_page") perPage: Int = 20
    ): Response<DriverTripsResponse>
    
    @GET("driver/trips/{tripId}")
    suspend fun getDriverTripById(
        @Header("Authorization") token: String,
        @Path("tripId") tripId: Int
    ): Response<DriverTripDetailsResponse>
    
    @GET("driver/requests")
    suspend fun getDriverRequests(
        @Header("Authorization") token: String,
        @Query("status") status: String = "pending",
        @Query("per_page") perPage: Int = 20
    ): Response<DriverRequestsResponse>
    
    @POST("driver/requests/{requestId}/accept")
    suspend fun acceptDriverRequest(
        @Header("Authorization") token: String,
        @Path("requestId") requestId: String
    ): Response<RequestActionResponse>
    
    @POST("driver/requests/{requestId}/reject")
    suspend fun rejectDriverRequest(
        @Header("Authorization") token: String,
        @Path("requestId") requestId: String
    ): Response<RequestActionResponse>
    
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
    
    // Get amenities list
    @GET("amenities")
    suspend fun getAmenities(
        @Header("Authorization") token: String
    ): Response<AmenitiesResponse>
    
    // Create new trip
    @POST("driver/trips")
    suspend fun createTrip(
        @Header("Authorization") token: String,
        @Body request: com.example.taxi_app.data.CreateTripRequest
    ): Response<com.example.taxi_app.data.CreateTripResponse>
}
