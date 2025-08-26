package com.example.taxi_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taxi_app.data.*
import com.example.taxi_app.ui.screens.*
import com.example.taxi_app.ui.screens.client.*
import com.example.taxi_app.ui.screens.driver.*
import com.example.taxi_app.ui.theme.Taxi_appTheme
import com.example.taxi_app.viewmodel.TaxiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Taxi_appTheme {
                TaxiApp()
            }
        }
    }
}

@Composable
fun TaxiApp() {
    val viewModel: TaxiViewModel = viewModel()
    
    val appMode by viewModel.appMode.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    
    // Company data
    val company by viewModel.company.collectAsState()
    val vehicles by viewModel.vehicles.collectAsState()
    val members by viewModel.members.collectAsState()
    val trips by viewModel.trips.collectAsState()
    val requests by viewModel.requests.collectAsState()
    
    // Client data
    val availableTrips by viewModel.availableTrips.collectAsState()
    val clientBookings by viewModel.clientBookings.collectAsState()
    
    // Driver data
    val driverStats by viewModel.driverStats.collectAsState()
    val driverTrips by viewModel.driverTrips.collectAsState()

    when {
        appMode == null -> {
            // App Mode Selector
            AppModeSelector(
                onModeSelected = viewModel::selectAppMode
            )
        }
        
        appMode == AppMode.COMPANY -> {
            // Company Management Screens
            when (currentScreen) {
                Screen.CompanyLogin -> {
                    CompanyLoginScreen(
                        onLogin = viewModel::loginCompany,
                        onBackToModeSelector = viewModel::resetToModeSelector
                    )
                }
                Screen.Dashboard -> {
                    DashboardScreen(
                        company = company,
                        onNavigate = viewModel::navigateToScreen,
                        onLogout = viewModel::logout
                    )
                }
                Screen.Fleet -> {
                    FleetScreen(
                        company = company,
                        vehicles = vehicles,
                        onNavigate = viewModel::navigateToScreen,
                        onAddVehicle = viewModel::addVehicle,
                        onLogout = viewModel::logout
                    )
                }
                Screen.Members -> {
                    MembersScreen(
                        company = company,
                        members = members,
                        onNavigate = viewModel::navigateToScreen,
                        onAddMember = viewModel::addMember,
                        onLogout = viewModel::logout
                    )
                }
                Screen.Trips -> {
                    TripsScreen(
                        company = company,
                        trips = trips,
                        vehicles = vehicles,
                        drivers = viewModel.getDrivers(),
                        onNavigate = viewModel::navigateToScreen,
                        onAddTrip = viewModel::addTrip,
                        onPublishTrip = viewModel::publishTrip,
                        onArchiveTrip = viewModel::archiveTrip,
                        onUnarchiveTrip = viewModel::unarchiveTrip,
                        onLogout = viewModel::logout
                    )
                }
                Screen.Requests -> {
                    RequestsScreen(
                        company = company,
                        requests = requests.filter { it.status == "pending" },
                        onNavigate = viewModel::navigateToScreen,
                        onAcceptRequest = viewModel::acceptRequest,
                        onDeclineRequest = viewModel::declineRequest,
                        onLogout = viewModel::logout
                    )
                }
                else -> {}
            }
        }
        
        appMode == AppMode.CLIENT -> {
            // Client Screens
            when (currentScreen) {
                Screen.ClientLogin -> {
                    ClientLoginScreen(
                        onLogin = viewModel::loginClient,
                        onNavigateToRegister = { viewModel.navigateToScreen(Screen.ClientRegister) },
                        onBackToModeSelector = viewModel::resetToModeSelector
                    )
                }
                Screen.ClientRegister -> {
                    ClientRegisterScreen(
                        onRegister = viewModel::registerClient,
                        onNavigateToLogin = { viewModel.navigateToScreen(Screen.ClientLogin) },
                        onBackToLogin = { viewModel.navigateToScreen(Screen.ClientLogin) }
                    )
                }
                Screen.ClientHome -> {
                    currentUser?.let { user ->
                        ClientHomeScreen(
                            user = user,
                            availableTrips = availableTrips,
                            onTripSelected = { trip ->
                                // Store selected trip and navigate to booking
                                viewModel.navigateToScreen(Screen.ClientBooking)
                            },
                            onProfileClicked = { viewModel.navigateToScreen(Screen.ClientProfile) },
                            onHistoryClicked = { viewModel.navigateToScreen(Screen.ClientHistory) },
                            onLogout = viewModel::logout
                        )
                    }
                }
                Screen.ClientBooking -> {
                    currentUser?.let { user ->
                        // For simplicity, using first available trip
                        availableTrips.firstOrNull()?.let { trip ->
                            ClientBookingScreen(
                                trip = trip,
                                user = user,
                                onBookTrip = { seats, payment, notes ->
                                    viewModel.bookTrip(trip.id, seats, payment, notes)
                                    viewModel.navigateToScreen(Screen.ClientHome)
                                },
                                onBack = { viewModel.navigateToScreen(Screen.ClientHome) }
                            )
                        }
                    }
                }
                Screen.ClientHistory -> {
                    currentUser?.let { user ->
                        ClientHistoryScreen(
                            user = user,
                            bookings = clientBookings,
                            onBack = { viewModel.navigateToScreen(Screen.ClientHome) }
                        )
                    }
                }
                Screen.ClientProfile -> {
                    currentUser?.let { user ->
                        ClientProfileScreen(
                            user = user,
                            onBack = { viewModel.navigateToScreen(Screen.ClientHome) },
                            onLogout = viewModel::logout
                        )
                    }
                }
                else -> {}
            }
        }
        
        appMode == AppMode.DRIVER -> {
            // Driver Screens
            when (currentScreen) {
                Screen.DriverLogin -> {
                    DriverLoginScreen(
                        onLogin = viewModel::loginDriver,
                        onBackToModeSelector = viewModel::resetToModeSelector
                    )
                }
                Screen.DriverDashboard -> {
                    currentUser?.let { driver ->
                        DriverDashboardScreen(
                            driver = driver,
                            stats = driverStats,
                            availableTrips = driverTrips.filter { it.status == "published" },
                            onAcceptTrip = viewModel::acceptDriverTrip,
                            onToggleAvailability = viewModel::toggleDriverAvailability,
                            onViewEarnings = { viewModel.navigateToScreen(Screen.DriverEarnings) },
                            onViewProfile = { viewModel.navigateToScreen(Screen.DriverProfile) },
                            onLogout = viewModel::logout
                        )
                    }
                }
                Screen.DriverEarnings -> {
                    currentUser?.let { driver ->
                        DriverEarningsScreen(
                            driver = driver,
                            stats = driverStats,
                            onBack = { viewModel.navigateToScreen(Screen.DriverDashboard) }
                        )
                    }
                }
                Screen.DriverProfile -> {
                    currentUser?.let { driver ->
                        DriverProfileScreen(
                            driver = driver,
                            stats = driverStats,
                            onBack = { viewModel.navigateToScreen(Screen.DriverDashboard) },
                            onLogout = viewModel::logout
                        )
                    }
                }
                else -> {}
            }
        }
    }
}