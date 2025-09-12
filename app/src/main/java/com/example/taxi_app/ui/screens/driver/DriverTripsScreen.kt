package com.example.taxi_app.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.taxi_app.ui.theme.*
import com.example.taxi_app.viewmodel.TaxiViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverTripsScreen(
    viewModel: TaxiViewModel,
    onBack: () -> Unit,
    onTripClick: ((com.example.taxi_app.data.api.DriverTripData) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val driverTrips by viewModel.driverPublishedTrips.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAllDriverTrips()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TaxiBackground)
    ) {
        // Top Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Իմ ճանապահություններ",
                    fontWeight = FontWeight.Bold,
                    color = TaxiBlack
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Վերադառնալ",
                        tint = TaxiBlack
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.fetchAllDriverTrips() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Թարմացնել",
                        tint = TaxiBlack
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = TaxiYellow
            )
        )

        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading && driverTrips.isEmpty()) {
                // Loading state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = TaxiBlue)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Բեռնվում է...",
                        fontSize = 16.sp,
                        color = TaxiGray
                    )
                }
            } else if (driverTrips.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TaxiGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Դուք դեռ չունեք ճանապահություններ",
                        fontSize = 18.sp,
                        color = TaxiBlack,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Ձեր ավելացրած ճանապահությունները կցուցադրվեն այստեղ",
                        fontSize = 14.sp,
                        color = TaxiGray
                    )
                }
            } else {
                // Trips list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(driverTrips) { trip ->
                        DriverTripCard(
                            trip = trip,
                            onClick = onTripClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DriverTripCard(
    trip: com.example.taxi_app.data.api.DriverTripData,
    onClick: ((com.example.taxi_app.data.api.DriverTripData) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick(trip) }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Trip Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ճանապահություն #${trip.id}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxiBlack
                    )
                }
                
                StatusChip(status = trip.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Trip Route
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // From location
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FiberManualRecord,
                        contentDescription = null,
                        tint = Color.Green,
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
                        tint = Color.Red,
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

            Spacer(modifier = Modifier.height(12.dp))

            // Trip Details Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${trip.price_amd} դրամ",
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
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    // Departure time
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = TaxiGray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val formattedDate = try {
                            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                            val date = formatter.parse(trip.departure_at.take(19))
                            val displayFormatter = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
                            displayFormatter.format(date!!)
                        } catch (e: Exception) {
                            trip.departure_at
                        }
                        Text(
                            text = formattedDate,
                            fontSize = 12.sp,
                            color = TaxiGray
                        )
                    }
                }
            }

            // Payment methods
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = TaxiGray
                )
                Spacer(modifier = Modifier.width(4.dp))
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

            // Amenities
            if (!trip.amenities.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TaxiYellow
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Հարմարություններ՝ ${trip.amenities.joinToString(", ") { it.name }}",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                }
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

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, textColor, text) = when (status.lowercase()) {
        "published" -> Triple(Color.Green.copy(alpha = 0.2f), Color.Green, "Հրապարակված")
        "draft" -> Triple(TaxiYellow.copy(alpha = 0.2f), TaxiBlack, "Սևագիր")
        "archived" -> Triple(TaxiGray.copy(alpha = 0.2f), TaxiGray, "Արխիվ")
        else -> Triple(TaxiGray.copy(alpha = 0.2f), TaxiGray, status)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
