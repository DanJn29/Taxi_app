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
import com.example.taxi_app.viewmodel.TaxiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    user: User,
    availableTrips: List<Trip>,
    onTripSelected: (Trip) -> Unit,
    onProfileClicked: () -> Unit,
    onHistoryClicked: () -> Unit,
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
                    // Add plate number and color
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        trip.vehicle?.plate?.let { plate ->
                            Text(
                                text = "Համար՝ $plate",
                                fontSize = 12.sp,
                                color = TaxiGray
                            )
                            
                            trip.vehicle.color?.let { color ->
                                Text(
                                    text = " • $color",
                                    fontSize = 12.sp,
                                    color = TaxiGray
                                )
                            }
                        }
                        
                        // Show color only if no plate
                        if (trip.vehicle?.plate == null) {
                            trip.vehicle?.color?.let { color ->
                                Text(
                                    text = "Գույն՝ $color",
                                    fontSize = 12.sp,
                                    color = TaxiGray
                                )
                            }
                        }
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDialog(
    viewModel: TaxiViewModel?,
    onDismiss: () -> Unit
) {
    val filterMinPrice = viewModel?.filterMinPrice?.collectAsState()?.value
    val filterMaxPrice = viewModel?.filterMaxPrice?.collectAsState()?.value
    val filterMinSeats = viewModel?.filterMinSeats?.collectAsState()?.value
    val filterPaymentMethods = viewModel?.filterPaymentMethods?.collectAsState()?.value ?: emptyList()
    
    var tempMinPrice by remember { mutableStateOf(filterMinPrice?.toString() ?: "") }
    var tempMaxPrice by remember { mutableStateOf(filterMaxPrice?.toString() ?: "") }
    var tempMinSeats by remember { mutableStateOf(filterMinSeats?.toString() ?: "") }
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
                    text = "Գնային շրջակ (AMD)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = tempMinPrice,
                        onValueChange = { tempMinPrice = it },
                        label = { Text("Նվազագույնը") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TaxiBlue,
                            focusedLabelColor = TaxiBlue
                        )
                    )
                    
                    OutlinedTextField(
                        value = tempMaxPrice,
                        onValueChange = { tempMaxPrice = it },
                        label = { Text("Առավելագույնը") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TaxiBlue,
                            focusedLabelColor = TaxiBlue
                        )
                    )
                }
                
                // Minimum Seats
                Text(
                    text = "Նվազագույն տեղերի քանակ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                
                OutlinedTextField(
                    value = tempMinSeats,
                    onValueChange = { tempMinSeats = it },
                    label = { Text("Տեղերի քանակ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TaxiBlue,
                        focusedLabelColor = TaxiBlue
                    )
                )
                
                // Payment Methods
                Text(
                    text = "Վճարման եղանակ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                
                val paymentOptions = listOf("cash" to "Կանխիկ", "card" to "Քարտ")
                
                paymentOptions.forEach { (value, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = tempPaymentMethods.contains(value),
                            onCheckedChange = { isChecked ->
                                tempPaymentMethods = if (isChecked) {
                                    tempPaymentMethods + value
                                } else {
                                    tempPaymentMethods - value
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = TaxiBlue
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            color = TaxiBlack
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Apply filters
                    val minPrice = tempMinPrice.toIntOrNull()
                    val maxPrice = tempMaxPrice.toIntOrNull()
                    val minSeats = tempMinSeats.toIntOrNull()
                    
                    viewModel?.updateFilterPriceRange(minPrice, maxPrice)
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
