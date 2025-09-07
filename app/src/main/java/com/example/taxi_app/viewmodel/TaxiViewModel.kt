package com.example.taxi_app.viewmodel

import android.content.Context
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
import com.example.taxi_app.data.api.DriverTripsResponse
import com.example.taxi_app.data.api.DriverTripData
import com.example.taxi_app.data.api.VehicleResponse
import com.example.taxi_app.data.api.BookingRequest
import com.example.taxi_app.data.api.BookingResponse
import com.example.taxi_app.data.api.RequestsResponse
import com.example.taxi_app.data.api.RequestData
import com.example.taxi_app.utils.NotificationHelper
import com.example.taxi_app.utils.AuthManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime
import java.util.Locale

// Error response data class for parsing API errors
data class ErrorResponse(
    val message: String?
)

class TaxiViewModel(private val context: Context) : ViewModel() {

    // Initialize notification helper and auth manager
    private val notificationHelper = NotificationHelper(context)
    private val authManager = AuthManager(context)

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
    
    // Authentication State
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    // Track if driver just registered and needs vehicle setup
    private val _isNewDriverRegistration = MutableStateFlow(false)
    val isNewDriverRegistration: StateFlow<Boolean> = _isNewDriverRegistration.asStateFlow()

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

    // Requests Data (for viewing user's booking requests)
    private val _myRequests = MutableStateFlow<List<RequestData>>(emptyList())
    val myRequests: StateFlow<List<RequestData>> = _myRequests.asStateFlow()

    // Notification States
    private val _unreadNotificationsCount = MutableStateFlow(0)
    val unreadNotificationsCount: StateFlow<Int> = _unreadNotificationsCount.asStateFlow()
    
    private val _lastReadRequestCount = MutableStateFlow(0)
    private val _hasNewAcceptedRequests = MutableStateFlow(false)
    val hasNewAcceptedRequests: StateFlow<Boolean> = _hasNewAcceptedRequests.asStateFlow()
    
    private val _hasNewRejectedRequests = MutableStateFlow(false)
    val hasNewRejectedRequests: StateFlow<Boolean> = _hasNewRejectedRequests.asStateFlow()
    
    private val _latestNotificationMessage = MutableStateFlow<String?>(null)
    val latestNotificationMessage: StateFlow<String?> = _latestNotificationMessage.asStateFlow()

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

    // Driver Published Trips from API
    private val _driverPublishedTrips = MutableStateFlow<List<DriverTripData>>(emptyList())
    val driverPublishedTrips: StateFlow<List<DriverTripData>> = _driverPublishedTrips.asStateFlow()

    // Auto-refresh mechanism
    private var autoRefreshJob: Job? = null
    private val _autoRefreshEnabled = MutableStateFlow(false)
    val autoRefreshEnabled: StateFlow<Boolean> = _autoRefreshEnabled.asStateFlow()
    
    // Refresh interval in milliseconds (30 seconds)
    private val refreshIntervalMs = 30_000L

    // Navigation
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    init {
        loadSampleData()
        restoreAuthSession()
    }

