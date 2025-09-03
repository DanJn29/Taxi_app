package com.example.taxi_app.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxi_app.data.*
import com.example.taxi_app.data.api.NetworkModule
import com.example.taxi_app.data.api.LoginRequest
import com.example.taxi_app.data.api.LoginResponse
import com.example.taxi_app.data.api.LogoutResponse
import com.example.taxi_app.data.api.RegistrationRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

// Error response data class for parsing API errors
data class ErrorResponse(
    val message: String?
)

class TaxiViewModel : ViewModel() {

    // App Mode
    private val _appMode = MutableStateFlow<AppMode?>(null)
    val appMode: StateFlow<AppMode?> = _appMode.asStateFlow()

    // Current User
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Company Data
    private val _company = MutableStateFlow(
        Company(
            id = "1",
            name = "Taxi Yerevan",
            vehiclesCount = 0,
            tripsCount = 0,
            pendingRequests = 0,
            owner = User("1", "John Doe", "john@example.com", "owner")
        )
    )
    val company: StateFlow<Company> = _company.asStateFlow()

    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles: StateFlow<List<Vehicle>> = _vehicles.asStateFlow()

    private val _members = MutableStateFlow<List<User>>(emptyList())
    val members: StateFlow<List<User>> = _members.asStateFlow()

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    private val _requests = MutableStateFlow<List<Request>>(emptyList())
    val requests: StateFlow<List<Request>> = _requests.asStateFlow()

    // Map Location State
    private val _currentMapLocation = MutableStateFlow<GeoPoint?>(null)
    val currentMapLocation: StateFlow<GeoPoint?> = _currentMapLocation.asStateFlow()

    // Navigation Scroll State
    val navigationScrollState = LazyListState()

    // Loading and Error States
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // Authentication Token
    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    // Client Data
    private val _availableTrips = MutableStateFlow<List<Trip>>(emptyList())
    val availableTrips: StateFlow<List<Trip>> = _availableTrips.asStateFlow()

    private val _clientBookings = MutableStateFlow<List<Booking>>(emptyList())
    val clientBookings: StateFlow<List<Booking>> = _clientBookings.asStateFlow()

    // Driver Data
    private val _driverStats = MutableStateFlow(
        DriverStats(
            totalEarnings = 0,
            totalTrips = 0,
            rating = 0f,
            todayEarnings = 0,
            todayTrips = 0,
            pendingTrips = 0
        )
    )
    val driverStats: StateFlow<DriverStats> = _driverStats.asStateFlow()

    private val _driverTrips = MutableStateFlow<List<Trip>>(emptyList())
    val driverTrips: StateFlow<List<Trip>> = _driverTrips.asStateFlow()

    // Navigation
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    init {
        loadSampleData()
    }

    private fun loadSampleData() {
        viewModelScope.launch {
            val sampleVehicles = listOf(
                Vehicle("1", "Toyota", "Camry", "01AM123", "#0ea5e9", 4, isAvailable = true),
                Vehicle("2", "Honda", "Civic", "02BN456", "#ef4444", 4, isAvailable = true)
            )
            _vehicles.value = sampleVehicles

            val sampleMembers = listOf(
                User("2", "Armen Sargsyan", "armen@example.com", "driver", "+374 77 123456", rating = 4.8f, totalTrips = 156)
            )
            _members.value = sampleMembers

            val sampleTrips = listOf(
                Trip("1", "1", "2", "Yerevan Center", "Zvartnots Airport", 40.1776, 44.5126, 40.1596, 44.3931, 5000, 4, 2, "2025-08-26 14:30", status = "published", vehicle = sampleVehicles[0], driver = sampleMembers[0], distance = "15 km", duration = "25 min")
            )
            _trips.value = sampleTrips
            _availableTrips.value = sampleTrips.filter { it.status == "published" && it.seatsTaken < it.seatsTotal }
        }
    }

