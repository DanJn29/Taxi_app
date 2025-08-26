package com.example.taxi_app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.data.AppMode
import com.example.taxi_app.ui.components.TaxiButton
import com.example.taxi_app.ui.theme.*

@Composable
fun AppModeSelector(
    onModeSelected: (AppMode) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxiBackground)
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val isSmallScreen = screenWidth < 360.dp || screenHeight < 640.dp
        val isLargeScreen = screenWidth > 500.dp
        
        val horizontalPadding = when {
            isLargeScreen -> (screenWidth * 0.15f).coerceAtMost(80.dp)
            isSmallScreen -> 16.dp
            else -> 24.dp
        }
        
        val logoSize = when {
            isSmallScreen -> 80.dp
            isLargeScreen -> 120.dp
            else -> 100.dp
        }
        
        val titleSize = when {
            isSmallScreen -> 24.sp
            isLargeScreen -> 36.sp
            else -> 32.sp
        }
        
        val cardHeight = when {
            isSmallScreen -> 100.dp
            isLargeScreen -> 140.dp
            else -> 120.dp
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        // App Logo/Title
        Card(
            modifier = Modifier
                .size(logoSize)
                .padding(bottom = if (isSmallScreen) 24.dp else 32.dp),
            shape = RoundedCornerShape(logoSize / 2),
            colors = CardDefaults.cardColors(containerColor = TaxiYellow)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalTaxi,
                    contentDescription = "Taxi",
                    modifier = Modifier.size(logoSize * 0.5f),
                    tint = TaxiBlack
                )
            }
        }
        
        Text(
            text = "Taxi Platform",
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            color = TaxiBlack,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Ընտրիր գործառնակարգ",
            fontSize = if (isSmallScreen) 14.sp else 16.sp,
            color = TaxiGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = if (isSmallScreen) 32.dp else 48.dp)
        )
        
        // Mode Selection Cards
        Column(
            verticalArrangement = Arrangement.spacedBy(if (isSmallScreen) 12.dp else 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ModeCard(
                title = "Ընկերության վահանակ",
                subtitle = "Կառավարիր ֆլոտ, վարորդներ և երթուղիներ",
                icon = Icons.Default.Business,
                onClick = { onModeSelected(AppMode.COMPANY) },
                height = cardHeight,
                isSmallScreen = isSmallScreen
            )
            
            ModeCard(
                title = "Ուղևոր",
                subtitle = "Ամրագրիր երթուղի և հետևիր գնացքին",
                icon = Icons.Default.Person,
                onClick = { onModeSelected(AppMode.CLIENT) },
                height = cardHeight,
                isSmallScreen = isSmallScreen
            )
            
            ModeCard(
                title = "Վարորդ",
                subtitle = "Ստացիր հայտեր և վարիր երթուղիներ",
                icon = Icons.Default.DirectionsCar,
                onClick = { onModeSelected(AppMode.DRIVER) },
                height = cardHeight,
                isSmallScreen = isSmallScreen
            )
        }
        
        Spacer(modifier = Modifier.height(if (isSmallScreen) 16.dp else 32.dp))
        
        Text(
            text = "© 2025 Taxi Platform",
            fontSize = if (isSmallScreen) 10.sp else 12.sp,
            color = TaxiGray,
            textAlign = TextAlign.Center
        )
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    height: Dp,
    isSmallScreen: Boolean
) {
    val iconSize = if (isSmallScreen) 50.dp else 60.dp
    val titleSize = if (isSmallScreen) 16.sp else 18.sp
    val subtitleSize = if (isSmallScreen) 12.sp else 14.sp
    val padding = if (isSmallScreen) 16.dp else 20.dp
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(iconSize),
                shape = RoundedCornerShape(iconSize / 2),
                colors = CardDefaults.cardColors(containerColor = TaxiYellow.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(iconSize * 0.5f),
                        tint = TaxiBlack
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(if (isSmallScreen) 12.dp else 16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = titleSize,
                    fontWeight = FontWeight.SemiBold,
                    color = TaxiBlack,
                    maxLines = if (isSmallScreen) 1 else 2
                )
                Text(
                    text = subtitle,
                    fontSize = subtitleSize,
                    color = TaxiGray,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = if (isSmallScreen) 2 else 3
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Navigate",
                tint = TaxiGray,
                modifier = Modifier.size(if (isSmallScreen) 20.dp else 24.dp)
            )
        }
    }
}
