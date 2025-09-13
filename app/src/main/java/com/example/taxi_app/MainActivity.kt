package com.example.taxi_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taxi_app.data.*
import com.example.taxi_app.data.api.TripDataV2
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
    val tripsV2 by viewModel.tripsV2.collectAsState()
    val selectedTrip by viewModel.selectedTrip.collectAsState()
    val selectedTripId by viewModel.selectedTripId.collectAsState()
    val clientBookings by viewModel.clientBookings.collectAsState()
    
    // Driver data
    val driverStats by viewModel.driverStats.collectAsState()
    val driverTrips by viewModel.driverTrips.collectAsState()
    val driverPublishedTrips by viewModel.driverPublishedTrips.collectAsState()
    val selectedDriverTrip by viewModel.selectedDriverTrip.collectAsState()
    val driverVehicle by viewModel.driverVehicle.collectAsState()
    val amenities by viewModel.amenities.collectAsState()
    val driverRequestNotificationCount by viewModel.driverRequestNotificationCount.collectAsState()

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
                            availableTrips = tripsV2,
                            onTripSelected = { trip: TripDataV2 ->
                                // Store selected trip ID and navigate to trip details
                                viewModel.selectTripIdForDetails(trip.id)
                                viewModel.navigateToScreen(Screen.ClientTripDetails)
                            },
                            onProfileClicked = { viewModel.navigateToScreen(Screen.ClientProfile) },
                            onHistoryClicked = { viewModel.navigateToScreen(Screen.ClientHistory) },
                            onRequestsClicked = { viewModel.navigateToScreen(Screen.ClientRequests) },
                            onLogout = viewModel::logout,
                            viewModel = viewModel
                        )
                    }
                }
                Screen.ClientTripDetails -> {
                    currentUser?.let { user ->
                        selectedTripId?.let { selectedTripId ->
                            ClientTripDetailsScreen(
                                tripId = selectedTripId,
                                user = user,
                                viewModel = viewModel,
                                onBack = { 
                                    viewModel.clearSelectedTrip()
                                    viewModel.navigateToScreen(Screen.ClientHome) 
                                },
                                onBookTrip = { _, seats, notes ->
                                    viewModel.bookTrip(selectedTripId.toString(), seats.toInt(), "cash", notes)
                                    viewModel.clearSelectedTrip()
                                    viewModel.navigateToScreen(Screen.ClientHome)
                                }
                            )
                        } ?: run {
                            // If no trip ID is selected, go back to home
                            LaunchedEffect(Unit) {
                                viewModel.navigateToScreen(Screen.ClientHome)
                            }
                        }
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
                            driverVehicle = driverVehicle,
                            onAcceptTrip = viewModel::acceptDriverTrip,
                            onViewEarnings = { viewModel.navigateToScreen(Screen.DriverEarnings) },
                            onViewRequests = { viewModel.navigateToScreen(Screen.DriverRequests) },
                            onViewTrips = { viewModel.navigateToScreen(Screen.DriverTrips) },
                            onAddTrip = { viewModel.navigateToScreen(Screen.AddTrip) },
                            onViewProfile = { viewModel.navigateToScreen(Screen.DriverProfile) },
                            onLogout = viewModel::logout,
                            onShowMessage = { message ->
                                // Show toast message
                                android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show()
                            },
                            onReloadVehicle = { viewModel.loadDriverVehicle() },
                            successMessage = successMessage,
                            onClearSuccessMessage = viewModel::clearSuccessMessage,
                            errorMessage = errorMessage,
                            onClearErrorMessage = viewModel::clearErrorMessage,
                            driverRequestNotificationCount = driverRequestNotificationCount,
                            onClearDriverRequestNotifications = viewModel::clearDriverRequestNotifications
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
                            onToggleAvailability = viewModel::toggleDriverAvailability,
                            onLogout = viewModel::logout
                        )
                    }
                }
                Screen.DriverRequests -> {
                    DriverRequestsScreen(
                        viewModel = viewModel,
                        navigationScrollState = viewModel.navigationScrollState,
                        onNavigate = { screen -> viewModel.navigateToScreen(screen) }
                    )
                }
                Screen.DriverTrips -> {
                    DriverTripsScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateToScreen(Screen.DriverDashboard) },
                        onTripClick = { trip -> viewModel.navigateToTripDetails(trip) }
                    )
                }
                Screen.AddTrip -> {
                    AddTripScreen(
                        amenities = amenities,
                        driverVehicle = driverVehicle,
                        currentMapLocation = currentMapLocation,
                        successMessage = successMessage,
                        errorMessage = errorMessage,
                        onCreateTrip = viewModel::createTrip,
                        onBack = { viewModel.navigateToScreen(Screen.DriverDashboard) },
                        onLoadAmenities = viewModel::fetchAmenities,
                        onMapLocationUpdate = viewModel::updateCurrentMapLocation,
                        onClearSuccess = viewModel::clearSuccessMessage,
                        onClearError = viewModel::clearErrorMessage
                    )
                }
                Screen.TripDetails -> {
                    selectedDriverTrip?.let { trip ->
                        TripDetailsScreen(
                            trip = trip,
                            onBackClick = { viewModel.navigateToScreen(Screen.DriverTrips) }
                        )
                    }
                }
                else -> {}
            }
        }
    }
}