    fun loginClient(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val request = LoginRequest(email = email, password = password)
                val response = NetworkModule.apiService.loginClient(request)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.user != null && loginResponse.token != null) {
                        // Store the authentication token
                        _authToken.value = loginResponse.token
                        
                        _currentUser.value = User(
                            id = loginResponse.user.id.toString(),
                            name = loginResponse.user.name,
                            email = loginResponse.user.email,
                            role = "client",
                            phone = "",
                            isVerified = loginResponse.user.email_verified_at != null,
                            rating = 0f,
                            totalTrips = 0
                        )
                        _currentScreen.value = Screen.ClientHome
                    } else {
                        _errorMessage.value = loginResponse?.message ?: "Login failed"
                    }
                } else {
                    // Handle API error response with specific messages
                    try {
                        val errorBody = response.errorBody()?.string()
                        val gson = Gson()
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        
                        when (errorResponse?.message) {
                            "email_unverified" -> {
                                _errorMessage.value = "Your email address is not verified. Please check your email and click the verification link."
                            }
                            "invalid_credentials" -> {
                                _errorMessage.value = "Invalid email or password. Please check your credentials and try again."
                            }
                            "admin_approval_required" -> {
                                _errorMessage.value = "Your account is pending admin approval. Please wait for approval before logging in."
                            }
                            else -> {
                                _errorMessage.value = errorResponse?.message ?: "Login failed: ${response.message()}"
                            }
                        }
                    } catch (parseException: Exception) {
                        // Fallback if JSON parsing fails
                        _errorMessage.value = "Login failed: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registerClient(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val request = RegistrationRequest(
                    name = name,
                    email = email,
                    password = password,
                    password_confirmation = password
                )
                val response = NetworkModule.apiService.registerClient(request)

                if (response.isSuccessful) {
                    val registrationResponse = response.body()
                    if (registrationResponse?.user != null) {
                        _successMessage.value = "Registration successful! Please wait for admin approval."
                    } else {
                        _errorMessage.value = registrationResponse?.message ?: "Registration failed"
                    }
                } else {
                    _errorMessage.value = "Registration failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    // App mode functions
    fun selectAppMode(mode: AppMode) {
        _appMode.value = mode
        _currentScreen.value = when (mode) {
            AppMode.COMPANY -> Screen.CompanyLogin
            AppMode.CLIENT -> Screen.ClientLogin
            AppMode.DRIVER -> Screen.DriverLogin
        }
    }

    fun resetToModeSelector() {
        _appMode.value = null
        _currentUser.value = null
        _currentScreen.value = Screen.Dashboard
    }

    // Navigation functions
    fun navigateToScreen(screen: Screen) {
        _currentScreen.value = screen
    }

    // Company functions
    fun loginCompany(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val request = LoginRequest(email = email, password = password)
                val response = NetworkModule.apiService.loginClient(request)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.user != null && loginResponse.token != null) {
                        _currentUser.value = User(
                            id = loginResponse.user.id.toString(),
                            name = loginResponse.user.name,
                            email = loginResponse.user.email,
                            role = "company",
                            phone = "",
                            isVerified = loginResponse.user.email_verified_at != null,
                            rating = 0f,
                            totalTrips = 0
                        )
                        _currentScreen.value = Screen.Dashboard
                    } else {
                        _errorMessage.value = loginResponse?.message ?: "Login failed"
                    }
                } else {
                    // Handle API error response with specific messages
                    try {
                        val errorBody = response.errorBody()?.string()
                        val gson = Gson()
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        
                        when (errorResponse?.message) {
                            "email_unverified" -> {
                                _errorMessage.value = "Your email address is not verified. Please check your email and click the verification link."
                            }
                            "invalid_credentials" -> {
                                _errorMessage.value = "Invalid email or password. Please check your credentials and try again."
                            }
                            "admin_approval_required" -> {
                                _errorMessage.value = "Your account is pending admin approval. Please wait for approval before logging in."
                            }
                            else -> {
                                _errorMessage.value = errorResponse?.message ?: "Login failed: ${response.message()}"
                            }
                        }
                    } catch (parseException: Exception) {
                        // Fallback if JSON parsing fails
                        _errorMessage.value = "Login failed: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addVehicle(brand: String, model: String, plate: String, color: String, seats: Int) {
        viewModelScope.launch {
            val newVehicle = Vehicle(
                id = (_vehicles.value.size + 1).toString(),
                brand = brand,
                model = model,
                plate = plate,
                color = color,
                seats = seats,
                isAvailable = true
            )
            _vehicles.value = _vehicles.value + newVehicle
        }
    }

    fun addMember(name: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            val newMember = User(
                id = (_members.value.size + 2).toString(),
                name = name,
                email = email,
                role = role,
                phone = "",
                rating = 0f,
                totalTrips = 0
            )
            _members.value = _members.value + newMember
        }
    }

    fun getDrivers(): List<User> {
        return _members.value.filter { it.role == "driver" }
    }

    fun addTrip(trip: Trip) {
        viewModelScope.launch {
            val newTrip = trip.copy(
                id = (_trips.value.size + 1).toString(),
                status = "draft"
            )
            _trips.value = _trips.value + newTrip
        }
    }

    fun addTripWithParams(
        vehicleId: String, assignedDriverId: String, fromAddr: String, toAddr: String,
        fromLat: Double, fromLng: Double, toLat: Double, toLng: Double,
        priceAmd: Int, seatsTotal: Int, departureAt: String
    ) {
        viewModelScope.launch {
            val driver = _members.value.find { it.id == assignedDriverId }
            val vehicle = _vehicles.value.find { it.id == vehicleId } ?: _vehicles.value.firstOrNull()
            if (driver != null && vehicle != null) {
                val newTrip = Trip(
                    id = (_trips.value.size + 1).toString(),
                    vehicleId = vehicleId,
                    assignedDriverId = assignedDriverId,
                    fromAddr = fromAddr,
                    toAddr = toAddr,
                    fromLat = fromLat,
                    fromLng = fromLng,
                    toLat = toLat,
                    toLng = toLng,
                    priceAmd = priceAmd,
                    seatsTotal = seatsTotal,
                    seatsTaken = 0,
                    departureAt = departureAt,
                    status = "draft",
                    vehicle = vehicle,
                    driver = driver,
                    distance = "0 km",
                    duration = "0 min"
                )
                _trips.value = _trips.value + newTrip
            }
        }
    }

    fun publishTrip(tripId: String) {
        viewModelScope.launch {
            _trips.value = _trips.value.map { trip ->
                if (trip.id == tripId) {
                    trip.copy(status = "published")
                } else {
                    trip
                }
            }
            updateAvailableTrips()
        }
    }

    fun archiveTrip(tripId: String) {
        viewModelScope.launch {
            _trips.value = _trips.value.map { trip ->
                if (trip.id == tripId) {
                    trip.copy(status = "archived")
                } else {
                    trip
                }
            }
            updateAvailableTrips()
        }
    }

    fun unarchiveTrip(tripId: String) {
        viewModelScope.launch {
            _trips.value = _trips.value.map { trip ->
                if (trip.id == tripId) {
                    trip.copy(status = "published")
                } else {
                    trip
                }
            }
            updateAvailableTrips()
        }
    }

    private fun updateAvailableTrips() {
        _availableTrips.value = _trips.value.filter { 
            it.status == "published" && it.seatsTaken < it.seatsTotal 
        }
    }

    fun updateCurrentMapLocation(location: org.osmdroid.util.GeoPoint) {
        _currentMapLocation.value = location
    }

    // Request functions
    fun acceptRequest(requestId: String) {
        viewModelScope.launch {
            _requests.value = _requests.value.map { request ->
                if (request.id == requestId) {
                    request.copy(status = "accepted")
                } else {
                    request
                }
            }
        }
    }

    fun declineRequest(requestId: String) {
        viewModelScope.launch {
            _requests.value = _requests.value.map { request ->
                if (request.id == requestId) {
                    request.copy(status = "declined")
                } else {
                    request
                }
            }
        }
    }

    // Client functions
    fun bookTrip(tripId: String, seats: Int, payment: String, notes: String) {
        viewModelScope.launch {
            val trip = _trips.value.find { it.id == tripId }
            if (trip != null && trip.seatsTaken + seats <= trip.seatsTotal) {
                _trips.value = _trips.value.map { t ->
                    if (t.id == tripId) {
                        t.copy(seatsTaken = t.seatsTaken + seats)
                    } else {
                        t
                    }
                }
                updateAvailableTrips()
            }
        }
    }

    // Driver functions
    fun loginDriver(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val request = LoginRequest(email = email, password = password)
                val response = NetworkModule.apiService.loginClient(request)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.user != null && loginResponse.token != null) {
                        _currentUser.value = User(
                            id = loginResponse.user.id.toString(),
                            name = loginResponse.user.name,
                            email = loginResponse.user.email,
                            role = "driver",
                            phone = "",
                            isVerified = loginResponse.user.email_verified_at != null,
                            rating = 0f,
                            totalTrips = 0
                        )
                        _currentScreen.value = Screen.DriverDashboard
                    } else {
                        _errorMessage.value = loginResponse?.message ?: "Login failed"
                    }
                } else {
                    // Handle API error response with specific messages
                    try {
                        val errorBody = response.errorBody()?.string()
                        val gson = Gson()
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        
                        when (errorResponse?.message) {
                            "email_unverified" -> {
                                _errorMessage.value = "Your email address is not verified. Please check your email and click the verification link."
                            }
                            "invalid_credentials" -> {
                                _errorMessage.value = "Invalid email or password. Please check your credentials and try again."
                            }
                            "admin_approval_required" -> {
                                _errorMessage.value = "Your account is pending admin approval. Please wait for approval before logging in."
                            }
                            else -> {
                                _errorMessage.value = errorResponse?.message ?: "Login failed: ${response.message()}"
                            }
                        }
                    } catch (parseException: Exception) {
                        // Fallback if JSON parsing fails
                        _errorMessage.value = "Login failed: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun acceptDriverTrip(tripId: String) {
        viewModelScope.launch {
            // Driver accepts a trip assignment
        }
    }

    fun toggleDriverAvailability() {
        viewModelScope.launch {
            // Toggle driver availability status
        }
    }

    // General functions
    fun logout() {
        viewModelScope.launch {
            try {
                // Call logout API if user has a token
                _authToken.value?.let { token ->
                    try {
                        val response = NetworkModule.apiService.logout("Bearer $token")
                        // Note: We don't need to handle the response for logout,
                        // we clear local state regardless of API response
                    } catch (e: Exception) {
                        // Log error but continue with logout
                        // In a production app, you might want to log this error
                    }
                }
            } finally {
                // Always clear local state regardless of API call result
                _currentUser.value = null
                _appMode.value = null
                _currentMapLocation.value = null
                _authToken.value = null // Clear the token
                _currentScreen.value = Screen.Dashboard
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}