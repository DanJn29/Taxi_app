package com.example.taxi_app.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.taxi_app.data.User
import com.example.taxi_app.data.api.TripDetailsData
import com.example.taxi_app.ui.theme.*
import com.example.taxi_app.viewmodel.TaxiViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.net.URL
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientTripDetailsScreen(
    tripId: Int,
    user: User,
    viewModel: TaxiViewModel,
    onBack: () -> Unit,
    onBookTrip: (Int, String, String) -> Unit
) {
    val tripDetails by viewModel.tripDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var selectedSeats by remember { mutableStateOf(1) }
    var selectedPayment by remember { mutableStateOf("cash") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(tripId) {
        viewModel.loadTripDetails(tripId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxiWhite)
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    "Ուղևորության մանրամասներ",
                    color = TaxiBlack,
                    fontWeight = FontWeight.Medium
                ) 
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TaxiBlack
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = TaxiWhite
            )
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TaxiYellow)
            }
        } else {
            tripDetails?.let { trip ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Trip Overview Card
                    TripOverviewCard(trip = trip)

                    // Route and Timing Card
                    RouteTimingCard(trip = trip)

                    // Route Map Card
                    RouteMapCard(trip = trip)

                    // Driver and Vehicle Card
                    DriverVehicleCard(trip = trip)

                    // Amenities Card (if any)
                    if (trip.amenitiesByCat.isNotEmpty()) {
                        AmenitiesCard(amenities = trip.amenitiesByCat)
                    }

                    // Stops Card (if any)
                    if (trip.stops.isNotEmpty()) {
                        StopsCard(stops = trip.stops)
                    }

                    // Reviews Card
                    ReviewsCard(reviews = trip.reviews)

                    // Booking Section
                    BookingSection(
                        trip = trip,
                        selectedSeats = selectedSeats,
                        onSeatsChange = { selectedSeats = it },
                        selectedPayment = selectedPayment,
                        onPaymentChange = { selectedPayment = it },
                        notes = notes,
                        onNotesChange = { notes = it },
                        onBook = { 
                            onBookTrip(trip.id, selectedSeats.toString(), notes)
                        }
                    )
                }
            } ?: run {
                // Show error state when trip details failed to load
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Չհաջողվեց բեռնել ուղևորության մանրամասները",
                            fontSize = 16.sp,
                            color = TaxiBlack,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.loadTripDetails(tripId) },
                            colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow)
                        ) {
                            Text("Կրկին փորձել", color = TaxiBlack)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TripOverviewCard(trip: TripDetailsData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with trip type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ուղևորություն #${trip.id}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxiBlack
                )
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (trip.is_company) TaxiBlue.copy(alpha = 0.2f) else TaxiYellow.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (trip.is_company) "Ընկերություն" else "Անհատական",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = if (trip.is_company) TaxiBlue else TaxiYellow,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Route
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = trip.from,
                    fontSize = 14.sp,
                    color = TaxiBlack,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = trip.to,
                    fontSize = 14.sp,
                    color = TaxiBlack,
                    modifier = Modifier.weight(1f)
                )
            }

            Divider(color = TaxiLightGray)

            // Key Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Գին",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                    Text(
                        text = "${trip.price_amd} ֏",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxiYellow
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Ազատ տեղեր",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                    Text(
                        text = "${trip.seats_total - trip.seats_taken}/${trip.seats_total}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxiBlack
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Սպասող հայտեր",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                    Text(
                        text = "${trip.pending_requests_count}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxiBlue
                    )
                }
            }
        }
    }
}

@Composable
fun RouteTimingCard(trip: TripDetailsData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Ժամանակ և երթուղի",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TaxiBlack
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = TaxiBlue,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Մեկնում է՝ ${formatDepartureTime(trip.departure_at)}",
                    fontSize = 14.sp,
                    color = TaxiBlack,
                    fontWeight = FontWeight.Medium
                )
            }

            // Payment methods
            Text(
                text = "Վճարման եղանակներ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TaxiBlack
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                trip.pay_methods.forEach { method ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TaxiLightGray.copy(alpha = 0.3f),
                        border = BorderStroke(1.dp, TaxiLightGray)
                    ) {
                        Text(
                            text = when(method) {
                                "cash" -> "Կանխիկ"
                                "card" -> "Քարտ"
                                else -> method
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            color = TaxiBlack
                        )
                    }
                }
            }
        }
    }
}

