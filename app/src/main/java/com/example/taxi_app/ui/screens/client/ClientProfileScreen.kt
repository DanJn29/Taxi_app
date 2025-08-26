package com.example.taxi_app.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.taxi_app.data.User
import com.example.taxi_app.ui.theme.TaxiBackground
import com.example.taxi_app.ui.theme.TaxiYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientProfileScreen(
    user: User,
    onBack: () -> Unit,
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
                    text = "Իմ պրոֆիլը",
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
                modifier = Modifier
                    .fillMaxWidth(),
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
                        text = user.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Text(
                        text = user.email,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    if (user.phone.isNotBlank()) {
                        Text(
                            text = user.phone,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Verification status
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (user.isVerified) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (user.isVerified) Color.Green else Color(0xFFFFA500),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (user.isVerified) "Հաստատված հաշիվ" else "Չհաստատված հաշիվ",
                            fontSize = 12.sp,
                            color = if (user.isVerified) Color.Green else Color(0xFFFFA500)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats Card
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
                        text = "Վիճակագրություն",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Rating
                    ProfileStatItem(
                        icon = Icons.Default.Star,
                        label = "Գնահատական",
                        value = "${user.rating}/5.0"
                    )
                    
                    // Total trips
                    ProfileStatItem(
                        icon = Icons.Default.DirectionsCar,
                        label = "Ամբողջ ճանապարհորդություններ",
                        value = "${user.totalTrips}"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Account Actions
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
                    
                    // Settings options
                    ProfileActionItem(
                        icon = Icons.Default.Edit,
                        label = "Խմբագրել պրոֆիլը",
                        onClick = { /* TODO: Edit profile */ }
                    )
                    
                    ProfileActionItem(
                        icon = Icons.Default.Notifications,
                        label = "Ծանուցումների կարգավորումներ",
                        onClick = { /* TODO: Notification settings */ }
                    )
                    
                    ProfileActionItem(
                        icon = Icons.Default.Help,
                        label = "Օգնություն և աջակցություն",
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
fun ProfileStatItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
fun ProfileActionItem(
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
