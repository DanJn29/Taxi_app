package com.example.taxi_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taxi_app.data.*
import com.example.taxi_app.ui.screens.*
import com.example.taxi_app.ui.screens.client.*
import com.example.taxi_app.ui.screens.driver.*
import com.example.taxi_app.ui.screens.company.*
import com.example.taxi_app.ui.theme.Taxi_appTheme
import com.example.taxi_app.viewmodel.TaxiViewModel
import com.example.taxi_app.viewmodel.TaxiViewModelFactory

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
    val context = LocalContext.current
    val viewModel: TaxiViewModel = viewModel(factory = TaxiViewModelFactory(context))
    
    val appMode by viewModel.appMode.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    
    // Company data
    val company by viewModel.company.collectAsState()
    val vehicles by viewModel.vehicles.collectAsState()
    val members by viewModel.members.collectAsState()
    val trips by viewModel.trips.collectAsState()
    val requests by viewModel.requests.collectAsState()
    
    // Map location data
    val currentMapLocation by viewModel.currentMapLocation.collectAsState()
    
    // Loading and error states
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    
    // Client data
    val availableTrips by viewModel.availableTrips.collectAsState()
    val selectedTrip by viewModel.selectedTrip.collectAsState()
    val clientBookings by viewModel.clientBookings.collectAsState()
    
    // Driver data
    val driverStats by viewModel.driverStats.collectAsState()
    val driverTrips by viewModel.driverTrips.collectAsState()
    val driverPublishedTrips by viewModel.driverPublishedTrips.collectAsState()

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
                        onBackToModeSelector = viewModel::resetToModeSelector,
                        onRegister = { viewModel.navigateToScreen(Screen.CompanyRegister) },
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onClearError = viewModel::clearError
                    )
                }
                Screen.CompanyRegister -> {
                    CompanyRegisterScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateToScreen(Screen.CompanyLogin) },
                        onNavigateToLogin = { viewModel.navigateToScreen(Screen.CompanyLogin) }
                    )
                }
                Screen.Dashboard -> {
                    DashboardScreen(
                        company = company,
                        navigationScrollState = viewModel.navigationScrollState,
                        onNavigate = viewModel::navigateToScreen,
                        onLogout = viewModel::logout
                    )
                }
                Screen.Fleet -> {
                    FleetScreen(
                        company = company,
                        vehicles = vehicles,
                        navigationScrollState = viewModel.navigationScrollState,
                        onNavigate = viewModel::navigateToScreen,
                        onAddVehicle = viewModel::addVehicle,
                        onLogout = viewModel::logout
                    )
                }
                Screen.Members -> {
                    MembersScreen(
                        company = company,
                        members = members,
                        navigationScrollState = viewModel.navigationScrollState,
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
                        currentMapLocation = currentMapLocation,
                        navigationScrollState = viewModel.navigationScrollState,
                        onNavigate = viewModel::navigateToScreen,
                        onAddTrip = viewModel::addTrip,
                        onPublishTrip = viewModel::publishTrip,
                        onArchiveTrip = viewModel::archiveTrip,
                        onUnarchiveTrip = viewModel::unarchiveTrip,
                        onUpdateMapLocation = viewModel::updateCurrentMapLocation,
                        onLogout = viewModel::logout
                    )
                }
                Screen.Requests -> {
                    RequestsScreen(
                        company = company,
                        requests = requests.filter { it.status == "pending" },
                        navigationScrollState = viewModel.navigationScrollState,
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
                        onBackToModeSelector = viewModel::resetToModeSelector,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onClearError = viewModel::clearError
                    )
                }
                Screen.ClientRegister -> {
                    ClientRegisterScreen(
                        onRegister = viewModel::registerClient,
                        onNavigateToLogin = { viewModel.navigateToScreen(Screen.ClientLogin) },
                        onBackToLogin = { viewModel.navigateToScreen(Screen.ClientLogin) },
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        successMessage = successMessage,
                        onClearError = viewModel::clearError,
                        onClearSuccess = viewModel::clearSuccessMessage
                    )
                }
                Screen.ClientHome -> {
                    currentUser?.let { user ->
                        ClientHomeScreen(
                            user = user,
                            availableTrips = availableTrips,
                            onTripSelected = { trip ->
                                // Store selected trip and navigate to booking
                                viewModel.selectTrip(trip)
                                viewModel.navigateToScreen(Screen.ClientBooking)
                            },
                            onProfileClicked = { viewModel.navigateToScreen(Screen.ClientProfile) },
                            onHistoryClicked = { viewModel.navigateToScreen(Screen.ClientHistory) },
                            onRequestsClicked = { viewModel.navigateToScreen(Screen.ClientRequests) },
                            onLogout = viewModel::logout,
                            viewModel = viewModel
                        )
                    }
                }
                Screen.ClientBooking -> {
                    currentUser?.let { user ->
                        selectedTrip?.let { trip ->
                            ClientBookingScreen(
                                trip = trip,
                                user = user,
                                viewModel = viewModel,
                                onBookTrip = { seats, payment, notes ->
                                    viewModel.bookTrip(trip.id, seats, payment, notes)
                                    viewModel.clearSelectedTrip()
                                    viewModel.navigateToScreen(Screen.ClientHome)
                                },
                                onBack = { 
                                    viewModel.clearSelectedTrip()
                                    viewModel.navigateToScreen(Screen.ClientHome) 
                                }
                            )
                        } ?: run {
                            // If no trip is selected, go back to home
                            LaunchedEffect(Unit) {
                                viewModel.navigateToScreen(Screen.ClientHome)
                            }
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
                Screen.ClientRequests -> {
                    ClientRequestsScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateToScreen(Screen.ClientHome) }
                    )
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
                        onBackToModeSelector = viewModel::resetToModeSelector,
                        onNavigateToRegister = { viewModel.navigateToScreen(Screen.DriverRegister) },
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onClearError = viewModel::clearError
                    )
                }
                Screen.DriverRegister -> {
                    DriverRegisterScreen(
                        onRegister = viewModel::registerDriver,
                        onNavigateToLogin = { viewModel.navigateToScreen(Screen.DriverLogin) },
                        onBackToLogin = { viewModel.navigateToScreen(Screen.DriverLogin) },
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        successMessage = successMessage,
                        onClearError = viewModel::clearError,
                        onClearSuccess = viewModel::clearSuccessMessage
                    )
                }
                Screen.DriverVehicleSetup -> {
                    currentUser?.let { driver ->
                        DriverVehicleSetupScreen(
                            user = driver,
                            viewModel = viewModel,
                            onVehicleRegistered = { 
                                viewModel.navigateToScreen(Screen.DriverDashboard)
                            },
                            onBack = { viewModel.navigateToScreen(Screen.DriverLogin) }
                        )
                    }
                }
                Screen.DriverDashboard -> {
                    currentUser?.let { driver ->
                        DriverDashboardScreen(
                            driver = driver,
                            stats = driverStats,
                            availableTrips = driverTrips.filter { it.status == "published" },
                            publishedTrips = driverPublishedTrips,
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