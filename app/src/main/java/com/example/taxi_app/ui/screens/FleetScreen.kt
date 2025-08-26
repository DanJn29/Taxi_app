package com.example.taxi_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.data.Company
import com.example.taxi_app.data.Screen
import com.example.taxi_app.data.Vehicle
import com.example.taxi_app.ui.components.*
import com.example.taxi_app.ui.theme.*

@Composable
fun FleetScreen(
    company: Company,
    vehicles: List<Vehicle>,
    onNavigate: (Screen) -> Unit,
    onAddVehicle: (String, String, String, String, Int) -> Unit,
    onLogout: () -> Unit
) {
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("#0ea5e9") }
    var seats by remember { mutableStateOf("4") }

    TaxiLayout(
        company = company,
        currentScreen = Screen.Fleet,
        onNavigate = onNavigate,
        onLogout = onLogout
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Ֆլոտ",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TaxiBlack,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Add vehicle form
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxiWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Ավելացնել մեքենա",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TaxiBlack,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        TaxiTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = "Մարկա",
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        TaxiTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = "Մոդել",
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        TaxiTextField(
                            value = plate,
                            onValueChange = { plate = it },
                            label = "Պետ․ համար",
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        TaxiTextField(
                            value = color,
                            onValueChange = { color = it },
                            label = "Գույն (#hex)",
                            placeholder = "#0ea5e9",
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        TaxiTextField(
                            value = seats,
                            onValueChange = { seats = it },
                            label = "Տեղերի թիվ",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        TaxiButton(
                            text = "Պահպանել",
                            onClick = {
                                if (brand.isNotBlank() && model.isNotBlank() && plate.isNotBlank()) {
                                    onAddVehicle(brand, model, plate, color, seats.toIntOrNull() ?: 4)
                                    // Clear form
                                    brand = ""
                                    model = ""
                                    plate = ""
                                    color = "#0ea5e9"
                                    seats = "4"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                // Vehicle list
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Մեքենաների ցուցակ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TaxiBlack,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    if (vehicles.isEmpty()) {
                        EmptyState(message = "Դատարկ է")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(vehicles) { vehicle ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = TaxiWhite)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "${vehicle.brand} ${vehicle.model} · ${vehicle.plate}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TaxiBlack
                                        )
                                        Text(
                                            text = "Տեղեր՝ ${vehicle.seats}",
                                            fontSize = 14.sp,
                                            color = TaxiGray,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
