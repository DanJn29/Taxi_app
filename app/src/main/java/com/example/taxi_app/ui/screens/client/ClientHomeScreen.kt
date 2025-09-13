package com.example.taxi_app.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.data.*
import com.example.taxi_app.data.api.TripDataV2
import com.example.taxi_app.ui.components.*
import com.example.taxi_app.ui.theme.*
import com.example.taxi_app.viewmodel.TaxiViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    user: User,
    availableTrips: List<TripDataV2>,
    onTripSelected: (TripDataV2) -> Unit,
    onProfileClicked: () -> Unit,
    onHistoryClicked: () -> Unit,
    onRequestsClicked: () -> Unit,
    onLogout: () -> Unit,
    viewModel: TaxiViewModel? = null
) {
    var searchFromLocation by remember { mutableStateOf("") }
    var searchToLocation by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    
    // Collect states from ViewModel if available
    viewModel?.let { vm ->
        searchFromLocation = vm.searchFromLocation.collectAsState().value
        searchToLocation = vm.searchToLocation.collectAsState().value
    }
    
    val filterMinPrice = viewModel?.filterMinPrice?.collectAsState()?.value
    val filterMaxPrice = viewModel?.filterMaxPrice?.collectAsState()?.value
    val filterMinSeats = viewModel?.filterMinSeats?.collectAsState()?.value
    val filterPaymentMethods = viewModel?.filterPaymentMethods?.collectAsState()?.value ?: emptyList()
    
    // Notification states
    val unreadNotificationsCount = viewModel?.unreadNotificationsCount?.collectAsState()?.value ?: 0
    val hasNewAcceptedRequests = viewModel?.hasNewAcceptedRequests?.collectAsState()?.value ?: false
    val hasNewRejectedRequests = viewModel?.hasNewRejectedRequests?.collectAsState()?.value ?: false
    val latestNotificationMessage = viewModel?.latestNotificationMessage?.collectAsState()?.value
    
    // Local state for showing notification snackbar
    var showNotificationSnackbar by remember { mutableStateOf(false) }
    var notificationMessage by remember { mutableStateOf("") }
    
    // Show notification when new accepted or rejected requests are detected
    LaunchedEffect(hasNewAcceptedRequests, hasNewRejectedRequests) {
        if ((hasNewAcceptedRequests || hasNewRejectedRequests) && latestNotificationMessage != null) {
            notificationMessage = latestNotificationMessage
            showNotificationSnackbar = true
        }
    }
    
    // Load trips when screen first appears
    LaunchedEffect(Unit) {
        viewModel?.loadTripsV2()
    }
    
    // Count active filters
    val activeFiltersCount = listOfNotNull(
        if (searchFromLocation.isNotEmpty()) 1 else null,
        if (searchToLocation.isNotEmpty()) 1 else null,
        filterMinPrice,
        filterMaxPrice,
        filterMinSeats,
        if (filterPaymentMethods.isNotEmpty()) 1 else null
    ).size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxiBackground)
    ) {
        // Top Bar
        TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Բարև, ${user.name.split(" ").firstOrNull() ?: ""}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TaxiBlack
                        )
                        Text(
                            text = "Ուր ենք գնալու այսօր?",
                            fontSize = 14.sp,
                            color = TaxiGray
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClicked) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Պրոֆիլ",
                            tint = TaxiBlack
                        )
                    }
                    IconButton(onClick = onHistoryClicked) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Պատմություն",
                            tint = TaxiBlack
                        )
                    }
                    IconButton(onClick = onRequestsClicked) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = "Հայտեր",
                                tint = TaxiBlack
                            )
                            // Notification badge
                            if (unreadNotificationsCount > 0) {
                                Badge(
                                    modifier = Modifier.align(Alignment.TopEnd),
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = if (unreadNotificationsCount > 9) "9+" else unreadNotificationsCount.toString(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TaxiYellow
                )
            )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxiWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Գտիր երթուղի",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TaxiBlack,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    TaxiTextField(
                        value = searchFromLocation,
                        onValueChange = { viewModel?.updateSearchFromLocation(it) },
                        label = "Որտեղից",
                        placeholder = "Նշիր մեկնման կետը",
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    TaxiTextField(
                        value = searchToLocation,
                        onValueChange = { viewModel?.updateSearchToLocation(it) },
                        label = "Ուր",
                        placeholder = "Նշիր նպատակակետը",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { 
                                showFilterDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (activeFiltersCount > 0) TaxiBlue.copy(alpha = 0.1f) else androidx.compose.ui.graphics.Color.Transparent
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Ֆիլտր",
                                modifier = Modifier.size(16.dp),
                                tint = if (activeFiltersCount > 0) TaxiBlue else androidx.compose.ui.graphics.Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (activeFiltersCount > 0) "Ֆիլտր ($activeFiltersCount)" else "Ֆիլտր",
                                color = if (activeFiltersCount > 0) TaxiBlue else androidx.compose.ui.graphics.Color.Gray
                            )
                        }
                        
                        if (searchFromLocation.isNotEmpty() || searchToLocation.isNotEmpty()) {
                            OutlinedButton(
                                onClick = { viewModel?.clearAllFilters() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Մաքրել",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Մաքրել")
                            }
                        } else {
                            TaxiButton(
                                text = "Գտնել",
                                onClick = { 
                                    // Trigger search by applying current filters
                                    // This will be handled automatically by the ViewModel state changes
                                },
                                modifier = Modifier.weight(1f),
                                enabled = searchFromLocation.isNotEmpty() || searchToLocation.isNotEmpty()
                            )
                        }
                    }
                }
            }
            }
            
            // Available Trips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Հասանելի երթուղիներ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxiBlack
                    )
                    
                    if (activeFiltersCount > 0) {
                        Text(
                            text = "${availableTrips.size} արդյունք",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TaxiGray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (availableTrips.isEmpty()) {
                item {
                    EmptyState(
                        message = if (activeFiltersCount > 0) 
                            "Այս ֆիլտրերով երթուղիներ չգտնվեցին" 
                        else 
                            "Երթուղիներ չկան"
                    )
                }
            } else {
                items(availableTrips) { trip ->
                    TripCard(
                        trip = trip,
                        onTripSelected = { onTripSelected(trip) }
                    )
                }
            }
        }
    }
    
    // Show notification when new requests are accepted or rejected
    if (showNotificationSnackbar) {
        LaunchedEffect(Unit) {
            // Log the notification message
            android.util.Log.d("TaxiApp", "Showing notification: $notificationMessage")
            kotlinx.coroutines.delay(4000)
            showNotificationSnackbar = false
            viewModel?.clearNotificationMessage()
            // Don't reset notification count here - only when user opens requests page
        }
    }
    
    // Filter Dialog
    if (showFilterDialog) {
        FilterDialog(
            viewModel = viewModel,
            onDismiss = { showFilterDialog = false }
        )
    }
}

