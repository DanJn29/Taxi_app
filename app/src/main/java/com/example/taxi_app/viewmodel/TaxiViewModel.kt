package com.example.taxi_app.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxi_app.data.*
import org.osmdroid.util.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
    
    // Map Location State - preserves current location across navigation
    private val _currentMapLocation = MutableStateFlow<GeoPoint?>(null)
    val currentMapLocation: StateFlow<GeoPoint?> = _currentMapLocation.asStateFlow()
    
    // Navigation Scroll State - preserves navbar scroll position across navigation
    val navigationScrollState = LazyListState()
    
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
            // Sample vehicles
            val sampleVehicles = listOf(
                Vehicle("1", "Toyota", "Camry", "01AM123", "#0ea5e9", 4, isAvailable = true),
                Vehicle("2", "Honda", "Civic", "02BN456", "#ef4444", 4, isAvailable = true),
                Vehicle("3", "Mercedes", "E-Class", "03CD789", "#000000", 5, isAvailable = false)
            )
            _vehicles.value = sampleVehicles
            
            // Sample members
            val sampleMembers = listOf(
                User("2", "Armen Sargsyan", "armen@example.com", "driver", "+374 77 123456", rating = 4.8f, totalTrips = 156),
                User("3", "Nare Hakobyan", "nare@example.com", "driver", "+374 77 234567", rating = 4.9f, totalTrips = 203),
                User("4", "Davit Petrosyan", "davit@example.com", "dispatcher", "+374 77 345678")
            )
            _members.value = sampleMembers
            
            // Sample trips for company
            val sampleTrips = listOf(
                Trip("1", "1", "2", "Yerevan Center", "Zvartnots Airport", 40.1776, 44.5126, 40.1596, 44.3931, 5000, 4, 2, "2025-08-26 14:30", status = "published", vehicle = sampleVehicles[0], driver = sampleMembers[0], distance = "15 km", duration = "25 min"),
                Trip("2", "2", "3", "Republic Square", "Vernissage Market", 40.1776, 44.5126, 40.1796, 44.5146, 2000, 4, 1, "2025-08-26 16:00", status = "published", vehicle = sampleVehicles[1], driver = sampleMembers[1], distance = "3 km", duration = "10 min")
            )
            _trips.value = sampleTrips
            
            // Available trips for clients (published trips with available seats)
            _availableTrips.value = sampleTrips.filter { it.status == "published" && it.seatsTaken < it.seatsTotal }
            
            // Driver trips (for driver dashboard)
            _driverTrips.value = sampleTrips
            
            // Sample driver stats
            _driverStats.value = DriverStats(
                totalEarnings = 1250000,
                totalTrips = 156,
                rating = 4.8f,
                todayEarnings = 25000,
                todayTrips = 5,
                pendingTrips = 3
            )
            
            updateCompanyStats()
        }
    }
    
    // App Mode Management
    fun selectAppMode(mode: AppMode) {
        _appMode.value = mode
        when (mode) {
            AppMode.COMPANY -> _currentScreen.value = Screen.CompanyLogin
            AppMode.CLIENT -> _currentScreen.value = Screen.ClientLogin
            AppMode.DRIVER -> _currentScreen.value = Screen.DriverLogin
        }
    }
    
    fun resetToModeSelector() {
        _appMode.value = null
        _currentUser.value = null
    }
    
    // Authentication
    fun loginCompany(email: String, password: String) {
        viewModelScope.launch {
            // Simulate company login
            val user = User("company_admin1", "Company Admin", email, "owner", "+374 77 123456", isVerified = true)
            _currentUser.value = user
            _currentScreen.value = Screen.Dashboard
        }
    }
    
    fun loginClient(email: String, password: String) {
        viewModelScope.launch {
            // Simulate login
            val user = User("client1", "John Client", email, "client", "+374 77 999888", isVerified = true, rating = 4.5f, totalTrips = 12)
            _currentUser.value = user
            _currentScreen.value = Screen.ClientHome
        }
    }
    
    fun registerClient(name: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            // Simulate registration
            val user = User("client_new", name, email, "client", phone, isVerified = false, rating = 0f, totalTrips = 0)
            _currentUser.value = user
            _currentScreen.value = Screen.ClientHome
        }
    }
    
    fun loginDriver(email: String, password: String) {
        viewModelScope.launch {
            // Simulate driver login - create a default driver if none exists
            var driver = _members.value.find { it.email == email && it.role == "driver" }
            
            if (driver == null) {
                // Create a default driver for demonstration
                driver = User(
                    id = "driver1", 
                    name = "Driver User", 
                    email = email, 
                    role = "driver",
                    phone = "+374 77 555666",
                    isVerified = true,
                    rating = 4.8f,
                    totalTrips = 125
                )
                // Add driver to members list
                _members.value = _members.value + driver
            }
            
            _currentUser.value = driver
            _currentScreen.value = Screen.DriverDashboard
        }
    }
    
    // Navigation
    fun navigateToScreen(screen: Screen) {
        _currentScreen.value = screen
    }
    
    // Company Management
    fun addVehicle(brand: String, model: String, plate: String, color: String, seats: Int) {
        viewModelScope.launch {
            val newVehicle = Vehicle(
                id = (vehicles.value.size + 1).toString(),
                brand = brand,
                model = model,
                plate = plate,
                color = color,
                seats = seats,
                isAvailable = true
            )
            _vehicles.value = _vehicles.value + newVehicle
            updateCompanyStats()
        }
    }
    
    fun addMember(name: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            val newMember = User(
                id = (members.value.size + 2).toString(),
                name = name,
                email = email,
                role = role,
                phone = "",
                rating = 0f,
                totalTrips = 0
            )
            _members.value = _members.value + newMember
            updateCompanyStats()
        }
    }
    
    fun addTrip(trip: Trip) {
        viewModelScope.launch {
            _trips.value = _trips.value + trip
            if (trip.status == "published") {
                _availableTrips.value = _availableTrips.value + trip
            }
            updateCompanyStats()
        }
    }
    
    fun publishTrip(tripId: String) {
        viewModelScope.launch {
            _trips.value = _trips.value.map { trip ->
                if (trip.id == tripId) {
                    val publishedTrip = trip.copy(status = "published")
                    // Add to available trips if not already there
                    if (_availableTrips.value.none { it.id == tripId }) {
                        _availableTrips.value = _availableTrips.value + publishedTrip
                    }
                    publishedTrip
                } else trip
            }
        }
    }
    
    fun archiveTrip(tripId: String) {
        viewModelScope.launch {
            _trips.value = _trips.value.map { trip ->
                if (trip.id == tripId) {
                    trip.copy(status = "archived")
                } else trip
            }
            // Remove from available trips
            _availableTrips.value = _availableTrips.value.filter { it.id != tripId }
        }
    }
    
    fun unarchiveTrip(tripId: String) {
        viewModelScope.launch {
            _trips.value = _trips.value.map { trip ->
                if (trip.id == tripId) {
                    trip.copy(status = "draft")
                } else trip
            }
        }
    }
    
    fun acceptRequest(requestId: String) {
        viewModelScope.launch {
            _requests.value = _requests.value.map { request ->
                if (request.id == requestId) {
                    request.copy(status = "accepted")
                } else request
            }
            updateCompanyStats()
        }
    }
    
    fun declineRequest(requestId: String) {
        viewModelScope.launch {
            _requests.value = _requests.value.map { request ->
                if (request.id == requestId) {
                    request.copy(status = "declined")
                } else request
            }
            updateCompanyStats()
        }
    }
    
    // Client Functions
    fun bookTrip(tripId: String, seats: Int, paymentMethod: String, notes: String) {
        viewModelScope.launch {
            val trip = _availableTrips.value.find { it.id == tripId }
            val user = _currentUser.value
            
            if (trip != null && user != null) {
                val booking = Booking(
                    id = System.currentTimeMillis().toString(),
                    userId = user.id,
                    tripId = tripId,
                    seats = seats,
                    totalPrice = trip.priceAmd * seats,
                    paymentMethod = paymentMethod,
                    status = "confirmed",
                    pickupLocation = LocationData(trip.fromAddr, trip.fromLat, trip.fromLng),
                    dropoffLocation = LocationData(trip.toAddr, trip.toLat, trip.toLng),
                    trip = trip,
                    driver = trip.driver ?: User("", "", "", ""),
                    createdAt = "2025-08-26 12:00"
                )
                
                _clientBookings.value = _clientBookings.value + booking
                
                // Update trip seats taken
                _availableTrips.value = _availableTrips.value.map { 
                    if (it.id == tripId) it.copy(seatsTaken = it.seatsTaken + seats) 
                    else it 
                }
            }
        }
    }
    
    // Driver Functions
    fun acceptDriverTrip(tripId: String) {
        viewModelScope.launch {
            // Update trip status to active
            _driverTrips.value = _driverTrips.value.map { trip ->
                if (trip.id == tripId) {
                    trip.copy(status = "active")
                } else trip
            }
            
            // Update driver stats
            val currentStats = _driverStats.value
            _driverStats.value = currentStats.copy(
                todayTrips = currentStats.todayTrips + 1,
                pendingTrips = currentStats.pendingTrips - 1
            )
        }
    }
    
    fun toggleDriverAvailability() {
        // This would update driver availability status in a real app
    }
    
    // Utility Functions
    private fun updateCompanyStats() {
        val vehicleCount = _vehicles.value.size
        val tripCount = _trips.value.size
        val pendingRequestCount = _requests.value.count { it.status == "pending" }
        
        _company.value = _company.value.copy(
            vehiclesCount = vehicleCount,
            tripsCount = tripCount,
            pendingRequests = pendingRequestCount
        )
    }
    
    fun getDrivers(): List<User> {
        return _members.value.filter { it.role == "driver" }
    }
    
    // Map location functions
    fun updateCurrentMapLocation(location: GeoPoint) {
        _currentMapLocation.value = location
    }
    
    fun logout() {
        _currentUser.value = null
        _appMode.value = null
        _currentMapLocation.value = null // Reset map location on logout
    }
}
