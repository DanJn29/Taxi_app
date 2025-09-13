package com.example.taxi_app.ui.screens.client

import androidx.compose.foundation.background
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
import androidx.compose.ui.window.Dialog
import com.example.taxi_app.data.api.CompletedTripData
import com.example.taxi_app.ui.theme.*
import com.example.taxi_app.viewmodel.TaxiViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHistoryScreenV2(
    viewModel: TaxiViewModel,
    onBack: () -> Unit
) {
    val completedTrips by viewModel.completedTrips.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showRatingDialog by remember { mutableStateOf<CompletedTripData?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadCompletedTrips()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxiBackground)
    ) {
        // Top Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Ավարտված ուղևորություններ",
                    color = TaxiBlack,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Վերադարձ",
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
        } else if (completedTrips.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TaxiGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ավարտված ուղևորություններ չկան",
                        fontSize = 16.sp,
                        color = TaxiGray
                    )
                }
            }
        } else {
            // Completed trips list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(completedTrips) { tripData ->
                    CompletedTripCard(
                        tripData = tripData,
                        onRate = { showRatingDialog = tripData }
                    )
                }
            }
        }
    }

    // Rating dialog
    showRatingDialog?.let { tripData ->
        RatingDialog(
            tripData = tripData,
            onDismiss = { showRatingDialog = null },
            onRate = { rating ->
                viewModel.rateTrip(tripData.id, rating)
                showRatingDialog = null
            }
        )
    }
}

@Composable
fun CompletedTripCard(
    tripData: CompletedTripData,
    onRate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ուղևորություն #${tripData.id}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxiBlack
                    )
                    Text(
                        text = formatDateTimeV2(tripData.departure_at),
                        fontSize = 12.sp,
                        color = TaxiGray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF4CAF50).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Ավարտված",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            // Trip route
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
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
                        text = tripData.from_addr,
                        fontSize = 14.sp,
                        color = TaxiBlack,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(
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
                        text = tripData.to_addr,
                        fontSize = 14.sp,
                        color = TaxiBlack,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Trip details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Price
                Column {
                    Text(
                        text = "Գին",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                    Text(
                        text = "${tripData.price_amd} ֏",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxiYellow
                    )
                }
                
                // Driver
                Column {
                    Text(
                        text = "Վարորդ",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                    Text(
                        text = tripData.driver.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TaxiBlack
                    )
                }
            }

            // Rating section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (tripData.rating != null) {
                    // Show existing rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Ձեր գնահատականը՝",
                            fontSize = 12.sp,
                            color = TaxiGray
                        )
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < tripData.rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (index < tripData.rating) Color(0xFFFFD700) else TaxiGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    // Show rate button
                    Button(
                        onClick = onRate,
                        colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = TaxiBlack
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Գնահատել",
                            fontSize = 12.sp,
                            color = TaxiBlack
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RatingDialog(
    tripData: CompletedTripData,
    onDismiss: () -> Unit,
    onRate: (Int) -> Unit
) {
    var rating by remember { mutableStateOf(5) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TaxiWhite)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Գնահատել ուղևորությունը",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxiBlack
                )

                Text(
                    text = "${tripData.from_addr} → ${tripData.to_addr}",
                    fontSize = 14.sp,
                    color = TaxiGray
                )

                // Rating stars
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(5) { index ->
                        IconButton(
                            onClick = { rating = index + 1 }
                        ) {
                            Icon(
                                imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Աստղ ${index + 1}",
                                tint = if (index < rating) Color(0xFFFFD700) else TaxiGray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TaxiGray
                        )
                    ) {
                        Text("Չեղարկել")
                    }
                    
                    Button(
                        onClick = { onRate(rating) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow)
                    ) {
                        Text("Գնահատել", color = TaxiBlack)
                    }
                }
            }
        }
    }
}

private fun formatDateTimeV2(dateTimeString: String): String {
    return try {
        val zonedDateTime = ZonedDateTime.parse(dateTimeString)
        val formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm", Locale("hy", "AM"))
        zonedDateTime.format(formatter)
    } catch (e: Exception) {
        dateTimeString.take(16).replace("T", " ")
    }
}