// Route Map Card
@Composable
fun RouteMapCard(trip: TripDetailsData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Երթուղի",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TaxiBlack,
                modifier = Modifier.padding(bottom = 12.dp),
                maxLines = 1
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                RouteMap(
                    fromLat = trip.from_lat,
                    fromLng = trip.from_lng,
                    toLat = trip.to_lat,
                    toLng = trip.to_lng,
                    fromAddr = trip.from,
                    toAddr = trip.to,
                    stops = trip.stops,
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds()
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Route legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = Color(0xFF4CAF50),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Սկիզբ",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = Color(0xFFFF9800),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Կանգառ",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(3.dp)
                            .background(
                                color = Color(0xFF2196F3),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Երթուղի",
                        fontSize = 10.sp,
                        color = TaxiGray,
                        maxLines = 1
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = Color(0xFFF44336),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Վերջ",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                }
            }
        }
    }
}

@Composable
fun DriverVehicleCard(trip: TripDetailsData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Վարորդ և տրանսպորտ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TaxiBlack
            )

            // Driver info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Driver avatar placeholder
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(TaxiBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TaxiBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trip.actor.name ?: "անհայտ վարորդ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TaxiBlack
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${trip.actor.rating ?: 0.0} (${trip.actor.trips ?: 0} ուղևորություն)",
                            fontSize = 12.sp,
                            color = TaxiGray
                        )
                    }
                }
            }

            Divider(color = TaxiLightGray)

            // Vehicle info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = TaxiBlue,
                    modifier = Modifier.size(24.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${trip.vehicle.brand ?: "անհայտ"} ${trip.vehicle.model ?: ""}".trim(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TaxiBlack
                    )
                    Text(
                        text = "${hexToArmenianColorName(trip.vehicle.color)} • ${trip.vehicle.plate ?: "անհայտ համար"}",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                }
            }
        }
    }
}

