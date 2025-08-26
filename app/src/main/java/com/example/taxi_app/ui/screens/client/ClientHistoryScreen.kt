package com.example.taxi_app.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.data.Booking
import com.example.taxi_app.data.User
import com.example.taxi_app.ui.theme.TaxiBackground
import com.example.taxi_app.ui.theme.TaxiYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHistoryScreen(
    user: User,
    bookings: List<Booking>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxiBackground)
    ) {
        // Top Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Ճանապարհորդությունների պատմություն",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Վերադառնալ"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = TaxiYellow,
                titleContentColor = Color.Black,
                navigationIconContentColor = Color.Black
            )
        )

        // Content
        if (bookings.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Դուք դեռ չեք կատարել ճանապարհորդություններ",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Ձեր ապագա ճանապարհորդությունները կցուցադրվեն այստեղ",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        } else {
            // Trip history list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookings) { booking ->
                    BookingHistoryCard(booking = booking)
                }
            }
        }
    }
}

@Composable
fun BookingHistoryCard(booking: Booking) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Status and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ամսաթիվ: ${booking.bookingDate}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                val statusColor = when (booking.status) {
                    "completed" -> Color.Green
                    "cancelled" -> Color.Red
                    "active" -> TaxiYellow
                    else -> Color.Gray
                }
                
                val statusText = when (booking.status) {
                    "completed" -> "Ավարտված"
                    "cancelled" -> "Չեղարկված"
                    "active" -> "Ակտիվ"
                    else -> "Սպասող"
                }
                
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Trip details
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = TaxiYellow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Ուղևորություն #${booking.tripId}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Seats and Payment
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Նստատեղեր: ${booking.seats}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Վճարում: ${booking.paymentMethod}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Notes if available
            if (booking.notes?.isNotBlank() == true) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Նշումներ: ${booking.notes}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
