package com.example.taxi_app.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.data.*
import com.example.taxi_app.ui.components.*
import com.example.taxi_app.ui.theme.*
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTripScreen(
    amenities: List<Amenity>,
    driverVehicle: Vehicle?,
    currentMapLocation: GeoPoint?,
    onCreateTrip: (CreateTripRequest) -> Unit,
    onBack: () -> Unit,
    onLoadAmenities: () -> Unit,
    onMapLocationUpdate: (GeoPoint) -> Unit
) {
    // Form state
    var fromAddress by remember { mutableStateOf("") }
    var fromLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var toAddress by remember { mutableStateOf("") }
    var toLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var departureDate by remember { mutableStateOf("") }
    var departureTime by remember { mutableStateOf("") }
    var seatsTotal by remember { mutableStateOf("") }
    var priceAmd by remember { mutableStateOf("") }
    var selectedPayMethods by remember { mutableStateOf(setOf<String>()) }
    var selectedAmenities by remember { mutableStateOf(setOf<Int>()) }

    // Load amenities when screen is composed
    LaunchedEffect(Unit) {
        onLoadAmenities()
    }

    // Validation
    val isFormValid = fromAddress.isNotBlank() && 
                     toAddress.isNotBlank() && 
                     departureDate.isNotBlank() && 
                     departureTime.isNotBlank() && 
                     seatsTotal.isNotBlank() && 
                     priceAmd.isNotBlank() && 
                     selectedPayMethods.isNotEmpty() &&
                     driverVehicle != null &&
                     fromLocation != null &&
                     toLocation != null

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxiBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Վերադառնալ",
                        tint = TaxiBlue
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ավելացնել երթուղի",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxiBlack
                )
            }
        }

        item {
            // Vehicle info
            if (driverVehicle != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Ձեր մեքենան",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxiBlue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${driverVehicle.brand} ${driverVehicle.model}",
                            fontSize = 14.sp,
                            color = TaxiBlack
                        )
                        Text(
                            text = "Նիշ՝ ${driverVehicle.plate} • ${driverVehicle.seats} նստատեղ",
                            fontSize = 12.sp,
                            color = TaxiGray
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = "Նախ պետք է գրանցել ձեր մեքենան",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // From section
        item {
            Column {
                Text(
                    text = "Ելման վայր",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                AddressMapPicker(
                    label = "Ելման հասցե",
                    address = fromAddress,
                    onAddressChange = { fromAddress = it },
                    currentMapLocation = currentMapLocation,
                    onMapLocationUpdate = { location ->
                        fromLocation = location
                        onMapLocationUpdate(location)
                    },
                    initialLocation = GeoPoint(40.1872, 44.5152) // Yerevan center
                )
            }
        }

        // To section
        item {
            Column {
                Text(
                    text = "Նպատակակետ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                AddressMapPicker(
                    label = "Նպատակակետի հասցե",
                    address = toAddress,
                    onAddressChange = { toAddress = it },
                    currentMapLocation = currentMapLocation,
                    onMapLocationUpdate = { location ->
                        toLocation = location
                        onMapLocationUpdate(location)
                    },
                    initialLocation = GeoPoint(40.1872, 44.5152) // Yerevan center
                )
            }
        }

        // Date and time
        item {
            Column {
                Text(
                    text = "Մեկնման ժամանակ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = departureDate,
                        onValueChange = { departureDate = it },
                        label = { Text("Ամսաթիվ (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("2025-03-05") }
                    )
                    OutlinedTextField(
                        value = departureTime,
                        onValueChange = { departureTime = it },
                        label = { Text("Ժամ (HH:MM)") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("14:00") }
                    )
                }
            }
        }

        // Seats and price
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Նստատեղեր",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TaxiBlack
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = seatsTotal,
                        onValueChange = { seatsTotal = it },
                        label = { Text("Քանակ") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Գին",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TaxiBlack
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = priceAmd,
                        onValueChange = { priceAmd = it },
                        label = { Text("ԱԴ") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }

        // Payment methods
        item {
            Column {
                Text(
                    text = "Վճարման եղանակներ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cash option
                    FilterChip(
                        onClick = {
                            selectedPayMethods = if (selectedPayMethods.contains("cash")) {
                                selectedPayMethods - "cash"
                            } else {
                                selectedPayMethods + "cash"
                            }
                        },
                        label = { Text("Կանխիկ") },
                        selected = selectedPayMethods.contains("cash"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TaxiBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                    
                    // Card option
                    FilterChip(
                        onClick = {
                            selectedPayMethods = if (selectedPayMethods.contains("card")) {
                                selectedPayMethods - "card"
                            } else {
                                selectedPayMethods + "card"
                            }
                        },
                        label = { Text("Քարտ") },
                        selected = selectedPayMethods.contains("card"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TaxiBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Amenities
        item {
            Column {
                Text(
                    text = "Հարմարություններ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                if (amenities.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(amenities) { amenity ->
                            FilterChip(
                                onClick = {
                                    selectedAmenities = if (selectedAmenities.contains(amenity.id)) {
                                        selectedAmenities - amenity.id
                                    } else {
                                        selectedAmenities + amenity.id
                                    }
                                },
                                label = { Text(amenity.name) },
                                selected = selectedAmenities.contains(amenity.id),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TaxiBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Բեռնվում են հարմարությունները...",
                        color = TaxiGray,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Create button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    if (isFormValid && driverVehicle != null && fromLocation != null && toLocation != null) {
                        val departureDateTime = "${departureDate}T${departureTime}:00Z"
                        
                        val tripRequest = CreateTripRequest(
                            vehicle_id = driverVehicle.id.toIntOrNull() ?: 0,
                            from_addr = fromAddress,
                            from_lat = fromLocation!!.latitude,
                            from_lng = fromLocation!!.longitude,
                            to_addr = toAddress,
                            to_lat = toLocation!!.latitude,
                            to_lng = toLocation!!.longitude,
                            departure_at = departureDateTime,
                            seats_total = seatsTotal.toIntOrNull() ?: 1,
                            price_amd = priceAmd.toIntOrNull() ?: 0,
                            pay_methods = selectedPayMethods.toList(),
                            status = "published",
                            amenities = selectedAmenities.toList()
                        )
                        
                        onCreateTrip(tripRequest)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TaxiBlue,
                    disabledContainerColor = TaxiGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ստեղծել երթուղի",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}