@Composable
fun AmenitiesCard(amenities: List<com.example.taxi_app.data.api.AmenityCategory>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Հարմարություններ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TaxiBlack
            )

            amenities.forEach { category ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = category.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TaxiBlack
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        category.items.forEach { item ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = TaxiYellow.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, TaxiYellow)
                            ) {
                                Text(
                                    text = item.name,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 12.sp,
                                    color = TaxiBlack
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
fun StopsCard(stops: List<com.example.taxi_app.data.api.TripStop>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Կանգառներ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TaxiBlack
            )

            stops.sortedBy { it.position }.forEach { stop ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = CircleShape,
                        color = TaxiBlue
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${stop.position}",
                                fontSize = 12.sp,
                                color = TaxiWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stop.name ?: "անհայտ կանգառ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TaxiBlack
                        )
                        Text(
                            text = stop.addr ?: "",
                            fontSize = 12.sp,
                            color = TaxiGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewsCard(reviews: com.example.taxi_app.data.api.TripReviews) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Գնահատականներ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxiBlack
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${reviews.summary.rating} (${reviews.summary.count})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TaxiBlack
                    )
                }
            }

            if (reviews.items.isNotEmpty()) {
                reviews.items.take(3).forEach { review ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                TaxiLightGray.copy(alpha = 0.3f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = if (index < (review.rating ?: 0)) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        tint = if (index < (review.rating ?: 0)) Color(0xFFFFD700) else TaxiGray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            
                            Text(
                                text = review.date?.let { formatReviewDate(it) } ?: "",
                                fontSize = 10.sp,
                                color = TaxiGray
                            )
                        }
                        
                        if (!review.text.isNullOrEmpty()) {
                            Text(
                                text = review.text,
                                fontSize = 12.sp,
                                color = TaxiBlack
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "Դեռ գնահատականներ չկան",
                    fontSize = 12.sp,
                    color = TaxiGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun BookingSection(
    trip: TripDetailsData,
    selectedSeats: Int,
    onSeatsChange: (Int) -> Unit,
    selectedPayment: String,
    onPaymentChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    onBook: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Ամրագրել ուղևորություն",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TaxiBlack
            )

            // Seats selection
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Տեղերի քանակ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = { if (selectedSeats > 1) onSeatsChange(selectedSeats - 1) },
                        enabled = selectedSeats > 1
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Նվազեցնել",
                            tint = if (selectedSeats > 1) TaxiBlack else TaxiGray
                        )
                    }
                    
                    Text(
                        text = "$selectedSeats",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxiBlack,
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.Center
                    )
                    
                    IconButton(
                        onClick = { 
                            val availableSeats = trip.seats_total - trip.seats_taken
                            if (selectedSeats < availableSeats) onSeatsChange(selectedSeats + 1) 
                        },
                        enabled = selectedSeats < (trip.seats_total - trip.seats_taken)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ավելացնել",
                            tint = if (selectedSeats < (trip.seats_total - trip.seats_taken)) TaxiBlack else TaxiGray
                        )
                    }
                }
            }

            // Payment method selection
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Վճարման եղանակ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    trip.pay_methods.forEach { method ->
                        FilterChip(
                            onClick = { onPaymentChange(method) },
                            label = { 
                                Text(
                                    when(method) {
                                        "cash" -> "Կանխիկ"
                                        "card" -> "Քարտ"
                                        else -> method
                                    }
                                )
                            },
                            selected = selectedPayment == method,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TaxiYellow,
                                selectedLabelColor = TaxiBlack
                            )
                        )
                    }
                }
            }

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = { Text("Լրացուցիչ նշումներ") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TaxiYellow,
                    focusedLabelColor = TaxiYellow
                )
            )

            // Total price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ընդամենը՝",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                Text(
                    text = "${trip.price_amd * selectedSeats} ֏",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxiYellow
                )
            }

            // Book button
            Button(
                onClick = onBook,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Ամրագրել ուղևորություն",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxiBlack,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

// Helper functions
private fun formatDepartureTime(departureTime: String): String {
    return try {
        val zonedDateTime = try {
            ZonedDateTime.parse(departureTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"))
        } catch (e: Exception) {
            ZonedDateTime.parse(departureTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
        }
        
        val formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm", Locale("hy", "AM"))
        zonedDateTime.format(formatter)
    } catch (e: Exception) {
        departureTime.replace("T", " ").substring(0, minOf(16, departureTime.length))
    }
}

private fun hexToArmenianColorName(hexColor: String?): String {
    if (hexColor.isNullOrEmpty()) return "անհայտ գույն"
    
    val colorMap = mapOf(
        "#FF0000" to "կարմիր",
        "#FF4500" to "նարանջագույն", 
        "#FFA500" to "նարանջ",
        "#FFFF00" to "դեղին",
        "#ADFF2F" to "դեղնականաչ",
        "#00FF00" to "կանաչ",
        "#00FFFF" to "կապույտականաչ",
        "#0000FF" to "կապույտ",
        "#4B0082" to "ինդիգո",
        "#9400D3" to "մանուշակագույն",
        "#FF1493" to "վարդագույն",
        "#FFB6C1" to "բաց վարդագույն",
        "#FFFFFF" to "սպիտակ",
        "#000000" to "սև",
        "#808080" to "գորշ",
        "#C0C0C0" to "արծաթագույն",
        "#FFD700" to "ոսկագույն",
        "#8B4513" to "դարչնագույն",
        "#A0522D" to "շագանակագույն",
        "#10b981" to "կանաչ",
        "Black" to "սև",
        "White" to "սպիտակ",
        "Red" to "կարմիր",
        "Blue" to "կապույտ",
        "Green" to "կանաչ",
        "Yellow" to "դեղին",
        "Silver" to "արծաթագույն",
        "Gray" to "գորշ",
        "Grey" to "գորշ"
    )
    
    return colorMap[hexColor.uppercase()] ?: hexColor.lowercase()
}

private fun formatReviewDate(dateString: String): String {
    return try {
        val zonedDateTime = ZonedDateTime.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("MMM dd", Locale("hy", "AM"))
        zonedDateTime.format(formatter)
    } catch (e: Exception) {
        dateString.take(10)
    }
}

// Route Map Component with OSRM integration
@Composable
private fun RouteMap(
    fromLat: Double,
    fromLng: Double,
    toLat: Double,
    toLng: Double,
    fromAddr: String,
    toAddr: String,
    stops: List<com.example.taxi_app.data.api.TripStop> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var routePoints by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }
    var routeInfo by remember { mutableStateOf<TripRouteInfo?>(null) }
    var isLoadingRoute by remember { mutableStateOf(true) }
    
    // Load real route when component initializes
    LaunchedEffect(fromLat, fromLng, toLat, toLng, stops) {
        isLoadingRoute = true
        val fromPoint = GeoPoint(fromLat, fromLng)
        val toPoint = GeoPoint(toLat, toLng)
        
        // Try to get real route with stops, fallback to simple route
        scope.launch {
            val (points, info) = getRealRoute(fromPoint, toPoint, stops)
            routePoints = points
            routeInfo = info
            isLoadingRoute = false
        }
    }
    
    Box(modifier = modifier.clipToBounds()) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            factory = { ctx ->
                Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", 0))
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    
                    // Restrict zoom levels to prevent overflow
                    minZoomLevel = 8.0
                    maxZoomLevel = 18.0
                    
                    // Restrict scrolling to prevent overflow
                    setScrollableAreaLimitLatitude(89.5, -89.5, 0)
                    setScrollableAreaLimitLongitude(-179.5, 179.5, 0)
                    
                    // Disable fling gesture to prevent uncontrolled scrolling
                    isFlingEnabled = false
                    
                    // Set up the map view
                    val mapController = controller
                    
                    // Create route points
                    val fromPoint = GeoPoint(fromLat, fromLng)
                    val toPoint = GeoPoint(toLat, toLng)
                    
                    // Calculate center including all stops
                    val allLats = mutableListOf(fromLat, toLat)
                    val allLngs = mutableListOf(fromLng, toLng)
                    stops.forEach { stop ->
                        stop.lat?.let { allLats.add(it) }
                        stop.lng?.let { allLngs.add(it) }
                    }
                    
                    val centerLat = allLats.average()
                    val centerLng = allLngs.average()
                    val centerPoint = GeoPoint(centerLat, centerLng)
                    
                    mapController.setCenter(centerPoint)
                    
                    // Calculate zoom level based on distance
                    val distance = fromPoint.distanceToAsDouble(toPoint)
                    val zoomLevel = when {
                        distance < 1000 -> 16.0 // Less than 1km
                        distance < 5000 -> 14.0 // Less than 5km
                        distance < 20000 -> 12.0 // Less than 20km
                        else -> 10.0
                    }
                    mapController.setZoom(zoomLevel)
                    
                    // Add markers with better styling
                    val fromMarker = Marker(this).apply {
                        position = fromPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Սկիզբ: $fromAddr"
                        icon = context.getDrawable(android.R.drawable.ic_menu_mylocation)?.apply {
                            setTint(android.graphics.Color.parseColor("#4CAF50")) // Material Green
                        }
                    }
                    
                    val toMarker = Marker(this).apply {
                        position = toPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Վերջ: $toAddr"
                        icon = context.getDrawable(android.R.drawable.ic_menu_mylocation)?.apply {
                            setTint(android.graphics.Color.parseColor("#F44336")) // Material Red
                        }
                    }
                    
                    // Create stop markers
                    val stopMarkers = stops.mapNotNull { stop ->
                        // Only create markers for stops with valid coordinates
                        if (stop.lat != null && stop.lng != null) {
                            Marker(this).apply {
                                position = GeoPoint(stop.lat, stop.lng)
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = "Կանգառ: ${stop.name ?: "անհայտ"}\n${stop.addr ?: ""}"
                                icon = context.getDrawable(android.R.drawable.ic_menu_mylocation)?.apply {
                                    setTint(android.graphics.Color.parseColor("#FF9800")) // Material Orange
                                }
                            }
                        } else null
                    }
                    
                    // Add markers to map
                    overlays.add(fromMarker)
                    overlays.add(toMarker)
                    overlays.addAll(stopMarkers)
                    
                    // Add padding to show all markers (origin, destination, and stops)
                    post {
                        // Collect all valid coordinates for bounding box
                        val allLats = mutableListOf(fromLat, toLat)
                        val allLngs = mutableListOf(fromLng, toLng)
                        
                        // Add stop coordinates if they exist
                        stops.forEach { stop ->
                            stop.lat?.let { allLats.add(it) }
                            stop.lng?.let { allLngs.add(it) }
                        }
                        
                        if (allLats.isNotEmpty() && allLngs.isNotEmpty()) {
                            zoomToBoundingBox(
                                org.osmdroid.util.BoundingBox(
                                    allLats.maxOrNull()!! + 0.001,
                                    allLngs.maxOrNull()!! + 0.001,
                                    allLats.minOrNull()!! - 0.001,
                                    allLngs.minOrNull()!! - 0.001
                                ),
                                true,
                                50
                            )
                        }
                    }
                }
            },
            update = { mapView ->
                // Update route when routePoints change
                if (routePoints.isNotEmpty()) {
                    // Remove old route overlays (keep markers)
                    val markersToKeep = mapView.overlays.filterIsInstance<Marker>()
                    mapView.overlays.clear()
                    
                    // Add route line with real road data
                    val routeLine = Polyline().apply {
                        setPoints(routePoints)
                        outlinePaint.color = android.graphics.Color.parseColor("#2196F3") // Material Blue
                        outlinePaint.strokeWidth = 12f
                        outlinePaint.strokeCap = android.graphics.Paint.Cap.ROUND
                        outlinePaint.strokeJoin = android.graphics.Paint.Join.ROUND
                        // Add shadow effect
                        outlinePaint.setShadowLayer(4f, 2f, 2f, android.graphics.Color.parseColor("#80000000"))
                    }
                    
                    // Re-add route and markers
                    mapView.overlays.add(routeLine)
                    mapView.overlays.addAll(markersToKeep)
                    mapView.invalidate()
                }
            }
        )
        
        // Loading indicator for route
        if (isLoadingRoute) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxiWhite.copy(alpha = 0.9f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = TaxiYellow,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Բեռնում է երթուղին...",
                            fontSize = 12.sp,
                            color = TaxiBlack
                        )
                    }
                }
            }
        }
    }
}

