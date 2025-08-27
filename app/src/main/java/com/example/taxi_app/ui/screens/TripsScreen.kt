package com.example.taxi_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.data.*
import com.example.taxi_app.ui.components.*
import com.example.taxi_app.ui.theme.*
import org.osmdroid.util.GeoPoint

@Composable
fun TripsScreen(
    company: Company,
    trips: List<Trip>,
    vehicles: List<Vehicle>,
    drivers: List<User>,
    currentMapLocation: GeoPoint?,
    navigationScrollState: LazyListState,
    onNavigate: (Screen) -> Unit,
    onAddTrip: (Trip) -> Unit,
    onPublishTrip: (String) -> Unit,
    onArchiveTrip: (String) -> Unit,
    onUnarchiveTrip: (String) -> Unit,
    onUpdateMapLocation: (GeoPoint) -> Unit,
    onLogout: () -> Unit
) {
    var selectedVehicle by remember { mutableStateOf(vehicles.firstOrNull()?.id ?: "") }
    var selectedDriver by remember { mutableStateOf(drivers.firstOrNull()?.id ?: "") }
    var fromAddr by remember { mutableStateOf("") }
    var toAddr by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("2500") }
    var seats by remember { mutableStateOf("4") }
    var departureDateTime by remember { mutableStateOf("") }

    val vehicleOptions = vehicles.map { it.id to "${it.brand} ${it.model} · ${it.plate}" }
    val driverOptions = drivers.map { it.id to it.name }

    TaxiLayout(
        company = company,
        currentScreen = Screen.Trips,
        navigationScrollState = navigationScrollState,
        onNavigate = onNavigate,
        onLogout = onLogout
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Title
            item {
                Text(
                    text = "Երթուղիներ",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxiBlack,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Create trip form
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxiWhite)
                ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Ստեղծել նոր երթուղի",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TaxiBlack,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            TaxiDropdown(
                                value = selectedVehicle,
                                onValueChange = { selectedVehicle = it },
                                label = "Մեքենա",
                                options = vehicleOptions,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            TaxiDropdown(
                                value = selectedDriver,
                                onValueChange = { selectedDriver = it },
                                label = "Վարորդ",
                                options = driverOptions,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            AddressMapPicker(
                                label = "Որտեղից (հասցե)",
                                address = fromAddr,
                                onAddressChange = { fromAddr = it },
                                currentMapLocation = currentMapLocation,
                                onMapLocationUpdate = onUpdateMapLocation,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            AddressMapPicker(
                                label = "Ուր (հասցե)",
                                address = toAddr,
                                onAddressChange = { toAddr = it },
                                currentMapLocation = currentMapLocation,
                                onMapLocationUpdate = onUpdateMapLocation,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            TaxiTextField(
                                value = departureDateTime,
                                onValueChange = { departureDateTime = it },
                                label = "Մեկնման ժամանակ (YYYY-MM-DD HH:MM)",
                                placeholder = "2025-01-01 10:30",
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                TaxiTextField(
                                    value = price,
                                    onValueChange = { price = it },
                                    label = "Գին (AMD)",
                                    keyboardType = KeyboardType.Number,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                TaxiTextField(
                                    value = seats,
                                    onValueChange = { seats = it },
                                    label = "Տեղերի թիվ",
                                    keyboardType = KeyboardType.Number,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TaxiButton(
                        text = "Ստեղծել երթուղի",
                        onClick = {
                            if (fromAddr.isNotBlank() && toAddr.isNotBlank()) {
                                
                                val trip = Trip(
                                    id = System.currentTimeMillis().toString(),
                                    vehicleId = selectedVehicle,
                                    assignedDriverId = selectedDriver,
                                    fromAddr = fromAddr,
                                    toAddr = toAddr,
                                    fromLat = 40.1872, // Default Yerevan coordinates
                                    fromLng = 44.5152,
                                    toLat = 40.1872,
                                    toLng = 44.5152,
                                    priceAmd = price.toIntOrNull() ?: 2500,
                                    seatsTotal = seats.toIntOrNull() ?: 4,
                                    vehicle = vehicles.find { it.id == selectedVehicle },
                                    driver = drivers.find { it.id == selectedDriver }
                                )
                                
                                onAddTrip(trip)
                                
                                // Clear form
                                fromAddr = ""
                                toAddr = ""
                                price = "2500"
                                seats = "4"
                                departureDateTime = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Text(
                        text = "Կոորդինատները պարտադիր են (lat/lng)։",
                        fontSize = 12.sp,
                        color = TaxiGray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            }
            
            // Trips list
            if (trips.isEmpty()) {
                item {
                    EmptyState(message = "Դատարկ է")
                }
            } else {
                items(trips) { trip ->
                    TripCard(
                        trip = trip,
                        onPublish = { onPublishTrip(trip.id) },
                        onArchive = { onArchiveTrip(trip.id) },
                        onUnarchive = { onUnarchiveTrip(trip.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TripCard(
    trip: Trip,
    onPublish: () -> Unit,
    onArchive: () -> Unit,
    onUnarchive: () -> Unit
) {
    val seatsTaken = trip.seatsTaken
    val seatsTotal = trip.seatsTotal
    val seatsLeft = maxOf(0, seatsTotal - seatsTaken)
    val canPublish = trip.status == "draft" && seatsLeft > 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${trip.vehicle?.brand ?: ""} ${trip.vehicle?.model ?: ""} · ${trip.vehicle?.plate ?: ""}",
                fontSize = 14.sp,
                color = TaxiGray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = "${trip.fromAddr} → ${trip.toAddr}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TaxiBlack,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Գին՝ ${trip.priceAmd} AMD",
                fontSize = 14.sp,
                color = TaxiGray
            )
            
            Text(
                text = "Տեղեր՝ $seatsTaken/$seatsTotal",
                fontSize = 14.sp,
                color = TaxiGray
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBadge(status = trip.status)
                
                Text(
                    text = "Սպասվող հայտեր՝ ${trip.pendingRequestsCount} · Հաստատված՝ ${trip.acceptedRequestsCount}",
                    fontSize = 12.sp,
                    color = TaxiGray,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (canPublish) {
                    TaxiButton(
                        text = "Հրապարակել",
                        onClick = onPublish,
                        backgroundColor = TaxiYellow,
                        textColor = TaxiBlack
                    )
                }
                
                if (trip.status != "archived") {
                    TaxiButton(
                        text = "Արխիվացնել",
                        onClick = onArchive
                    )
                }
                
                if (trip.status == "archived") {
                    TaxiButton(
                        text = "Վերադարձնել (սևագիր)",
                        onClick = onUnarchive,
                        backgroundColor = TaxiLightGray,
                        textColor = TaxiBlack
                    )
                }
            }
        }
    }
}
