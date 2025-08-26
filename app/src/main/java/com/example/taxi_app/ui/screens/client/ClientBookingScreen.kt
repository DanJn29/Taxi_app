package com.example.taxi_app.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun ClientBookingScreen(
    trip: Trip,
    user: User,
    onBookTrip: (Int, String, String) -> Unit,
    onBack: () -> Unit
) {
    var selectedSeats by remember { mutableStateOf(1) }
    var selectedPayment by remember { mutableStateOf("cash") }
    var notes by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val availableSeats = trip.seatsTotal - trip.seatsTaken
    val totalPrice = trip.priceAmd * selectedSeats

    val paymentMethods = listOf(
        "cash" to "Կանխիկ",
        "card" to "Բանկային քարտ"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxiBackground)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "Ամրագրում",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TaxiBlack
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Վերադառնալ",
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
            // Trip Details Card
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
                        text = "Երթուղու մանրամասներ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TaxiBlack,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Route
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = trip.fromAddr,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TaxiBlack
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = StatusError,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = trip.toAddr,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TaxiBlack
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Vehicle and Driver Info
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier.size(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = CardDefaults.cardColors(containerColor = TaxiYellow.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = TaxiBlack
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = "${trip.vehicle?.brand} ${trip.vehicle?.model}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TaxiBlack
                            )
                            Text(
                                text = "Վարորդ՝ ${trip.driver?.name}",
                                fontSize = 14.sp,
                                color = TaxiGray
                            )
                            if (trip.departureAt != null) {
                                Text(
                                    text = "Մեկնում՝ ${trip.departureAt}",
                                    fontSize = 14.sp,
                                    color = TaxiGray
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Booking Options Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TaxiWhite)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Ամրագրման տվյալներ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TaxiBlack,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Seat Selection
                    Text(
                        text = "Տեղերի քանակ",
                        fontSize = 14.sp,
                        color = TaxiGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        IconButton(
                            onClick = { if (selectedSeats > 1) selectedSeats-- },
                            enabled = selectedSeats > 1
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Նվազեցնել")
                        }
                        
                        Text(
                            text = selectedSeats.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        IconButton(
                            onClick = { if (selectedSeats < availableSeats) selectedSeats++ },
                            enabled = selectedSeats < availableSeats
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Ավելացնել")
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Text(
                            text = "$availableSeats տեղ հասանելի",
                            fontSize = 14.sp,
                            color = TaxiGray
                        )
                    }
                    
                    // Payment Method
                    TaxiDropdown(
                        value = selectedPayment,
                        onValueChange = { selectedPayment = it },
                        label = "Վճարման եղանակ",
                        options = paymentMethods,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Notes
                    TaxiTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = "Լրացուցիչ նշումներ (ընտրովի)",
                        placeholder = "Կարող եք ավելացնել լրացուցիչ տեղեկություններ...",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Price Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TaxiYellow.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Գին մեկ տեղի համար:",
                            fontSize = 14.sp,
                            color = TaxiGray
                        )
                        Text(
                            text = "${trip.priceAmd} AMD",
                            fontSize = 14.sp,
                            color = TaxiBlack
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Տեղերի քանակ:",
                            fontSize = 14.sp,
                            color = TaxiGray
                        )
                        Text(
                            text = selectedSeats.toString(),
                            fontSize = 14.sp,
                            color = TaxiBlack
                        )
                    }
                    
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = TaxiGray.copy(alpha = 0.3f)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ընդամենը:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxiBlack
                        )
                        Text(
                            text = "$totalPrice AMD",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxiBlack
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Book Button
            TaxiButton(
                text = "Ամրագրել ($totalPrice AMD)",
                onClick = { showConfirmDialog = true },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    
    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text("Հաստատել ամրագրումը")
            },
            text = {
                Text("Վստա՞հ եք, որ ցանկանում եք ամրագրել $selectedSeats տեղ $totalPrice AMD գնով:")
            },
            confirmButton = {
                TaxiButton(
                    text = "Հաստատել",
                    onClick = {
                        onBookTrip(selectedSeats, selectedPayment, notes)
                        showConfirmDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Չեղարկել", color = TaxiGray)
                }
            }
        )
    }
}
