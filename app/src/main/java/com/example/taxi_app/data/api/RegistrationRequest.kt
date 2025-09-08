//package com.example.taxi_app.data.api
//
//data class RegistrationRequest(
//    val name: String,
//    val email: String,
//    val password: String,
//    val password_confirmation: String
//)
//
//data class RegistrationResponse(
//    val message: String? = null,
//    val user: ApiUser? = null,
//    val token: String? = null,
//    val errors: Map<String, List<String>>? = null
//)
//
//data class ApiUser(
//    val id: Int,
//    val name: String,
//    val email: String,
//    val email_verified_at: String? = null,
//    val created_at: String,
//    val updated_at: String
//)
//
//data class ApiError(
//    val message: String,
//    val errors: Map<String, List<String>>? = null
//)

package com.example.taxi_app.data.api

data class RegistrationRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)

data class CompanyRegistrationRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String,
    val company_name: String
)

data class RegistrationResponse(
    val user: UserResponse?,
    val message: String?,
    val token: String? = null // Optional token for auto-login after registration
)

data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val email_verified_at: String?
)

// Trips API Response Models
data class TripsResponse(
    val data: List<TripData>,
    val meta: TripMeta,
    val links: TripLinks
)

data class TripData(
    val id: Int,
    val from_addr: String,
    val to_addr: String,
    val from_lat: Double,
    val from_lng: Double,
    val to_lat: Double,
    val to_lng: Double,
    val departure_at: String,
    val price_amd: Int,
    val seats_total: Int,
    val seats_taken: Int,
    val pending_requests_count: Int,
    val vehicle: VehicleData,
    val driver: DriverData?,
    val pay_methods: List<String>
)

data class VehicleData(
    val id: Int,
    val brand: String,
    val model: String,
    val plate: String,
    val color: String,
    val seats: Int,
    val photo: String? = null // URL to the vehicle photo
)

data class DriverData(
    val name: String,
    val id: Int
)

data class TripMeta(
    val current_page: Int,
    val last_page: Int,
    val per_page: Int,
    val total: Int
)

data class TripLinks(
    val first: String?,
    val last: String?,
    val prev: String?,
    val next: String?
)

// Vehicle Registration Response
data class VehicleResponse(
    val message: String? = null,
    val vehicle: VehicleData? = null,
    val errors: Map<String, List<String>>? = null,
    val success: Boolean? = null,
    val status: String? = null,
    val data: VehicleData? = null // Some APIs return data instead of vehicle
)

// Booking Request and Response
data class BookingRequest(
    val description: String,
    val seats: Int,
    val payment: String // "cash" or "card"
)

data class BookingResponse(
    val message: String? = null,
    val request: BookingData? = null,
    val errors: Map<String, List<String>>? = null,
    val success: Boolean? = null,
    val status: String? = null,
    val data: BookingData? = null
)

data class BookingData(
    val id: String,
    val trip_id: String,
    val user_id: String,
    val description: String,
    val seats: Int,
    val payment: String,
    val status: String = "pending",
    val created_at: String? = null
)

// Requests API Response Models
data class RequestsResponse(
    val data: List<RequestData>,
    val meta: RequestsMeta,
    val links: RequestsLinks
)

data class RequestData(
    val id: Int,
    val status: String,
    val payment: String,
    val seats: Int,
    val passenger_name: String,
    val phone: String,
    val trip: RequestTripData
)

data class RequestTripData(
    val id: Int,
    val from_addr: String,
    val to_addr: String,
    val departure_at: String,
    val price_amd: Int,
    val driver: String
)

data class RequestsMeta(
    val current_page: Int,
    val last_page: Int,
    val per_page: Int,
    val total: Int
)

data class RequestsLinks(
    val first: String?,
    val last: String?,
    val prev: String?,
    val next: String?
)

// Driver Trips API Response Models
data class DriverTripsResponse(
    val data: List<DriverTripData>,
    val meta: TripMeta,
    val links: TripLinks
)

data class DriverTripData(
    val id: Int,
    val from_addr: String,
    val to_addr: String,
    val from_lat: Double,
    val from_lng: Double,
    val to_lat: Double,
    val to_lng: Double,
    val departure_at: String,
    val price_amd: Int,
    val seats_total: Int,
    val seats_taken: Int,
    val status: String,
    val pay_methods: List<String>,
    val pending_requests_count: Int
)

// Driver Requests API Response Models
data class DriverRequestsResponse(
    val data: List<DriverRequestData>,
    val meta: RequestMeta,
    val links: RequestLinks
)

data class DriverRequestData(
    val id: Int,
    val status: String,
    val payment: String,
    val seats: Int,
    val passenger_name: String,
    val phone: String,
    val decided_by_user_id: Int?,
    val decided_at: String?,
    val trip: TripRequestData?
)

data class ClientData(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String?,
    val rating: Float?
)

data class TripRequestData(
    val id: Int,
    val from_addr: String,
    val to_addr: String,
    val departure_at: String,
    val price_amd: Int,
    val seats_requested: Int
)

data class RequestMeta(
    val current_page: Int,
    val from: Int?,
    val last_page: Int,
    val per_page: Int,
    val to: Int?,
    val total: Int
)

data class RequestLinks(
    val first: String?,
    val last: String?,
    val prev: String?,
    val next: String?
)

data class RequestActionResponse(
    val message: String
)

// Amenities API response
data class AmenitiesResponse(
    val data: List<com.example.taxi_app.data.Amenity>,
    val message: String? = null
)
