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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.data.api.RequestData
import com.example.taxi_app.ui.theme.*
import com.example.taxi_app.viewmodel.TaxiViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientRequestsScreen(
    viewModel: TaxiViewModel,
    onBack: () -> Unit
) {
    val requests by viewModel.myRequests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Mark notifications as read when this screen is opened
    LaunchedEffect(Unit) {
        viewModel.markNotificationsAsRead()
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
                    text = "Իմ հայտերը",
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
                IconButton(onClick = { viewModel.refreshMyRequests() }) {
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
            if (isLoading && requests.isEmpty()) {
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
            } else if (requests.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TaxiGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Դուք դեռ չունեք հայտեր",
                        fontSize = 18.sp,
                        color = TaxiBlack,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Ձեր ամրագրումների հայտերը կցուցադրվեն այստեղ",
                        fontSize = 14.sp,
                        color = TaxiGray
                    )
                }
            } else {
                // Requests list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(requests) { request ->
                        RequestCard(request = request)
                    }
                }
            }
        }
    }
}

@Composable
fun RequestCard(request: RequestData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Status and ID row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(status = request.status)
                Text(
                    text = "Հայտ #${request.id}",
                    fontSize = 12.sp,
                    color = TaxiGray,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Trip route
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = TaxiBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${request.trip.from_addr} → ${request.trip.to_addr}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TaxiBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Departure time
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = TaxiGray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formatDepartureTime(request.trip.departure_at),
                    fontSize = 14.sp,
                    color = TaxiGray
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Request details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Seats and payment
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TaxiBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${request.seats} տեղ",
                        fontSize = 14.sp,
                        color = TaxiBlack,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Icon(
                        imageVector = if (request.payment == "cash") Icons.Default.Payments else Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = TaxiBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (request.payment == "cash") "Կանխիկ" else "Քարտ",
                        fontSize = 14.sp,
                        color = TaxiBlack,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Price
                Text(
                    text = "${request.trip.price_amd * request.seats} AMD",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxiBlack
                )
            }
            
            // Driver info
            if (request.trip.driver.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DriveEta,
                        contentDescription = null,
                        tint = TaxiGray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Վարորդ՝ ${request.trip.driver}",
                        fontSize = 14.sp,
                        color = TaxiGray
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, textColor, text) = when (status.lowercase()) {
        "pending" -> Triple(TaxiYellow.copy(alpha = 0.2f), TaxiBlack, "Սպասման մեջ")
        "approved" -> Triple(Color(0xFF4CAF50).copy(alpha = 0.2f), Color(0xFF2E7D32), "Հաստատված")
        "accepted" -> Triple(Color(0xFF4CAF50).copy(alpha = 0.2f), Color(0xFF2E7D32), "Ընդունված")
        "rejected" -> Triple(Color(0xFFF44336).copy(alpha = 0.2f), Color(0xFFC62828), "Մերժված")
        "cancelled" -> Triple(TaxiGray.copy(alpha = 0.2f), TaxiGray, "Չեղարկված")
        else -> Triple(TaxiGray.copy(alpha = 0.2f), TaxiGray, status)
    }
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
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

private fun formatDepartureTime(departureAt: String): String {
    return try {
        val zonedDateTime = ZonedDateTime.parse(departureAt)
        val formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm", Locale("hy", "AM"))
        zonedDateTime.format(formatter)
    } catch (e: Exception) {
        departureAt
    }
}
