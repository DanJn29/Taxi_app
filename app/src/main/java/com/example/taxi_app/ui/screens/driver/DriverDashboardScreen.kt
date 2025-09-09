package com.example.taxi_app.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.taxi_app.ui.components.*
import com.example.taxi_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    driver: User,
    stats: DriverStats,
    availableTrips: List<Trip>,
    publishedTrips: List<com.example.taxi_app.data.api.DriverTripData>,
    driverVehicle: Vehicle?,
    onAcceptTrip: (String) -> Unit,
    onViewEarnings: () -> Unit,
    onViewRequests: () -> Unit,
    onAddTrip: () -> Unit,
    onViewProfile: () -> Unit,
    onLogout: () -> Unit,
    onShowMessage: (String) -> Unit,
    onReloadVehicle: () -> Unit,
    successMessage: String?,
    onClearSuccessMessage: () -> Unit,
    errorMessage: String?,
    onClearErrorMessage: () -> Unit,
    driverRequestNotificationCount: Int,
    onClearDriverRequestNotifications: () -> Unit
) {
    // Handle success message visibility
    LaunchedEffect(successMessage) {
        successMessage?.let {
            kotlinx.coroutines.delay(3000) // Show for 3 seconds
            onClearSuccessMessage()
        }
    }

    // Handle error message visibility
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            kotlinx.coroutines.delay(3000) // Show for 3 seconds
            onClearErrorMessage()
        }
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    android.util.Log.d("TaxiApp", "Add Trip FAB clicked. driverVehicle: $driverVehicle")
                    // Check if driver has a vehicle before navigating to Add Trip
                    if (driverVehicle != null) {
                        android.util.Log.d("TaxiApp", "Vehicle found, navigating to Add Trip")
                        onAddTrip()
                    } else {
                        android.util.Log.d("TaxiApp", "No vehicle found, reloading vehicle data...")
                        onReloadVehicle()
                        onShowMessage("Checking vehicle registration...")
                    }
                },
                containerColor = TaxiBlue,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Trip"
                )
            }
        },
        containerColor = TaxiBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        item {
            // Top Bar
            TopAppBar(
                title = {
                    Text(
                        text = "Բարև, ${driver.name.split(" ").firstOrNull() ?: ""}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TaxiBlack
                    )
                },
                actions = {
                    IconButton(onClick = onViewProfile) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Պրոֆիլ",
                            tint = TaxiBlack
                        )
                    }

                    Box {
                        IconButton(
                            onClick = {
                                onClearDriverRequestNotifications()
                                onViewRequests()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = TaxiBlack
                            )
                        }
                        
                        // Notification badge
                        if (driverRequestNotificationCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(20.dp)
                                    .offset(x = 4.dp, y = (-4).dp)
                                    .background(
                                        color = Color.Red,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (driverRequestNotificationCount > 99) "99+" else driverRequestNotificationCount.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TaxiYellow
                )
            )
        }
        
        item {
            // Stats Cards
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = TaxiBlack,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Այսօրվա եկամուտ",
                                fontSize = 12.sp,
                                color = TaxiGray
                            )
                            Text(
                                text = "${stats.todayEarnings} AMD",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxiBlack
                            )
                        }
                    }
                    
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = TaxiBlack,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Այսօրվա երթուղիներ",
                                fontSize = 12.sp,
                                color = TaxiGray
                            )
                            Text(
                                text = stats.todayTrips.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxiBlack
                            )
                        }
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = TaxiBlack,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ընդհանուր գնահատական",
                                fontSize = 12.sp,
                                color = TaxiGray
                            )
                            Text(
                                text = "⭐ ${stats.rating}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxiBlack
                            )
                        }
                    }
                    
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = null,
                                tint = TaxiBlack,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ընդհանուր երթուղիներ",
                                fontSize = 12.sp,
                                color = TaxiGray
                            )
                            Text(
                                text = stats.totalTrips.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxiBlack
                            )
                        }
                    }
                }
                
                // Earnings Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxiYellow.copy(alpha = 0.1f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Ընդհանուր եկամուտ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TaxiBlack
                            )
                            Text(
                                text = "${stats.totalEarnings} AMD",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxiBlack
                            )
                        }
                        
                        TaxiButton(
                            text = "Տեսնել մանրամասն",
                            onClick = onViewEarnings
                        )
                    }
                }
            }
        }
        