// OSRM Route fetching function with waypoints support
private suspend fun getRealRoute(
    from: GeoPoint, 
    to: GeoPoint, 
    stops: List<com.example.taxi_app.data.api.TripStop> = emptyList()
): Pair<List<GeoPoint>, TripRouteInfo?> {
    return try {
        withContext(Dispatchers.IO) {
            // Build waypoints string for OSRM API
            val waypoints = mutableListOf<String>()
            
            // Add starting point
            waypoints.add("${from.longitude},${from.latitude}")
            
            // Add stops (only those with valid coordinates) sorted by position
            val validStops = stops
                .filter { it.lat != null && it.lng != null }
                .sortedBy { it.position ?: 0 }
            
            validStops.forEach { stop ->
                waypoints.add("${stop.lng},${stop.lat}")
            }
            
            // Add destination point
            waypoints.add("${to.longitude},${to.latitude}")
            
            // Use OSRM demo server for routing (free, no API key needed)
            // Join all waypoints with semicolons for multi-point routing
            val waypointsString = waypoints.joinToString(";")
            val url = "https://router.project-osrm.org/route/v1/driving/" +
                    waypointsString +
                    "?overview=full&geometries=geojson"
            
            val connection = URL(url).openConnection()
            val response = connection.getInputStream().bufferedReader().readText()
            
            val json = JSONObject(response)
            val routes = json.getJSONArray("routes")
            
            if (routes.length() > 0) {
                val route = routes.getJSONObject(0)
                val geometry = route.getJSONObject("geometry")
                val coordinates = geometry.getJSONArray("coordinates")
                
                // Extract route info
                val distance = route.getDouble("distance") // in meters
                val duration = route.getDouble("duration") // in seconds
                val routeInfo = TripRouteInfo(
                    distanceKm = (distance / 1000).toFloat(),
                    durationMinutes = (duration / 60).toInt()
                )
                
                val routePoints = mutableListOf<GeoPoint>()
                for (i in 0 until coordinates.length()) {
                    val coord = coordinates.getJSONArray(i)
                    val lng = coord.getDouble(0)
                    val lat = coord.getDouble(1)
                    routePoints.add(GeoPoint(lat, lng))
                }
                Pair(routePoints, routeInfo)
            } else {
                // Fallback to simple route if API fails
                Pair(createSimpleRoute(from, to, stops), null)
            }
        }
    } catch (e: Exception) {
        android.util.Log.e("RouteMap", "Error getting real route: ${e.message}")
        // Fallback to simple route if API fails
        Pair(createSimpleRoute(from, to, stops), null)
    }
}