@Composable
private fun TripCard(
    trip: TripDataV2,
    onTripSelected: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Route info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FiberManualRecord,
                            contentDescription = null,
                            tint = StatusPublished,
                            modifier = Modifier.size(8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = trip.from_addr,
                            fontSize = 14.sp,
                            color = TaxiBlack,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = StatusError,
                            modifier = Modifier.size(8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = trip.to_addr,
                            fontSize = 14.sp,
                            color = TaxiBlack,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${trip.price_amd} AMD",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxiBlack
                    )
                    Text(
                        text = "Տեղեր՝ ${trip.seats_taken}/${trip.seats_total}",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                    if (trip.pending_requests_count > 0) {
                        Text(
                            text = "Հայտեր՝ ${trip.pending_requests_count}",
                            fontSize = 11.sp,
                            color = TaxiYellow,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Vehicle and driver info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxiYellow.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = TaxiBlack
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${trip.vehicle.brand} ${trip.vehicle.model}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TaxiBlack
                    )
                    Text(
                        text = "Վարորդ՝ ${trip.driver.name}",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                    // Vehicle details
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Համար՝ ${trip.vehicle.plate}",
                            fontSize = 12.sp,
                            color = TaxiGray
                        )
                        Text(
                            text = " • ${hexToArmenianColorName(trip.vehicle.color)}",
                            fontSize = 12.sp,
                            color = TaxiGray
                        )
                    }
                }
                
                TaxiButton(
                    text = "Ամրագրել",
                    onClick = onTripSelected,
                    modifier = Modifier.width(100.dp)
                )
            }
            
            // Departure time
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TaxiGray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Մեկնում՝ ${formatDepartureTime(trip.departure_at)}",
                    fontSize = 12.sp,
                    color = TaxiGray
                )
            }
            
            // Amenities
            if (trip.amenities.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TaxiGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Հարմարություններ՝ ${trip.amenities.joinToString(", ") { amenity -> amenity.name }}",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                }
            }
            
            // Payment methods
            if (trip.pay_methods.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TaxiGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Վճարման եղանակներ՝ ${trip.pay_methods.joinToString(", ")}",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDialog(
    viewModel: TaxiViewModel?,
    onDismiss: () -> Unit
) {
    val filterMaxPrice = viewModel?.filterMaxPrice?.collectAsState()?.value
    val filterMinSeats = viewModel?.filterMinSeats?.collectAsState()?.value
    val filterPaymentMethods = viewModel?.filterPaymentMethods?.collectAsState()?.value ?: emptyList()
    
    // Max price state for slider (in AMD)
    val maxPriceLimit = 50000f // Maximum price limit
    var maxPrice by remember { 
        mutableStateOf(filterMaxPrice?.toFloat() ?: 0f) // Start from 0 by default
    }
    var tempMinSeats by remember { mutableStateOf(filterMinSeats ?: 1) }
    var tempPaymentMethods by remember { mutableStateOf(filterPaymentMethods) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Ֆիլտրեր",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TaxiBlack
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Price Range
                Text(
                    text = "Առավելագույն գին (AMD)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                
                // Max price display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "0 AMD",
                        fontSize = 14.sp,
                        color = TaxiGray
                    )
                    Text(
                        text = "${maxPrice.toInt()} AMD",
                        fontSize = 16.sp,
                        color = TaxiBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                // Max Price Slider
                Slider(
                    value = maxPrice,
                    onValueChange = { maxPrice = it },
                    valueRange = 0f..maxPriceLimit,
                    steps = 49, // Creates 50 price points (1000 AMD steps)
                    colors = SliderDefaults.colors(
                        thumbColor = TaxiBlue,
                        activeTrackColor = TaxiBlue,
                        inactiveTrackColor = TaxiBlue.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Min/Max labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "0 AMD",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                    Text(
                        text = "${maxPriceLimit.toInt()} AMD",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                }
                
                // Minimum Seats
                Text(
                    text = "Նվազագույն տեղերի քանակ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                
                // Seats counter with +/- buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Decrease button
                    OutlinedButton(
                        onClick = { 
                            if (tempMinSeats > 1) tempMinSeats -= 1 
                        },
                        enabled = tempMinSeats > 1,
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (tempMinSeats > 1) TaxiBlue else TaxiGray,
                            disabledContentColor = TaxiGray.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(
                            width = 2.dp, 
                            color = if (tempMinSeats > 1) TaxiBlue else TaxiGray.copy(alpha = 0.5f)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Նվազեցնել",
                            modifier = Modifier.size(28.dp),
                            tint = if (tempMinSeats > 1) TaxiBlue else TaxiGray.copy(alpha = 0.5f)
                        )
                    }
                    
                    // Seats display
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = TaxiBlue.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "$tempMinSeats",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxiBlue,
                            modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp)
                        )
                    }
                    
                    // Increase button
                    OutlinedButton(
                        onClick = { 
                            if (tempMinSeats < 8) tempMinSeats += 1 
                        },
                        enabled = tempMinSeats < 8,
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (tempMinSeats < 8) TaxiBlue else TaxiGray,
                            disabledContentColor = TaxiGray.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(
                            width = 2.dp, 
                            color = if (tempMinSeats < 8) TaxiBlue else TaxiGray.copy(alpha = 0.5f)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ավելացնել",
                            modifier = Modifier.size(28.dp),
                            tint = if (tempMinSeats < 8) TaxiBlue else TaxiGray.copy(alpha = 0.5f)
                        )
                    }
                }
                
                // Payment Methods
                Text(
                    text = "Վճարման եղանակ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cash button
                    OutlinedButton(
                        onClick = {
                            tempPaymentMethods = if (tempPaymentMethods.contains("cash")) {
                                tempPaymentMethods - "cash"
                            } else {
                                tempPaymentMethods + "cash"
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (tempPaymentMethods.contains("cash")) {
                                TaxiBlue.copy(alpha = 0.1f)
                            } else {
                                Color.Transparent
                            },
                            contentColor = if (tempPaymentMethods.contains("cash")) {
                                TaxiBlue
                            } else {
                                TaxiGray
                            }
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (tempPaymentMethods.contains("cash")) {
                                TaxiBlue
                            } else {
                                TaxiGray.copy(alpha = 0.3f)
                            }
                        ),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (tempPaymentMethods.contains("cash")) {
                                    TaxiBlue
                                } else {
                                    TaxiGray
                                }
                            )
                            Text(
                                text = "Կանխիկ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    // Card button
                    OutlinedButton(
                        onClick = {
                            tempPaymentMethods = if (tempPaymentMethods.contains("card")) {
                                tempPaymentMethods - "card"
                            } else {
                                tempPaymentMethods + "card"
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (tempPaymentMethods.contains("card")) {
                                TaxiBlue.copy(alpha = 0.1f)
                            } else {
                                Color.Transparent
                            },
                            contentColor = if (tempPaymentMethods.contains("card")) {
                                TaxiBlue
                            } else {
                                TaxiGray
                            }
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (tempPaymentMethods.contains("card")) {
                                TaxiBlue
                            } else {
                                TaxiGray.copy(alpha = 0.3f)
                            }
                        ),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (tempPaymentMethods.contains("card")) {
                                    TaxiBlue
                                } else {
                                    TaxiGray
                                }
                            )
                            Text(
                                text = "Քարտ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Apply filters
                    val minPrice = 0 // Start from 0
                    val maxPriceValue = maxPrice.toInt()
                    val minSeats = tempMinSeats // Direct integer value
                    
                    viewModel?.updateFilterPriceRange(minPrice, maxPriceValue)
                    viewModel?.updateFilterMinSeats(minSeats)
                    viewModel?.updateFilterPaymentMethods(tempPaymentMethods)
                    
                    onDismiss()
                }
            ) {
                Text(
                    text = "Կիրառել",
                    color = TaxiBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    // Clear all filters
                    viewModel?.clearAllFilters()
                    onDismiss()
                }
            ) {
                Text(
                    text = "Մաքրել բոլորը",
                    color = TaxiGray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}

// Utility function to format departure time in a human-readable format
private fun formatDepartureTime(departureAt: String): String {
    return try {
        // Try parsing with timezone offset format first (e.g., "2025-09-10T06:19:00+00:00")
        val inputFormatWithOffset = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        var date = try {
            inputFormatWithOffset.parse(departureAt)
        } catch (e: Exception) {
            // Fallback to UTC format (e.g., "2024-12-15T14:30:00Z")
            val inputFormatUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormatUTC.timeZone = TimeZone.getTimeZone("UTC")
            inputFormatUTC.parse(departureAt)
        }
        
        if (date != null) {
            val departureCalendar = Calendar.getInstance()
            departureCalendar.time = date
            
            val dayFormat = SimpleDateFormat("EEEE", Locale("hy", "AM")) // Armenian day names
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateFormat = SimpleDateFormat("dd MMM", Locale("hy", "AM"))
            
            val dayName = dayFormat.format(date)
            val time = timeFormat.format(date)
            val dateStr = dateFormat.format(date)
            
            // Check if it's today, tomorrow, or another day
            val today = Calendar.getInstance()
            val tomorrow = Calendar.getInstance()
            tomorrow.add(Calendar.DAY_OF_YEAR, 1)
            
            when {
                isSameDay(departureCalendar, today) -> "Այսօր $time-ին"
                isSameDay(departureCalendar, tomorrow) -> "Վաղը $time-ին"
                departureCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> "$dayName, $dateStr $time-ին"
                else -> "$dateStr $time-ին"
            }
        } else {
            departureAt
        }
    } catch (e: Exception) {
        // Fallback to original string if parsing fails
        departureAt
    }
}

// Helper function to check if two calendar dates are the same day
private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

// Utility function to convert hex color codes to Armenian color names
private fun hexToArmenianColorName(hexColor: String): String {
    val cleanHex = hexColor.replace("#", "").lowercase()
    
    return when (cleanHex) {
        "ffffff", "fff" -> "սպիտակ"
        "000000", "000" -> "սև"
        "ff0000", "f00" -> "կարմիր"
        "00ff00", "0f0" -> "կանաչ"
        "10b981" -> "կանաչ"  // Emerald green
        "0000ff", "00f" -> "կապույտ"
        "ffff00", "ff0" -> "դեղին"
        "ff00ff", "f0f" -> "մանուշակագույն"
        "00ffff", "0ff" -> "երկնագույն"
        "808080", "888" -> "մոխրագույն"
        "c0c0c0" -> "արծաթագույն"
        "800000" -> "մուգ կարմիր"
        "008000" -> "մուգ կանաչ"
        "000080" -> "մուգ կապույտ"
        "808000" -> "ձիթապտղագույն"
        "800080" -> "մանուշակագույն"
        "008080" -> "կապտականաչ"
        "ffa500" -> "նարնջագույն"
        "ffc0cb" -> "վարդագույն"
        "a52a2a" -> "շագանակագույն"
        "daa520" -> "ոսկեգույն"
        "cd853f" -> "աղազգույն"
        "d2691e" -> "շոկոլադագույն"
        "8b4513" -> "մուգ շագանակագույն"
        "696969" -> "մուգ մոխրագույն"
        "2f4f4f" -> "մուգ մոխրագույն"
        "191970" -> "կեսգիշերային կապույտ"
        "dc143c" -> "ծիրանագույն"
        "b22222" -> "աղյուսագույն"
        "228b22" -> "անտառային կանաչ"
        "32cd32" -> "բաց կանաչ"
        "90ee90" -> "բաց կանաչ"
        "add8e6" -> "բաց կապույտ"
        "87ceeb" -> "երկնագույն"
        "f0f8ff" -> "բաց կապույտ"
        "f5f5dc" -> "բեժ"
        else -> {
            // Try to map common color names
            when {
                cleanHex.contains("red") -> "կարմիր"
                cleanHex.contains("blue") -> "կապույտ"
                cleanHex.contains("green") -> "կանաչ"
                cleanHex.contains("white") -> "սպիտակ"
                cleanHex.contains("black") -> "սև"
                cleanHex.contains("yellow") -> "դեղին"
                cleanHex.contains("orange") -> "նարնջագույն"
                cleanHex.contains("pink") -> "վարդագույն"
                cleanHex.contains("purple") -> "մանուշակագույն"
                cleanHex.contains("brown") -> "շագանակագույն"
                cleanHex.contains("gray") || cleanHex.contains("grey") -> "մոխրագույն"
                cleanHex.contains("silver") -> "արծաթագույն"
                cleanHex.contains("gold") -> "ոսկեգույն"
                else -> hexColor // Return original if no match found
            }
        }
    }
}