//        item {
//            // Driver Requests Card
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                shape = RoundedCornerShape(16.dp),
//                colors = CardDefaults.cardColors(containerColor = TaxiBlue.copy(alpha = 0.1f)),
//                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(20.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Column {
//                        Text(
//                            text = "Ուղևորների հայտեր",
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = TaxiBlack
//                        )
//                        Text(
//                            text = "Կարավարել հայտերը",
//                            fontSize = 12.sp,
//                            color = TaxiGray
//                        )
//                    }
//
//                    TaxiButton(
//                        text = "Տեսնել հայտերը",
//                        onClick = onViewRequests
//                    )
//                }
//            }
//        }
        
        item {
            Text(
                text = "Հասանելի երթուղիներ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TaxiBlack,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            if (stats.pendingTrips > 0) {
                Text(
                    text = "${stats.pendingTrips} նոր հայտ",
                    fontSize = 14.sp,
                    color = StatusPublished,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        if (publishedTrips.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    EmptyState(message = "Հրապարակված երթուղիներ չկան")
                }
            }
        } else {
            item {
                // Published trips header
                Text(
                    text = "Ձեր հրապարակված երթուղիները",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxiBlack,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            items(publishedTrips) { trip ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxiWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Trip Route
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                // From location
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
                                        fontWeight = FontWeight.Medium,
                                        color = TaxiBlack
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                // To location
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
                                        fontWeight = FontWeight.Medium,
                                        color = TaxiBlack
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
                                    text = "Ուղևորներ՝ ${trip.seats_taken}/${trip.seats_total}",
                                    fontSize = 12.sp,
                                    color = TaxiGray
                                )
                            }
                        }
                        
                        // Departure time
                        if (trip.departure_at != null) {
                            Spacer(modifier = Modifier.height(12.dp))
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
                                    text = "Մեկնում՝ ${trip.departure_at}",
                                    fontSize = 12.sp,
                                    color = TaxiGray
                                )
                            }
                        }
                        
                        // Status and payment methods
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = StatusPublished
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Հրապարակված",
                                    fontSize = 12.sp,
                                    color = StatusPublished,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            Text(
                                text = trip.pay_methods.joinToString(", ") { 
                                    when(it) {
                                        "cash" -> "Կանխիկ"
                                        "card" -> "Քարտ"
                                        else -> it
                                    }
                                },
                                fontSize = 12.sp,
                                color = TaxiGray
                            )
                        }
                        
                        // Pending requests indicator if any
                        if (trip.pending_requests_count > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        TaxiYellow.copy(alpha = 0.1f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = TaxiYellow
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${trip.pending_requests_count} նոր հարցում",
                                    fontSize = 12.sp,
                                    color = TaxiBlack,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TaxiBlack,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = TaxiGray
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TaxiBlack
            )
        }
    }
}

@Composable
fun DriverTripCard(
    trip: Trip,
    onAccept: () -> Unit
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
            // Trip Route
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
                            text = trip.fromAddr,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TaxiBlack
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
                            text = trip.toAddr,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TaxiBlack
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${trip.priceAmd} AMD",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxiBlack
                    )
                    Text(
                        text = "Ուղևորներ՝ ${trip.seatsTaken}/${trip.seatsTotal}",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                }
            }
            
            if (trip.departureAt != null) {
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
                        text = "Մեկնում՝ ${trip.departureAt}",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                }
            }
            
            if (trip.distance.isNotEmpty() || trip.duration.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    if (trip.distance.isNotEmpty()) {
                        Text(
                            text = "Հեռավորություն՝ ${trip.distance}",
                            fontSize = 12.sp,
                            color = TaxiGray
                        )
                    }
                    if (trip.duration.isNotEmpty()) {
                        Text(
                            text = " • Տևողություն՝ ${trip.duration}",
                            fontSize = 12.sp,
                            color = TaxiGray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            TaxiButton(
                text = "Ընդունել երթուղին",
                onClick = onAccept,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PublishedTripCard(
    trip: com.example.taxi_app.data.api.DriverTripData
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
            // Trip Route
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // From location
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
                            fontWeight = FontWeight.Medium,
                            color = TaxiBlack
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // To location
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
                            fontWeight = FontWeight.Medium,
                            color = TaxiBlack
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
                        text = "Ուղևորներ՝ ${trip.seats_taken}/${trip.seats_total}",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                }
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
                    text = "Մեկնում՝ ${trip.departure_at}",
                    fontSize = 12.sp,
                    color = TaxiGray
                )
            }
            
            // Status and payment methods
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = StatusPublished
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Հրապարակված",
                        fontSize = 12.sp,
                        color = StatusPublished,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Text(
                    text = trip.pay_methods.joinToString(", ") { 
                        when(it) {
                            "cash" -> "Կանխիկ"
                            "card" -> "Քարտ"
                            else -> it
                        }
                    },
                    fontSize = 12.sp,
                    color = TaxiGray
                )
            }
            
            // Pending requests indicator if any
            if (trip.pending_requests_count > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            TaxiYellow.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TaxiYellow
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${trip.pending_requests_count} նոր հարցում",
                        fontSize = 12.sp,
                        color = TaxiBlack,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        }
    }
}