// Data class for route information in trip details
data class TripRouteInfo(
    val distanceKm: Float,
    val durationMinutes: Int
)

// Enhanced helper function for better simple routing (fallback) with stops support
private fun createSimpleRoute(
    from: GeoPoint, 
    to: GeoPoint, 
    stops: List<com.example.taxi_app.data.api.TripStop> = emptyList()
): List<GeoPoint> {
    val points = mutableListOf<GeoPoint>()
    
    // Create ordered list of all waypoints
    val waypoints = mutableListOf<GeoPoint>()
    waypoints.add(from)
    
    // Add valid stops sorted by position
    val validStops = stops
        .filter { it.lat != null && it.lng != null }
        .sortedBy { it.position ?: 0 }
    
    validStops.forEach { stop ->
        waypoints.add(GeoPoint(stop.lat!!, stop.lng!!))
    }
    
    waypoints.add(to)
    
    // Create route segments between consecutive waypoints
    for (i in 0 until waypoints.size - 1) {
        val startPoint = waypoints[i]
        val endPoint = waypoints[i + 1]
        
        points.add(startPoint)
        
        val latDiff = endPoint.latitude - startPoint.latitude
        val lngDiff = endPoint.longitude - startPoint.longitude
        val distance = startPoint.distanceToAsDouble(endPoint)
        
        // Create intermediate points for each segment
        val numPoints = when {
            distance < 1000 -> 2   // Short distance: 2 points
            distance < 5000 -> 3   // Medium distance: 3 points  
            distance < 20000 -> 5  // Long distance: 5 points
            else -> 8              // Very long distance: 8 points
        }
        
        for (j in 1..numPoints) {
            val progress = j.toDouble() / (numPoints + 1)
            val lat = startPoint.latitude + (latDiff * progress)
            val lng = startPoint.longitude + (lngDiff * progress)
            
            // Add slight road-like variations
            val roadVariation = when {
                progress < 0.3 -> 0.0001 * kotlin.math.sin(progress * kotlin.math.PI * 4)
                progress < 0.7 -> 0.00015 * kotlin.math.cos(progress * kotlin.math.PI * 3)  
                else -> 0.0001 * kotlin.math.sin(progress * kotlin.math.PI * 2)
            }
            
            points.add(GeoPoint(lat + roadVariation, lng + roadVariation * 0.7))
        }
    }
    
    // Add final destination
    points.add(to)
    return points
}
