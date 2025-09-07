package com.example.taxi_app.ui.screens.driver

import androidx.compose.foundation.background
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
    onAcceptTrip: (String) -> Unit,
    onToggleAvailability: () -> Unit,
    onViewEarnings: () -> Unit,
    onViewProfile: () -> Unit,
    onLogout: () -> Unit
) {
    var isAvailable by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxiBackground),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Top Bar
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Բարև, ${driver.name.split(" ").firstOrNull() ?: ""}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TaxiBlack
                        )
                        Text(
                            text = if (isAvailable) "Հասանելի" else "Հասանելի չէ",
                            fontSize = 14.sp,
                            color = if (isAvailable) StatusPublished else StatusError
                        )
                    }
                },
                actions = {
                    // Availability Toggle
                    Switch(
                        checked = isAvailable,
                        onCheckedChange = { 
                            isAvailable = it
                            onToggleAvailability()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TaxiYellow,
                            checkedTrackColor = TaxiBlack
                        )
                    )
                    
                    IconButton(onClick = onViewProfile) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Պրոֆիլ",
                            tint = TaxiBlack
                        )
                    }
                    
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Դուրս գալ",
                            tint = TaxiBlack
                        )
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
                    StatCard(
                        title = "Այսօրվա եկամուտ",
                        value = "${stats.todayEarnings} AMD",
                        icon = Icons.Default.MonetizationOn,
                        modifier = Modifier.weight(1f)
                    )
                    
                    StatCard(
                        title = "Այսօրվա երթուղիներ",
                        value = stats.todayTrips.toString(),
                        icon = Icons.Default.DirectionsCar,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Ընդհանուր գնահատական",
                        value = "⭐ ${stats.rating}",
                        icon = Icons.Default.Star,
                        modifier = Modifier.weight(1f)
                    )
                    
                    StatCard(
                        title = "Ընդհանուր երթուղիներ",
                        value = stats.totalTrips.toString(),
                        icon = Icons.Default.Assignment,
                        modifier = Modifier.weight(1f)
                    )
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

        if (!isAvailable) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StatusError.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.PauseCircle,
                            contentDescription = null,
                            tint = StatusError,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Դուք հասանելի չեք",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TaxiBlack
                        )
                        Text(
                            text = "Երթուղիներ ստանալու համար անցեք հասանելի ռեժիմ",
                            fontSize = 14.sp,
                            color = TaxiGray
                        )
                    }
                }
            }
        } else if (publishedTrips.isEmpty()) {
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
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    PublishedTripCard(trip = trip)
                }
            }
        }
    }
}

@Composable
private fun StatCard(
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
private fun DriverTripCard(
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
private fun PublishedTripCard(
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
