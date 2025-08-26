package com.example.taxi_app.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.data.DriverStats
import com.example.taxi_app.data.User
import com.example.taxi_app.ui.theme.TaxiBackground
import com.example.taxi_app.ui.theme.TaxiYellow

data class EarningRecord(
    val date: String,
    val tripId: String,
    val amount: Int,
    val duration: String,
    val distance: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverEarningsScreen(
    driver: User,
    stats: DriverStats,
    onBack: () -> Unit
) {
    // Sample earnings data
    val earningsHistory = remember {
        listOf(
            EarningRecord("Օգոստոս 26", "T-001", 3500, "45 րոպե", "15.2 կմ"),
            EarningRecord("Օգոստոս 26", "T-002", 2800, "32 րոպե", "11.5 կմ"),
            EarningRecord("Օգոստոս 25", "T-003", 4200, "58 րոպե", "22.1 կմ"),
            EarningRecord("Օգոստոս 25", "T-004", 1900, "25 րոպե", "8.3 կմ"),
            EarningRecord("Օգոստոս 24", "T-005", 5100, "72 րոպե", "28.7 կմ"),
        )
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
                    text = "Վարձակալություն",
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Today's Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Այսօրվա վարձակալություն",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        EarningsStat(
                            icon = Icons.Default.AttachMoney,
                            label = "Ողջ գումար",
                            value = "${stats.todayEarnings} AMD",
                            color = Color.Green
                        )
                        
                        EarningsStat(
                            icon = Icons.Default.DirectionsCar,
                            label = "Ճանապարհ.",
                            value = "${stats.tripsToday}",
                            color = TaxiYellow
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        EarningsStat(
                            icon = Icons.Default.Schedule,
                            label = "Ժամ առցանց",
                            value = "${stats.hoursOnline}ժ",
                            color = Color.Blue
                        )
                        
                        EarningsStat(
                            icon = Icons.Default.TrendingUp,
                            label = "Միջին",
                            value = "${if (stats.tripsToday > 0) stats.todayEarnings / stats.tripsToday else 0} AMD",
                            color = Color(0xFF9C27B0)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Weekly Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Շաբաթական ամփոփում",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        EarningsStat(
                            icon = Icons.Default.AccountBalanceWallet,
                            label = "Շաբ. գումար",
                            value = "${stats.weeklyEarnings} AMD",
                            color = Color.Green
                        )
                        
                        EarningsStat(
                            icon = Icons.Default.DateRange,
                            label = "Ա. օրեր",
                            value = "${stats.activeDaysThisWeek}",
                            color = TaxiYellow
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Recent Earnings
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Վերջին վարձակալություններ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    earningsHistory.forEach { earning ->
                        EarningItem(earning = earning)
                        if (earning != earningsHistory.last()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.Gray.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun EarningsStat(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun EarningItem(earning: EarningRecord) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = earning.tripId,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = earning.date,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "${earning.duration} • ${earning.distance}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        
        Text(
            text = "+${earning.amount} AMD",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Green
        )
    }
}
