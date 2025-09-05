package com.example.taxi_app.data

data class Company(
    val id: String,
    val name: String,
    val vehiclesCount: Int = 0,
    val tripsCount: Int = 0,
    val pendingRequests: Int = 0,
    val owner: User? = null
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String, // "driver", "dispatcher", "owner", "client"
    val phone: String = "",
    val profileImage: String = "",
    val isVerified: Boolean = false,
    val rating: Float = 0f,
    val totalTrips: Int = 0
)

data class Vehicle(
    val id: String,
    val brand: String,
    val model: String,
    val plate: String,
    val color: String,
    val seats: Int,
    val userId: String? = null,
    val isAvailable: Boolean = true
)

data class Trip(
    val id: String,
    val vehicleId: String,
    val assignedDriverId: String,
    val fromAddr: String,
    val toAddr: String,
    val fromLat: Double,
    val fromLng: Double,
    val toLat: Double,
    val toLng: Double,
    val priceAmd: Int,
    val seatsTotal: Int,
    val seatsTaken: Int = 0,
    val departureAt: String? = null,
    val payMethods: List<String> = listOf("cash"),
    val status: String = "draft", // "draft", "published", "archived", "active", "completed"
    val vehicle: Vehicle? = null,
    val driver: User? = null,
    val pendingRequestsCount: Int = 0,
    val acceptedRequestsCount: Int = 0,
    val distance: String = "",
    val duration: String = ""
)

data class Request(
    val id: String,
    val tripId: String,
    val userId: String,
    val seats: Int,
    val payment: String, // "cash", "card"
    val status: String = "pending", // "pending", "accepted", "declined", "completed"
    val trip: Trip,
    val user: User,
    val createdAt: String = "",
    val notes: String = ""
)

data class Booking(
    val id: String,
    val userId: String,
    val tripId: String,
    val seats: Int,
    val totalPrice: Int,
    val paymentMethod: String,
    val status: String, // "confirmed", "in_progress", "completed", "cancelled"
    val pickupLocation: LocationData,
    val dropoffLocation: LocationData,
    val trip: Trip,
    val driver: User,
    val createdAt: String,
    val completedAt: String? = null,
    val bookingDate: String = createdAt,
    val notes: String? = null
)

data class LocationData(
    val address: String,
    val latitude: Double,
    val longitude: Double
)

data class DriverStats(
    val totalEarnings: Int,
    val totalTrips: Int,
    val rating: Float,
    val todayEarnings: Int,
    val todayTrips: Int,
    val pendingTrips: Int,
    val isAvailable: Boolean = true,
    val hoursOnline: Int = 8,
    val tripsToday: Int = todayTrips,
    val weeklyEarnings: Int = todayEarnings * 5,
    val activeDaysThisWeek: Int = 5,
    val monthlyEarnings: Int = todayEarnings * 22,
    val tripsCompleted: Int = totalTrips
)

data class ClientProfile(
    val user: User,
    val favoriteLocations: List<LocationData>,
    val recentTrips: List<Booking>,
    val paymentMethods: List<PaymentMethod>
)

data class PaymentMethod(
    val id: String,
    val type: String, // "card", "cash", "digital_wallet"
    val name: String,
    val isDefault: Boolean
)

// Navigation destinations
sealed class Screen(val route: String) {
    // Company screens
    object CompanyLogin : Screen("company_login")
    object CompanyRegister : Screen("company_register")
    object Dashboard : Screen("dashboard")
    object Fleet : Screen("fleet")
    object Members : Screen("members")
    object Trips : Screen("trips")
    object Requests : Screen("requests")
    
    // Client screens
    object ClientLogin : Screen("client_login")
    object ClientRegister : Screen("client_register")
    object ClientHome : Screen("client_home")
    object ClientBooking : Screen("client_booking")
    object ClientTracking : Screen("client_tracking")
    object ClientHistory : Screen("client_history")
    object ClientProfile : Screen("client_profile")
    
    // Driver screens
    object DriverLogin : Screen("driver_login")
    object DriverRegister : Screen("driver_register")
    object DriverDashboard : Screen("driver_dashboard")
    object DriverTrips : Screen("driver_trips")
    object DriverEarnings : Screen("driver_earnings")
    object DriverProfile : Screen("driver_profile")
}

// App modes
enum class AppMode {
    COMPANY,
    CLIENT,
    DRIVER
}
