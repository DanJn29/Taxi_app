package com.example.taxi_app.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.data.DriverStats
import com.example.taxi_app.data.User
import com.example.taxi_app.ui.theme.TaxiBackground
import com.example.taxi_app.ui.theme.TaxiYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverProfileScreen(
    driver: User,
    stats: DriverStats,
    onBack: () -> Unit,
    onToggleAvailability: () -> Unit,
    onLogout: () -> Unit
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
                    text = "Վարորդի պրոֆիլ",
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

        // Profile Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture and Basic Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile picture placeholder
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(TaxiYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color.Black
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = driver.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Text(
                        text = "Վարորդ",
                        fontSize = 16.sp,
                        color = TaxiYellow,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = driver.email,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    if (driver.phone.isNotBlank()) {
                        Text(
                            text = driver.phone,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${driver.rating}/5.0",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = " (${driver.totalTrips} ճանապարհորդություն)",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Availability Toggle Section
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (stats.isAvailable) 
                                            Color(0xFF4CAF50).copy(alpha = 0.2f) 
                                        else 
                                            Color(0xFFFF5722).copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (stats.isAvailable) Icons.Default.CheckCircle else Icons.Default.PauseCircle,
                                    contentDescription = null,
                                    tint = if (stats.isAvailable) Color(0xFF4CAF50) else Color(0xFFFF5722),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column {
                                Text(
                                    text = "Հասանելիություն",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = if (stats.isAvailable) "Հասանելի եք նոր հայտերի համար" else "Նոր հայտեր չեք ստանալու",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        
                        Switch(
                            checked = stats.isAvailable,
                            onCheckedChange = { onToggleAvailability() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TaxiYellow,
                                checkedTrackColor = Color(0xFF4CAF50),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.Gray
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Driver Stats
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
                        text = "Վարորդի վիճակագրություն",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Driver specific stats
                    DriverStatItem(
                        icon = Icons.Default.AttachMoney,
                        label = "Ամսական եկամուտ",
                        value = "${stats.monthlyEarnings} AMD"
                    )
                    
                    DriverStatItem(
                        icon = Icons.Default.Schedule,
                        label = "Առցանց ժամեր",
                        value = "${stats.hoursOnline}ժ"
                    )
                    
                    DriverStatItem(
                        icon = Icons.Default.LocalTaxi,
                        label = "Ակտիվություն",
                        value = if (stats.isAvailable) "Մատչելի" else "Անմատչելի"
                    )
                    
                    DriverStatItem(
                        icon = Icons.Default.TrendingUp,
                        label = "Ամսական ճանապարհորդություններ",
                        value = "${stats.tripsCompleted}"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Account Management
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
                        text = "Հաշվի կառավարում",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Account actions
                    DriverActionItem(
                        icon = Icons.Default.Edit,
                        label = "Խմբագրել պրոֆիլը",
                        onClick = { /* TODO: Edit profile */ }
                    )
                    
                    DriverActionItem(
                        icon = Icons.Default.CarRental,
                        label = "Ավտոմեքենայի տվյալներ",
                        onClick = { /* TODO: Vehicle info */ }
                    )
                    
                    DriverActionItem(
                        icon = Icons.Default.Payment,
                        label = "Վճարման տվյալներ",
                        onClick = { /* TODO: Payment info */ }
                    )
                    
                    DriverActionItem(
                        icon = Icons.Default.Notifications,
                        label = "Ծանուցումներ",
                        onClick = { /* TODO: Notifications */ }
                    )
                    
                    DriverActionItem(
                        icon = Icons.Default.Help,
                        label = "Օգնություն",
                        onClick = { /* TODO: Help */ }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Logout Button
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Դուրս գալ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DriverStatItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TaxiYellow,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Composable
fun DriverActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.textButtonColors(
            contentColor = Color.Black
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TaxiYellow,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
