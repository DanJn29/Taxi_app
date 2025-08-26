package com.example.taxi_app.ui.screens.client

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
fun ClientHomeScreen(
    user: User,
    availableTrips: List<Trip>,
    onTripSelected: (Trip) -> Unit,
    onProfileClicked: () -> Unit,
    onHistoryClicked: () -> Unit,
    onLogout: () -> Unit
) {
    var fromLocation by remember { mutableStateOf("") }
    var toLocation by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }

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
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = TaxiYellow
            )
        )
        
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Search Card
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
                        value = fromLocation,
                        onValueChange = { fromLocation = it },
                        label = "Որտեղից",
                        placeholder = "Նշիր մեկնման կետը",
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    TaxiTextField(
                        value = toLocation,
                        onValueChange = { toLocation = it },
                        label = "Ուր",
                        placeholder = "Նշիր նպատակակետը",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showFilterDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Ֆիլտր",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ֆիլտր")
                        }
                        
                        TaxiButton(
                            text = "Գտնել",
                            onClick = { /* Search logic */ },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Available Trips
            Text(
                text = "Հասանելի երթուղիներ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TaxiBlack,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (availableTrips.isEmpty()) {
                EmptyState(message = "Երթուղիներ չկան")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(availableTrips) { trip ->
                        TripCard(
                            trip = trip,
                            onTripSelected = { onTripSelected(trip) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TripCard(
    trip: Trip,
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
                            text = trip.fromAddr,
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
                            text = trip.toAddr,
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
                        text = "${trip.priceAmd} AMD",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxiBlack
                    )
                    Text(
                        text = "${trip.seatsTotal - trip.seatsTaken} տեղ",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
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
                        text = "${trip.vehicle?.brand} ${trip.vehicle?.model}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TaxiBlack
                    )
                    Text(
                        text = "Վարորդ՝ ${trip.driver?.name ?: "Անհայտ"}",
                        fontSize = 12.sp,
                        color = TaxiGray
                    )
                }
                
                TaxiButton(
                    text = "Ամրագրել",
                    onClick = onTripSelected,
                    modifier = Modifier.width(100.dp)
                )
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
        }
    }
}
