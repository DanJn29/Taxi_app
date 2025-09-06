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
import com.example.taxi_app.data.api.CompanyRegistrationRequest
import com.example.taxi_app.data.api.TripsResponse
import com.example.taxi_app.data.api.TripData
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime
import java.util.Locale

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
    
    private val _allTrips = MutableStateFlow<List<Trip>>(emptyList()) // Store all trips for filtering
    
    // Search and Filter States
    private val _searchFromLocation = MutableStateFlow("")
    val searchFromLocation: StateFlow<String> = _searchFromLocation.asStateFlow()
    
    private val _searchToLocation = MutableStateFlow("")
    val searchToLocation: StateFlow<String> = _searchToLocation.asStateFlow()
    
    private val _filterMinPrice = MutableStateFlow<Int?>(null)
    val filterMinPrice: StateFlow<Int?> = _filterMinPrice.asStateFlow()
    
    private val _filterMaxPrice = MutableStateFlow<Int?>(null)
    val filterMaxPrice: StateFlow<Int?> = _filterMaxPrice.asStateFlow()
    
    private val _filterMinSeats = MutableStateFlow<Int?>(null)
    val filterMinSeats: StateFlow<Int?> = _filterMinSeats.asStateFlow()
    
    private val _filterPaymentMethods = MutableStateFlow<List<String>>(emptyList())
    val filterPaymentMethods: StateFlow<List<String>> = _filterPaymentMethods.asStateFlow()

    private val _selectedTrip = MutableStateFlow<Trip?>(null)
    val selectedTrip: StateFlow<Trip?> = _selectedTrip.asStateFlow()

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
            _allTrips.value = sampleTrips
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
                        android.util.Log.d("TaxiApp", "Login successful, stored token: ${loginResponse.token}")
                        
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
                        
                        // Load trips after successful login
                        loadTrips()
                        
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

    // Helper function to format departure time from API to readable format
    private fun formatDepartureTime(isoDateTime: String): String {
        return try {
            val zonedDateTime = ZonedDateTime.parse(isoDateTime)
            
            // Armenian month abbreviations
            val armenianMonths = arrayOf(
                "Հուն", "Փետ", "Մար", "Ապր", "Մայ", "Հուն",
                "Հուլ", "Օգս", "Սեպ", "Հոկ", "Նոյ", "Դեկ"
            )
            
            val month = armenianMonths[zonedDateTime.monthValue - 1]
            val day = zonedDateTime.dayOfMonth
            val year = zonedDateTime.year
            val hour = String.format("%02d", zonedDateTime.hour)
            val minute = String.format("%02d", zonedDateTime.minute)
            
            "$month $day, $year - $hour:$minute"
        } catch (e: Exception) {
            android.util.Log.e("TaxiApp", "Error formatting date: ${e.message}")
            // Return original if parsing fails
            isoDateTime
        }
    }

    // Helper function to convert hex color to Armenian color name
    private fun hexToArmenianColor(hexColor: String): String {
        return try {
            val normalizedHex = hexColor.lowercase().replace("#", "")
            
            // Ensure we have a valid hex string
            if (normalizedHex.length < 3) {
                return hexColor
            }
            
            // Pad short hex codes (e.g., "fff" -> "ffffff")
            val fullHex = when (normalizedHex.length) {
                3 -> normalizedHex.map { "$it$it" }.joinToString("")
                4 -> normalizedHex.take(3).map { "$it$it" }.joinToString("")
                5 -> normalizedHex.take(6).padEnd(6, '0')
                6 -> normalizedHex
                else -> normalizedHex.take(6)
            }
            
            when {
                // White colors
                fullHex in listOf("ffffff", "f8f8ff", "fffafa", "f0f8ff") -> "սպիտակ"
                // Black colors  
                fullHex in listOf("000000", "0f0f0f", "1a1a1a") -> "սեւ"
                // Red colors
                fullHex.startsWith("ff") && fullHex.substring(2, 4).toIntOrNull(16) ?: 255 < 100 -> "կարմիր"
                fullHex in listOf("ff0000", "dc143c", "b22222", "8b0000", "cd5c5c") -> "կարմիր"
                // Blue colors
                fullHex.startsWith("0") && fullHex.contains("f") -> "կապույտ" 
                fullHex in listOf("0000ff", "4169e1", "1e90ff", "6495ed", "87ceeb", "0ea5e9") -> "կապույտ"
                // Green colors
                fullHex.length >= 4 && fullHex.substring(2, 4).toIntOrNull(16) ?: 0 > 200 && 
                fullHex.substring(0, 2).toIntOrNull(16) ?: 0 < 100 -> "կանաչ"
                fullHex in listOf("00ff00", "32cd32", "90ee90", "228b22", "006400") -> "կանաչ"
                // Yellow colors
                fullHex.startsWith("ff") && fullHex.length >= 4 && 
                fullHex.substring(2, 4).toIntOrNull(16) ?: 0 > 200 -> "դեղին"
                fullHex in listOf("ffff00", "ffd700", "ffb347", "ffa500") -> "դեղին"
                // Gray colors (all RGB values are equal)
                fullHex.length >= 6 && 
                fullHex.substring(0, 2) == fullHex.substring(2, 4) && 
                fullHex.substring(2, 4) == fullHex.substring(4, 6) -> "գորշ"
                fullHex in listOf("808080", "696969", "a9a9a9", "d3d3d3", "c0c0c0") -> "գորշ"
                // Brown colors
                fullHex in listOf("a0522d", "8b4513", "d2691e", "cd853f", "f4a460") -> "շագանակագույն"
                // Purple colors
                fullHex in listOf("800080", "9370db", "8a2be2", "9932cc", "ba55d3") -> "մանուշակագույն"
                // Orange colors  
                fullHex in listOf("ffa500", "ff8c00", "ff7f50", "ff6347", "ff4500") -> "նարնջագույն"
                // Pink colors
                fullHex in listOf("ffc0cb", "ffb6c1", "ff69b4", "ff1493", "c71585") -> "վարդագույն"
                // Silver colors
                fullHex in listOf("c0c0c0", "dcdcdc", "f5f5f5") -> "արծաթագույն"
                // Default for unknown colors
                else -> hexColor
            }
        } catch (e: Exception) {
            android.util.Log.e("TaxiApp", "Error converting color $hexColor: ${e.message}")
            hexColor
        }
    }

    private fun loadTrips() {
        viewModelScope.launch {
            try {
                val token = _authToken.value
                if (token.isNullOrEmpty()) {
                    android.util.Log.e("TaxiApp", "No auth token available for trips API")
                    return@launch
                }

                android.util.Log.d("TaxiApp", "Loading trips with token: $token")
                val response = NetworkModule.apiService.getTrips("Bearer $token")

                if (response.isSuccessful) {
                    val tripsResponse = response.body()
                    tripsResponse?.let { apiResponse ->
                        // Convert API trips to local Trip objects
                        val convertedTrips = apiResponse.data.map { tripData ->
                            Trip(
                                id = tripData.id.toString(),
                                vehicleId = "",
                                assignedDriverId = tripData.driver.id.toString(),
                                fromAddr = tripData.from_addr,
                                toAddr = tripData.to_addr,
                                fromLat = tripData.from_lat,
                                fromLng = tripData.from_lng,
                                toLat = tripData.to_lat,
                                toLng = tripData.to_lng,
                                priceAmd = tripData.price_amd,
                                seatsTotal = tripData.seats_total,
                                seatsTaken = tripData.seats_taken,
                                status = "published", // API trips are published
                                departureAt = formatDepartureTime(tripData.departure_at),
                                payMethods = tripData.pay_methods,
                                vehicle = Vehicle(
                                    id = "",
                                    brand = tripData.vehicle.brand,
                                    model = tripData.vehicle.model,
                                    plate = tripData.vehicle.plate,
                                    color = hexToArmenianColor(tripData.vehicle.color),
                                    seats = tripData.vehicle.seats,
                                    userId = tripData.driver.id.toString(),
                                    isAvailable = true
                                ),
                                driver = User(
                                    id = tripData.driver.id.toString(),
                                    name = tripData.driver.name,
                                    email = "",
                                    role = "driver"
                                )
                            )
                        }
                        
                        // Update trips state
                        _trips.value = convertedTrips
                        _allTrips.value = convertedTrips
                        _availableTrips.value = convertedTrips.filter { it.seatsTaken < it.seatsTotal }
                        
                        android.util.Log.d("TaxiApp", "Successfully loaded ${convertedTrips.size} trips")
                    }
                } else {
                    android.util.Log.e("TaxiApp", "Failed to load trips: ${response.message()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("TaxiApp", "Error loading trips: ${e.message}")
            }
        }
    }

    // Public function to refresh trips
    fun refreshTrips() {
        loadTrips()
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

    fun selectTrip(trip: Trip) {
        _selectedTrip.value = trip
    }

    fun clearSelectedTrip() {
        _selectedTrip.value = null
    }
    
    // Search and Filter Functions
    fun updateSearchFromLocation(location: String) {
        _searchFromLocation.value = location
        applyFilters()
    }
    
    fun updateSearchToLocation(location: String) {
        _searchToLocation.value = location
        applyFilters()
    }
    
    fun updateFilterPriceRange(minPrice: Int?, maxPrice: Int?) {
        _filterMinPrice.value = minPrice
        _filterMaxPrice.value = maxPrice
        applyFilters()
    }
    
    fun updateFilterMinSeats(minSeats: Int?) {
        _filterMinSeats.value = minSeats
        applyFilters()
    }
    
    fun updateFilterPaymentMethods(paymentMethods: List<String>) {
        _filterPaymentMethods.value = paymentMethods
        applyFilters()
    }
    
    fun clearAllFilters() {
        _searchFromLocation.value = ""
        _searchToLocation.value = ""
        _filterMinPrice.value = null
        _filterMaxPrice.value = null
        _filterMinSeats.value = null
        _filterPaymentMethods.value = emptyList()
        applyFilters()
    }
    
    private fun applyFilters() {
        val fromLocation = _searchFromLocation.value.trim()
        val toLocation = _searchToLocation.value.trim()
        val minPrice = _filterMinPrice.value
        val maxPrice = _filterMaxPrice.value
        val minSeats = _filterMinSeats.value
        val paymentMethods = _filterPaymentMethods.value
        
        val filteredTrips = _allTrips.value.filter { trip ->
            var matches = true
            
            // Filter by from location
            if (fromLocation.isNotEmpty()) {
                matches = matches && trip.fromAddr.contains(fromLocation, ignoreCase = true)
            }
            
            // Filter by to location
            if (toLocation.isNotEmpty()) {
                matches = matches && trip.toAddr.contains(toLocation, ignoreCase = true)
            }
            
            // Filter by price range
            if (minPrice != null) {
                matches = matches && trip.priceAmd >= minPrice
            }
            if (maxPrice != null) {
                matches = matches && trip.priceAmd <= maxPrice
            }
            
            // Filter by available seats
            if (minSeats != null) {
                val availableSeats = trip.seatsTotal - trip.seatsTaken
                matches = matches && availableSeats >= minSeats
            }
            
            // Filter by payment methods
            if (paymentMethods.isNotEmpty()) {
                matches = matches && trip.payMethods.any { it in paymentMethods }
            }
            
            // Only show published trips with available seats
            matches = matches && trip.status == "published" && trip.seatsTaken < trip.seatsTotal
            
            matches
        }
        
        _availableTrips.value = filteredTrips
    }

    fun registerDriver(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                _successMessage.value = null

                val request = RegistrationRequest(
                    name = name,
                    email = email,
                    password = password,
                    password_confirmation = password
                )
                
                // Use the driver registration endpoint
                val response = NetworkModule.apiService.registerDriver(request)

                if (response.isSuccessful) {
                    val registrationResponse = response.body()
                    if (registrationResponse?.user != null) {
                        _successMessage.value = "Driver registration successful! Please wait until the admin approves your account."
                    } else {
                        _errorMessage.value = registrationResponse?.message ?: "Registration failed"
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
                                _errorMessage.value = errorResponse?.message ?: "Registration failed: ${response.message()}"
                            }
                        }
                    } catch (parseException: Exception) {
                        // Fallback if JSON parsing fails
                        _errorMessage.value = "Registration failed: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registerCompany(name: String, email: String, password: String, confirmPassword: String, companyName: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                _successMessage.value = null

                val request = CompanyRegistrationRequest(
                    name = name,
                    email = email,
                    password = password,
                    password_confirmation = confirmPassword,
                    company_name = companyName
                )
                val response = NetworkModule.apiService.registerCompany(request)

                if (response.isSuccessful) {
                    val registrationResponse = response.body()
                    _successMessage.value = registrationResponse?.message ?: "Company registration successful! Please check your email for verification."
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
                                _errorMessage.value = errorResponse?.message ?: "Registration failed: ${response.message()}"
                            }
                        }
                    } catch (parseException: Exception) {
                        // Fallback if JSON parsing fails
                        _errorMessage.value = "Registration failed: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
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
                        // Store the authentication token
                        _authToken.value = loginResponse.token
                        android.util.Log.d("TaxiApp", "Company login successful, stored token: ${loginResponse.token}")
                        
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
        _allTrips.value = _trips.value
        applyFilters()
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
                        // Store the authentication token
                        _authToken.value = loginResponse.token
                        android.util.Log.d("TaxiApp", "Driver login successful, stored token: ${loginResponse.token}")
                        
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
        android.util.Log.d("TaxiApp", "Logout function called")
        viewModelScope.launch {
            // Call logout API if user has a token
            val token = _authToken.value
            if (token != null) {
                try {
                    android.util.Log.d("TaxiApp", "Calling logout API with token: Bearer $token")
                    val response = NetworkModule.apiService.logout("Bearer $token")
                    android.util.Log.d("TaxiApp", "Logout API response: ${response.code()} - ${response.message()}")
                    if (response.isSuccessful) {
                        android.util.Log.d("TaxiApp", "Logout API call successful")
                    } else {
                        android.util.Log.w("TaxiApp", "Logout API call failed: ${response.code()}")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("TaxiApp", "Logout API call error: ${e.message}", e)
                }
            } else {
                android.util.Log.d("TaxiApp", "No auth token found, skipping logout API call")
            }
            
            // Always clear local state regardless of API call result
            android.util.Log.d("TaxiApp", "Clearing local state for logout")
            _currentUser.value = null
            _appMode.value = null
            _currentMapLocation.value = null
            _authToken.value = null // Clear the token
            _currentScreen.value = Screen.Dashboard
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}