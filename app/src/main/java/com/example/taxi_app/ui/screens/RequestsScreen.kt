package com.example.taxi_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.data.Company
import com.example.taxi_app.data.Request
import com.example.taxi_app.data.Screen
import com.example.taxi_app.ui.components.EmptyState
import com.example.taxi_app.ui.components.TaxiButton
import com.example.taxi_app.ui.components.TaxiLayout
import com.example.taxi_app.ui.theme.*

@Composable
fun RequestsScreen(
    company: Company,
    requests: List<Request>,
    navigationScrollState: LazyListState,
    onNavigate: (Screen) -> Unit,
    onAcceptRequest: (String) -> Unit,
    onDeclineRequest: (String) -> Unit,
    onLogout: () -> Unit
) {
    TaxiLayout(
        company = company,
        currentScreen = Screen.Requests,
        navigationScrollState = navigationScrollState,
        onNavigate = onNavigate,
        onLogout = onLogout
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Հայտերի հերթ",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TaxiBlack,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (requests.isEmpty()) {
                EmptyState(message = "Սպասվող հայտեր չկան")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(requests) { request ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = TaxiWhite)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "${request.trip.fromAddr} → ${request.trip.toAddr} · ${request.trip.departureAt?.toString() ?: ""}",
                                        fontSize = 14.sp,
                                        color = TaxiGray,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    
                                    Text(
                                        text = "${request.user.name} · տեղեր՝ ${request.seats} · ${if (request.payment == "card") "Քարտ" else "Կանխիկ"}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TaxiBlack
                                    )
                                }
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    TaxiButton(
                                        text = "Ընդունել",
                                        onClick = { onAcceptRequest(request.id) },
                                        backgroundColor = StatusPublished,
                                        textColor = TaxiWhite
                                    )
                                    
                                    TaxiButton(
                                        text = "Մերժել",
                                        onClick = { onDeclineRequest(request.id) },
                                        backgroundColor = StatusError,
                                        textColor = TaxiWhite
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