    private fun restoreAuthSession() {
        viewModelScope.launch {
            if (authManager.hasValidSession()) {
                val savedToken = authManager.getAuthToken()
                val savedUser = authManager.getUserData()
                val savedAppMode = authManager.getAppMode()
                
                if (savedToken != null && savedUser != null && savedAppMode != null) {
                    // Restore authentication state
                    _authToken.value = savedToken
                    _currentUser.value = savedUser
                    _appMode.value = savedAppMode
                    _isAuthenticated.value = true
                    
                    // Load user-specific data based on user type
                    when (savedUser.role) {
                        "client" -> {
                            // Load client-specific data
                            loadTrips()
                            loadMyRequests()
                            _currentScreen.value = Screen.ClientHome
                        }
                        "driver" -> {
                            // For restored sessions, always go to dashboard (never vehicle setup)
                            // Vehicle setup is only for newly registered drivers
                            _currentScreen.value = Screen.DriverDashboard
                        }
                        "company" -> {
                            // Load company-specific data
                            _currentScreen.value = Screen.Dashboard
                        }
                    }
                    
                    // Start auto-refresh for authenticated users
                    startAutoRefresh()
                    
                    android.util.Log.d("TaxiApp", "Authentication restored for user: ${savedUser.id}, type: ${savedUser.role}")
                }
            }
        }
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
                        
                        val currentUser = User(
                            id = loginResponse.user.id.toString(),
                            name = loginResponse.user.name,
                            email = loginResponse.user.email,
                            role = "client",
                            phone = "",
                            isVerified = loginResponse.user.email_verified_at != null,
                            rating = 0f,
                            totalTrips = 0
                        )
                        
                        _currentUser.value = currentUser
                        _isAuthenticated.value = true
                        
                        // Save authentication data for persistent login
                        authManager.saveAuthData(
                            token = loginResponse.token,
                            user = currentUser,
                            appMode = AppMode.CLIENT
                        )
                        
                        // Load trips after successful login
                        loadTrips()
                        
                        // Load user's requests
                        loadMyRequests()
                        
                        // Start auto-refresh to keep data synchronized
                        startAutoRefresh()
                        
                        _currentScreen.value = Screen.ClientHome
                    } else {
                        _errorMessage.value = "Մուտք չհաջողվեց: Խնդրում ենք նորից փորձել:"
                    }
                } else {
                    // Handle API error response with specific messages
                    try {
                        val errorBody = response.errorBody()?.string()
                        val gson = Gson()
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        
                        when (errorResponse?.message) {
                            "email_unverified" -> {
                                _errorMessage.value = "Ձեր էլ. հասցեն չի հաստատվել: Խնդրում ենք ստուգել ձեր էլ. փոստը և սեղմել հաստատման հղումը:"
                            }
                            "invalid_credentials" -> {
                                _errorMessage.value = "Սխալ էլ. հասցե կամ գաղտնաբառ: Խնդրում ենք ստուգել ձեր տվյալները և նորից փորձել:"
                            }
                            "admin_approval_required" -> {
                                _errorMessage.value = "Ձեր հաշիվը սպասում է ադմինիստրատորի հաստատմանը: Խնդրում ենք սպասել հաստատմանը:"
                            }
                            else -> {
                                _errorMessage.value = "Մուտք չհաջողվեց: Խնդրում ենք նորից փորձել:"
                            }
                        }
                    } catch (parseException: Exception) {
                        // Fallback if JSON parsing fails
                        _errorMessage.value = "Մուտք չհաջողվեց: Խնդրում ենք ստուգել ձեր ինտերնետ կապը և նորից փորձել:"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ինտերնետ կապի խնդիր: Խնդրում ենք ստուգել ձեր կապը և նորից փորձել:"
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

    // Function to load user's requests
    private fun loadMyRequests() {
        viewModelScope.launch {
            try {
                val token = _authToken.value
                if (token.isNullOrEmpty()) {
                    android.util.Log.e("TaxiApp", "No auth token available for requests API")
                    return@launch
                }

                android.util.Log.d("TaxiApp", "Loading my requests with token: $token")
                val response = NetworkModule.apiService.getMyRequests("Bearer $token", 20)

                if (response.isSuccessful) {
                    val requestsResponse = response.body()
                    requestsResponse?.let { apiResponse ->
                        val previousRequests = _myRequests.value
                        val newRequests = apiResponse.data
                        
                        // Check for newly accepted requests
                        val previousAcceptedCount = previousRequests.count { 
                            it.status.lowercase() in listOf("accepted", "approved") 
                        }
                        val newAcceptedCount = newRequests.count { 
                            it.status.lowercase() in listOf("accepted", "approved") 
                        }
                        
                        // Check for newly rejected requests
                        val previousRejectedCount = previousRequests.count { 
                            it.status.lowercase() in listOf("rejected", "declined") 
                        }
                        val newRejectedCount = newRequests.count { 
                            it.status.lowercase() in listOf("rejected", "declined") 
                        }
                        
                        // If there are new accepted requests, increment notification count
                        if (newAcceptedCount > previousAcceptedCount && previousRequests.isNotEmpty()) {
                            val newAcceptedRequestsCount = newAcceptedCount - previousAcceptedCount
                            _unreadNotificationsCount.value += newAcceptedRequestsCount
                            _hasNewAcceptedRequests.value = true
                            _latestNotificationMessage.value = "Ձեր հայտը ընդունվել է! 🎉"
                            
                            // Send push notification for accepted request
                            notificationHelper.showRequestAcceptedNotification()
                            
                            android.util.Log.d("TaxiApp", "Found $newAcceptedRequestsCount new accepted requests")
                        }
                        
                        // If there are new rejected requests, increment notification count
                        if (newRejectedCount > previousRejectedCount && previousRequests.isNotEmpty()) {
                            val newRejectedRequestsCount = newRejectedCount - previousRejectedCount
                            _unreadNotificationsCount.value += newRejectedRequestsCount
                            _hasNewRejectedRequests.value = true
                            _latestNotificationMessage.value = "Ձեր հայտը մերժվել է 😔"
                            
                            // Send push notification for rejected request
                            notificationHelper.showRequestRejectedNotification()
                            
                            android.util.Log.d("TaxiApp", "Found $newRejectedRequestsCount new rejected requests")
                        }
                        
                        _myRequests.value = newRequests
                        android.util.Log.d("TaxiApp", "Successfully loaded ${newRequests.size} requests")
                    }
                } else {
                    android.util.Log.e("TaxiApp", "Failed to load requests: ${response.message()}")
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("TaxiApp", "Error response body: $errorBody")
                }
            } catch (e: Exception) {
                android.util.Log.e("TaxiApp", "Error loading requests: ${e.message}")
            }
        }
    }

    // Public function to refresh requests
    fun refreshMyRequests() {
        loadMyRequests()
    }

    // Notification management functions
    fun markNotificationsAsRead() {
        _unreadNotificationsCount.value = 0
        _hasNewAcceptedRequests.value = false
        _hasNewRejectedRequests.value = false
        _latestNotificationMessage.value = null
        android.util.Log.d("TaxiApp", "Notifications marked as read")
    }
    
    fun resetNotificationCount() {
        _unreadNotificationsCount.value = 0
        _hasNewAcceptedRequests.value = false
        _hasNewRejectedRequests.value = false
        _latestNotificationMessage.value = null
    }
    
    fun clearNotificationMessage() {
        _latestNotificationMessage.value = null
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
                        _successMessage.value = "Գրանցումը հաջողվեց: Խնդրում ենք սպասել ադմինիստրատորի հաստատմանը:"
                    } else {
                        _errorMessage.value = "Գրանցում չհաջողվեց: Խնդրում ենք նորից փորձել:"
                    }
                } else {
                    _errorMessage.value = "Գրանցում չհաջողվեց: Խնդրում ենք ստուգել ձեր տվյալները և նորից փորձել:"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ինտերնետ կապի խնդիր: Խնդրում ենք ստուգել ձեր կապը և նորից փորձել:"
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
                        // After successful driver registration, go directly to car registration
                        android.util.Log.d("TaxiApp", "Driver registration successful - redirecting to car registration")
                        
                        // Check if we have a token for vehicle registration
                        if (registrationResponse.token != null) {
                            // Set temporary auth token for vehicle registration
                            _authToken.value = registrationResponse.token
                            android.util.Log.d("TaxiApp", "Using registration token for vehicle setup: ${registrationResponse.token}")
                        } else {
                            // If no token, we'll need to handle vehicle registration differently
                            // For now, log a warning but continue
                            android.util.Log.w("TaxiApp", "No token provided in registration response")
                        }
                        
                        // Store user data temporarily for car registration
                        val tempUser = User(
                            id = registrationResponse.user.id.toString(),
                            name = registrationResponse.user.name,
                            email = registrationResponse.user.email,
                            role = "driver",
                            phone = "",
                            isVerified = registrationResponse.user.email_verified_at != null,
                            rating = 0f,
                            totalTrips = 0
                        )
                        
                        // Set temporary user data (not fully logged in yet)
                        _currentUser.value = tempUser
                        _appMode.value = AppMode.DRIVER
                        
                        // Show success message and go to vehicle setup
                        _successMessage.value = "Վարորդի գրանցումը հաջողվեց: Այժմ գրանցեք ձեր ավտոմեքենան:"
                        _currentScreen.value = Screen.DriverVehicleSetup
                    } else {
                        _errorMessage.value = "Գրանցում չհաջողվեց: Խնդրում ենք նորից փորձել:"
                    }
                } else {
                    // Handle API error response with specific messages
                    try {
                        val errorBody = response.errorBody()?.string()
                        val gson = Gson()
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        
                        when (errorResponse?.message) {
                            "email_unverified" -> {
                                _errorMessage.value = "Ձեր էլ. հասցեն չի հաստատվել: Խնդրում ենք ստուգել ձեր էլ. փոստը և սեղմել հաստատման հղումը:"
                            }
                            "invalid_credentials" -> {
                                _errorMessage.value = "Սխալ էլ. հասցե կամ գաղտնաբառ: Խնդրում ենք ստուգել ձեր տվյալները և նորից փորձել:"
                            }
                            "admin_approval_required" -> {
                                _errorMessage.value = "Ձեր հաշիվը սպասում է ադմինիստրատորի հաստատմանը: Խնդրում ենք սպասել հաստատմանը:"
                            }
                            else -> {
                                _errorMessage.value = "Գրանցում չհաջողվեց: Խնդրում ենք նորից փորձել:"
                            }
                        }
                    } catch (parseException: Exception) {
                        // Fallback if JSON parsing fails
                        _errorMessage.value = "Գրանցում չհաջողվեց: Խնդրում ենք ստուգել ձեր ինտերնետ կապը և նորից փորձել:"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ինտերնետ կապի խնդիր: Խնդրում ենք ստուգել ձեր կապը և նորից փորձել:"
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
                    _successMessage.value = "Կազմակերպության գրանցումը հաջողվեց: Խնդրում ենք ստուգել ձեր էլ. փոստը հաստատման համար:"
                } else {
                    // Handle API error response with specific messages
                    try {
                        val errorBody = response.errorBody()?.string()
                        val gson = Gson()
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        
                        when (errorResponse?.message) {
                            "email_unverified" -> {
                                _errorMessage.value = "Ձեր էլ. հասցեն չի հաստատվել: Խնդրում ենք ստուգել ձեր էլ. փոստը և սեղմել հաստատման հղումը:"
                            }
                            "invalid_credentials" -> {
                                _errorMessage.value = "Սխալ էլ. հասցե կամ գաղտնաբառ: Խնդրում ենք ստուգել ձեր տվյալները և նորից փորձել:"
                            }
                            "admin_approval_required" -> {
                                _errorMessage.value = "Ձեր հաշիվը սպասում է ադմինիստրատորի հաստատմանը: Խնդրում ենք սպասել հաստատմանը:"
                            }
                            else -> {
                                _errorMessage.value = "Գրանցում չհաջողվեց: Խնդրում ենք նորից փորձել:"
                            }
                        }
                    } catch (parseException: Exception) {
                        // Fallback if JSON parsing fails
                        _errorMessage.value = "Գրանցում չհաջողվեց: Խնդրում ենք ստուգել ձեր ինտերնետ կապը և նորից փորձել:"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ինտերնետ կապի խնդիր: Խնդրում ենք ստուգել ձեր կապը և նորից փորձել:"
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
                        
                        val currentUser = User(
                            id = loginResponse.user.id.toString(),
                            name = loginResponse.user.name,
                            email = loginResponse.user.email,
                            role = "company",
                            phone = "",
                            isVerified = loginResponse.user.email_verified_at != null,
                            rating = 0f,
                            totalTrips = 0
                        )
                        
                        _currentUser.value = currentUser
                        _isAuthenticated.value = true
                        
                        // Save authentication data for persistent login
                        authManager.saveAuthData(
                            token = loginResponse.token,
                            user = currentUser,
                            appMode = AppMode.COMPANY
                        )
                        
                        // Start auto-refresh to keep data synchronized
                        startAutoRefresh()
                        
                        _currentScreen.value = Screen.Dashboard
                    } else {
                        _errorMessage.value = "Մուտք չհաջողվեց: Խնդրում ենք նորից փորձել:"
                    }
                } else {
                    // Handle API error response with specific messages
                    try {
                        val errorBody = response.errorBody()?.string()
                        val gson = Gson()
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        
                        when (errorResponse?.message) {
                            "email_unverified" -> {
                                _errorMessage.value = "Ձեր էլ. հասցեն չի հաստատվել: Խնդրում ենք ստուգել ձեր էլ. փոստը և սեղմել հաստատման հղումը:"
                            }
                            "invalid_credentials" -> {
                                _errorMessage.value = "Սխալ էլ. հասցե կամ գաղտնաբառ: Խնդրում ենք ստուգել ձեր տվյալները և նորից փորձել:"
                            }
                            "admin_approval_required" -> {
                                _errorMessage.value = "Ձեր հաշիվը սպասում է ադմինիստրատորի հաստատմանը: Խնդրում ենք սպասել հաստատմանը:"
                            }
                            else -> {
                                _errorMessage.value = "Մուտք չհաջողվեց: Խնդրում ենք նորից փորձել:"
                            }
                        }
                    } catch (parseException: Exception) {
                        // Fallback if JSON parsing fails
                        _errorMessage.value = "Մուտք չհաջողվեց: Խնդրում ենք ստուգել ձեր ինտերնետ կապը և նորից փորձել:"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ինտերնետ կապի խնդիր: Խնդրում ենք ստուգել ձեր կապը և նորից փորձել:"
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
    fun bookTrip(tripId: String, seats: Int, payment: String, description: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                _successMessage.value = null
                
                val token = _authToken.value
                if (token == null) {
                    _errorMessage.value = "Փորձեք նորից մուտք գործել"
                    _isLoading.value = false
                    return@launch
                }

                val bookingRequest = com.example.taxi_app.data.api.BookingRequest(
                    description = description,
                    seats = seats,
                    payment = payment
                )

                android.util.Log.d("TaxiApp", "Booking trip with: tripId=$tripId, seats=$seats, payment=$payment")
                android.util.Log.d("TaxiApp", "Description: $description")
                android.util.Log.d("TaxiApp", "Authorization token: Bearer $token")
                android.util.Log.d("TaxiApp", "Equivalent curl command:")
                android.util.Log.d("TaxiApp", "curl -X POST http://api.tamojni.com/api/trips/$tripId/requests \\")
                android.util.Log.d("TaxiApp", "  -H \"Authorization: Bearer $token\" \\")
                android.util.Log.d("TaxiApp", "  -H \"Content-Type: application/json\" \\")
                android.util.Log.d("TaxiApp", "  -d '{\"description\":\"$description\",\"seats\":$seats,\"payment\":\"$payment\"}'")

                val response = NetworkModule.apiService.bookTrip(
                    token = "Bearer $token",
                    tripId = tripId,
                    request = bookingRequest
                )

                android.util.Log.d("TaxiApp", "Raw booking API response code: ${response.code()}")
                android.util.Log.d("TaxiApp", "Raw booking API response message: ${response.message()}")

                if (response.isSuccessful) {
                    val bookingResponse = response.body()
                    android.util.Log.d("TaxiApp", "Booking API response: $bookingResponse")
                    
                    // Check if response indicates success
                    val isSuccess = when {
                        bookingResponse?.success == true -> true
                        bookingResponse?.status == "success" -> true
                        bookingResponse?.request != null -> true
                        bookingResponse?.data != null -> true
                        bookingResponse?.message?.contains("success", ignoreCase = true) == true -> true
                        bookingResponse?.message?.contains("created", ignoreCase = true) == true -> true
                        bookingResponse?.message?.contains("booked", ignoreCase = true) == true -> true
                        bookingResponse?.errors == null && response.code() in 200..299 -> true
                        bookingResponse == null && response.code() in 200..299 -> true
                        else -> false
                    }
                    
                    if (isSuccess) {
                        _successMessage.value = "Գրանցումը հաջողությամբ ուղարկվեց"
                        android.util.Log.d("TaxiApp", "Trip booked successfully")
                        
                        // Refresh trips data from API to get updated seat counts
                        loadTrips()
                        
                        // Refresh requests data to show the new booking request
                        loadMyRequests()
                    } else {
                        _errorMessage.value = bookingResponse?.message ?: "Գրանցումը չհաջողվեց"
                        android.util.Log.w("TaxiApp", "Trip booking failed: ${bookingResponse?.message}")
                        android.util.Log.w("TaxiApp", "Errors: ${bookingResponse?.errors}")
                    }
                } else {
                    android.util.Log.e("TaxiApp", "Trip booking API failed with code: ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("TaxiApp", "Error response body: $errorBody")
                    _errorMessage.value = when (response.code()) {
                        400 -> "Սխալ տվյալներ: Ստուգեք մուտքագրված տեղեկությունները"
                        401 -> "Թույլտվության խնդիր: Խնդրում ենք նորից մուտք գործել"
                        403 -> "Արգելված է: Ինչ-որ բան սխալ է"
                        404 -> "Ճանապարհորդությունը չի գտնվել"
                        422 -> "Տվյալների վավերացման սխալ"
                        else -> "Սերվերի խնդիր: Խնդրում ենք նորից փորձել"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ինտերնետ կապի խնդիր: Ստուգեք ձեր կապը"
                android.util.Log.e("TaxiApp", "Trip booking error: ${e.message}", e)
            } finally {
                _isLoading.value = false
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
                        
                        val currentUser = User(
                            id = loginResponse.user.id.toString(),
                            name = loginResponse.user.name,
                            email = loginResponse.user.email,
                            role = "driver",
                            phone = "",
                            isVerified = loginResponse.user.email_verified_at != null,
                            rating = 0f,
                            totalTrips = 0
                        )
                        
                        _currentUser.value = currentUser
                        _isAuthenticated.value = true
                        
                        // Save authentication data for persistent login
                        authManager.saveAuthData(
                            token = loginResponse.token,
                            user = currentUser,
                            appMode = AppMode.DRIVER
                        )
                        
                        // For all driver logins, go directly to dashboard
                        // Vehicle setup was completed during registration flow
                        android.util.Log.d("TaxiApp", "Driver login successful - going to dashboard")
                        
                        // Start auto-refresh to keep data synchronized
                        startAutoRefresh()
                        
                        // Fetch driver's published trips
                        fetchDriverTrips()
                        
                        _currentScreen.value = Screen.DriverDashboard
                    } else {
                        _errorMessage.value = "Մուտք չհաջողվեց: Խնդրում ենք նորից փորձել:"
                    }
                } else {
                    // Handle API error response with specific messages
                    try {
                        val errorBody = response.errorBody()?.string()
                        val gson = Gson()
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        
                        when (errorResponse?.message) {
                            "email_unverified" -> {
                                _errorMessage.value = "Ձեր էլ. հասցեն չի հաստատվել: Խնդրում ենք ստուգել ձեր էլ. փոստը և սեղմել հաստատման հղումը:"
                            }
                            "invalid_credentials" -> {
                                _errorMessage.value = "Սխալ էլ. հասցե կամ գաղտնաբառ: Խնդրում ենք ստուգել ձեր տվյալները և նորից փորձել:"
                            }
                            "admin_approval_required" -> {
                                _errorMessage.value = "Ձեր հաշիվը սպասում է ադմինիստրատորի հաստատմանը: Խնդրում ենք սպասել հաստատմանը:"
                            }
                            else -> {
                                _errorMessage.value = "Մուտք չհաջողվեց: Խնդրում ենք նորից փորձել:"
                            }
                        }
                    } catch (parseException: Exception) {
                        // Fallback if JSON parsing fails
                        _errorMessage.value = "Մուտք չհաջողվեց: Խնդրում ենք ստուգել ձեր ինտերնետ կապը և նորից փորձել:"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ինտերնետ կապի խնդիր: Խնդրում ենք ստուգել ձեր կապը և նորից փորձել:"
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

    /**
     * Checks if driver has ever called the vehicle registration API.
     * This function is available for future features that might need to check vehicle status,
     * but is not used for login routing decisions.
     */
    private suspend fun checkDriverHasVehicleRecord(): Boolean {
        return try {
            val token = _authToken.value
            if (token != null) {
                android.util.Log.d("TaxiApp", "Checking if driver has ever registered a vehicle")
                val response = NetworkModule.apiService.getDriverVehicle("Bearer $token")
                
                if (response.isSuccessful) {
                    val vehicleResponse = response.body()
                    val vehicleData = vehicleResponse?.vehicle ?: vehicleResponse?.data
                    
                    // If driver has ANY vehicle record, they have used the API before
                    val hasVehicleRecord = vehicleData != null
                    android.util.Log.d("TaxiApp", "Driver has vehicle record: $hasVehicleRecord")
                    
                    if (hasVehicleRecord) {
                        android.util.Log.d("TaxiApp", "Vehicle found: brand=${vehicleData?.brand}, model=${vehicleData?.model}, photo=${vehicleData?.photo}")
                    }
                    
                    return hasVehicleRecord
                } else if (response.code() == 404) {
                    // 404 typically means no vehicle record exists
                    android.util.Log.d("TaxiApp", "No vehicle record found (404) - driver needs to register vehicle")
                    return false
                } else {
                    android.util.Log.d("TaxiApp", "API error ${response.code()} - assuming no vehicle record")
                    return false
                }
            } else {
                android.util.Log.e("TaxiApp", "No auth token available")
                return false
            }
        } catch (e: Exception) {
            android.util.Log.e("TaxiApp", "Error checking driver vehicle: ${e.message}", e)
            // On error, assume no vehicle to be safe
            return false
        }
    }

    fun toggleDriverAvailability() {
        viewModelScope.launch {
            // Toggle driver availability status
        }
    }

    // Vehicle registration function
    fun registerVehicle(
        brand: String,
        model: String,
        seats: Int,
        color: String,
        plate: String,
        photoUri: android.net.Uri?,
        context: android.content.Context? = null
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                _successMessage.value = null
                
                val token = _authToken.value
                if (token == null) {
                    _errorMessage.value = "Փորձեք նորից մուտք գործել"
                    _isLoading.value = false
                    return@launch
                }

                // Prepare request body parts
                val brandBody = brand.toRequestBody("text/plain".toMediaTypeOrNull())
                val modelBody = model.toRequestBody("text/plain".toMediaTypeOrNull())
                val seatsBody = seats.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val colorBody = color.toRequestBody("text/plain".toMediaTypeOrNull())
                val plateBody = plate.toRequestBody("text/plain".toMediaTypeOrNull())
                
                // Prepare photo part if available
                var photoPart: okhttp3.MultipartBody.Part? = null
                if (photoUri != null) {
                    try {
                        // Convert Uri to file (simplified approach)
                        // In production, you'd want more robust file handling
                        val inputStream = context?.contentResolver?.openInputStream(photoUri)
                        if (inputStream != null) {
                            val bytes = inputStream.readBytes()
                            val photoBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                            photoPart = okhttp3.MultipartBody.Part.createFormData("photo", "car_photo.jpg", photoBody)
                            inputStream.close()
                            android.util.Log.d("TaxiApp", "Photo prepared for upload")
                        }
                    } catch (e: Exception) {
                        android.util.Log.w("TaxiApp", "Failed to prepare photo for upload: ${e.message}")
                        // Continue without photo if there's an error
                    }
                }

                android.util.Log.d("TaxiApp", "Calling vehicle registration API with: brand=$brand, model=$model, seats=$seats, color=$color, plate=$plate")
                android.util.Log.d("TaxiApp", "Authorization token: Bearer $token")
                android.util.Log.d("TaxiApp", "Equivalent curl command:")
                android.util.Log.d("TaxiApp", "curl -X POST http://api.tamojni.com/api/driver/vehicle \\")
                android.util.Log.d("TaxiApp", "  -H \"Authorization: Bearer $token\" \\")
                android.util.Log.d("TaxiApp", "  -F brand=$brand \\")
                android.util.Log.d("TaxiApp", "  -F model=$model \\")
                android.util.Log.d("TaxiApp", "  -F seats=$seats \\")
                android.util.Log.d("TaxiApp", "  -F color=$color \\")
                android.util.Log.d("TaxiApp", "  -F plate=\"$plate\"")

                val response = NetworkModule.apiService.registerVehicle(
                    token = "Bearer $token",
                    brand = brandBody,
                    model = modelBody,
                    seats = seatsBody,
                    color = colorBody,
                    plate = plateBody,
                    photo = photoPart
                )

                android.util.Log.d("TaxiApp", "Raw API response code: ${response.code()}")
                android.util.Log.d("TaxiApp", "Raw API response message: ${response.message()}")
                android.util.Log.d("TaxiApp", "Raw API response headers: ${response.headers()}")
                
                // Log raw response body for debugging
                val rawBody = response.body()
                android.util.Log.d("TaxiApp", "Raw response body type: ${rawBody?.javaClass?.name}")

                if (response.isSuccessful) {
                    val vehicleResponse = response.body()
                    android.util.Log.d("TaxiApp", "Vehicle registration API response: $vehicleResponse")
                    android.util.Log.d("TaxiApp", "Response code: ${response.code()}")
                    
                    // Check if response indicates success with multiple criteria
                    val isSuccess = when {
                        // Check for explicit success indicators
                        vehicleResponse?.success == true -> true
                        vehicleResponse?.status == "success" -> true
                        vehicleResponse?.vehicle != null -> true
                        vehicleResponse?.data != null -> true
                        
                        // Check for success messages
                        vehicleResponse?.message?.contains("success", ignoreCase = true) == true -> true
                        vehicleResponse?.message?.contains("created", ignoreCase = true) == true -> true
                        vehicleResponse?.message?.contains("registered", ignoreCase = true) == true -> true
                        
                        // Check if no errors and good response code
                        vehicleResponse?.errors == null && response.code() in 200..299 -> true
                        
                        // If response body is null/empty but status code is success, assume success
                        vehicleResponse == null && response.code() in 200..299 -> true
                        
                        else -> false
                    }
                    
                    if (isSuccess) {
                        _successMessage.value = "Ավտոմեքենան հաջողությամբ գրանցվեց"
                        android.util.Log.d("TaxiApp", "Vehicle registered successfully")
                        android.util.Log.d("TaxiApp", "Success criteria met - vehicle: ${vehicleResponse?.vehicle}, message: ${vehicleResponse?.message}")
                        
                        // After vehicle registration, clear temporary auth data but keep driver mode
                        _currentUser.value = null
                        // Keep _appMode.value = AppMode.DRIVER to stay in driver flow
                        _authToken.value = null // Clear temporary token
                        _isAuthenticated.value = false
                        
                        // Show completion message and redirect directly to driver login
                        _successMessage.value = "Ավտոմեքենայի գրանցումը ավարտվեց: Խնդրում ենք մուտք գործել:"
                        _currentScreen.value = Screen.DriverLogin
                    } else {
                        _errorMessage.value = vehicleResponse?.message ?: "Ավտոմեքենայի գրանցումը չհաջողվեց"
                        android.util.Log.w("TaxiApp", "Vehicle registration failed: ${vehicleResponse?.message}")
                        android.util.Log.w("TaxiApp", "Errors: ${vehicleResponse?.errors}")
                    }
                } else {
                    android.util.Log.e("TaxiApp", "Vehicle registration API failed with code: ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("TaxiApp", "Error response body: $errorBody")
                    _errorMessage.value = when (response.code()) {
                        400 -> "Սխալ տվյալներ: Ստուգեք մուտքագրված տեղեկությունները"
                        401 -> "Թույլտվության խնդիր: Խնդրում ենք նորից մուտք գործել"
                        422 -> "Տվյալների վավերացման սխալ"
                        else -> "Սերվերի խնդիր: Խնդրում ենք նորից փորձել"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ինտերնետ կապի խնդիր: Ստուգեք ձեր կապը"
                android.util.Log.e("TaxiApp", "Vehicle registration error: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
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
            
            // Stop auto-refresh
            stopAutoRefresh()
            
            // Clear persistent authentication data
            authManager.clearAuthData()
            
            _currentUser.value = null
            _appMode.value = null
            _currentMapLocation.value = null
            _authToken.value = null // Clear the token
            _isAuthenticated.value = false
            _isNewDriverRegistration.value = false // Reset registration flag
            _currentScreen.value = Screen.Dashboard
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // Auto-refresh functions
    fun startAutoRefresh() {
        android.util.Log.d("TaxiApp", "Starting auto-refresh")
        _autoRefreshEnabled.value = true
        
        autoRefreshJob?.cancel() // Cancel any existing refresh job
        autoRefreshJob = viewModelScope.launch {
            while (_autoRefreshEnabled.value && _authToken.value != null) {
                delay(refreshIntervalMs)
                if (_autoRefreshEnabled.value && _authToken.value != null) {
                    android.util.Log.d("TaxiApp", "Auto-refreshing data")
                    refreshData()
                }
            }
        }
    }

    fun stopAutoRefresh() {
        android.util.Log.d("TaxiApp", "Stopping auto-refresh")
        _autoRefreshEnabled.value = false
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

    // Fetch driver published trips from API
    fun fetchDriverTrips() {
        viewModelScope.launch {
            try {
                val token = _authToken.value
                if (token != null) {
                    android.util.Log.d("TaxiApp", "Fetching driver trips from API")
                    
                    val response = NetworkModule.apiService.getDriverTrips(
                        token = "Bearer $token",
                        status = "published",
                        perPage = 20
                    )
                    
                    if (response.isSuccessful) {
                        val tripsResponse = response.body()
                        if (tripsResponse?.data != null) {
                            _driverPublishedTrips.value = tripsResponse.data
                            android.util.Log.d("TaxiApp", "Successfully loaded ${tripsResponse.data.size} driver trips")
                        } else {
                            android.util.Log.w("TaxiApp", "No trips data in response")
                            _driverPublishedTrips.value = emptyList()
                        }
                    } else {
                        android.util.Log.e("TaxiApp", "Failed to fetch driver trips: ${response.code()}")
                        _errorMessage.value = "Չհաջողվեց բեռնել ուղևորությունները"
                    }
                } else {
                    android.util.Log.w("TaxiApp", "No auth token available for fetching driver trips")
                }
            } catch (e: Exception) {
                android.util.Log.e("TaxiApp", "Error fetching driver trips", e)
                _errorMessage.value = "Ինտերնետ կապի խնդիր: Խնդրում ենք ստուգել ձեր կապը"
            }
        }
    }

    private fun refreshData() {
        // Refresh trips data for all users
        loadTrips()
        
        // Refresh requests data for clients
        if (_currentUser.value?.role == "client") {
            loadMyRequests()
        }
        
        // Refresh driver trips for drivers
        if (_currentUser.value?.role == "driver") {
            fetchDriverTrips()
        }
        
        // Additional refresh calls can be added here based on user role if needed
        android.util.Log.d("TaxiApp", "Data refreshed for user role: ${_currentUser.value?.role}")
    }

    // Manual refresh function that can be called by UI
    fun refreshDataManually() {
        android.util.Log.d("TaxiApp", "Manual data refresh requested")
        refreshData()
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoRefresh()
    }
}