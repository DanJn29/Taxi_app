package com.example.taxi_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.data.Company
import com.example.taxi_app.data.Screen
import com.example.taxi_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxiLayout(
    company: Company,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onLogout: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val tabs = listOf(
        Triple(Screen.Dashboard, "Սկիզբ", Icons.Default.Dashboard),
        Triple(Screen.Fleet, "Ֆլոտ", Icons.Default.DirectionsCar),
        Triple(Screen.Members, "Վարորդներ", Icons.Default.Group),
        Triple(Screen.Trips, "Երթուղիներ", Icons.Default.Route),
        Triple(Screen.Requests, "Հայտեր", Icons.Default.Description)
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxiBackground)
    ) {
        val screenWidth = maxWidth
        val isSmallScreen = screenWidth < 360.dp
        val headerPadding = if (isSmallScreen) 12.dp else 16.dp
        val titleSize = if (isSmallScreen) 16.sp else 18.sp
        val tabPadding = if (isSmallScreen) PaddingValues(horizontal = 12.dp) else PaddingValues(horizontal = 16.dp)
        
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = TaxiYellow,
                shadowElevation = 4.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(headerPadding),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Taxi Platform · ${company.name}",
                            fontSize = titleSize,
                            fontWeight = FontWeight.Bold,
                            color = TaxiBlack
                        )
                    }
                    
                    // Navigation tabs
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = tabPadding,
                        horizontalArrangement = Arrangement.spacedBy(if (isSmallScreen) 6.dp else 8.dp)
                    ) {
                        items(tabs) { (screen, label, icon) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (currentScreen == screen) TaxiBlack 
                                        else androidx.compose.ui.graphics.Color.Transparent
                                    )
                                    .clickable { onNavigate(screen) }
                                    .padding(
                                        horizontal = if (isSmallScreen) 8.dp else 12.dp, 
                                        vertical = if (isSmallScreen) 6.dp else 8.dp
                                    )
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(if (isSmallScreen) 16.dp else 18.dp),
                                    tint = if (currentScreen == screen) TaxiYellow else TaxiBlack
                                )
                                Spacer(modifier = Modifier.width(if (isSmallScreen) 6.dp else 8.dp))
                                Text(
                                    text = label,
                                    fontSize = if (isSmallScreen) 12.sp else 14.sp,
                                    color = if (currentScreen == screen) TaxiYellow else TaxiBlack,
                                    fontWeight = if (currentScreen == screen) FontWeight.Medium else FontWeight.Normal
                                )
                            }
                        }
                        
                        // Logout button placed after all tabs
                        onLogout?.let { logout ->
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(TaxiBlack.copy(alpha = 0.1f))
                                        .clickable { logout() }
                                        .padding(
                                            horizontal = if (isSmallScreen) 8.dp else 12.dp, 
                                            vertical = if (isSmallScreen) 6.dp else 8.dp
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = "Դուրս գալ",
                                        modifier = Modifier.size(if (isSmallScreen) 16.dp else 18.dp),
                                        tint = TaxiBlack
                                    )
                                    Spacer(modifier = Modifier.width(if (isSmallScreen) 6.dp else 8.dp))
                                    Text(
                                        text = "Դուրս գալ",
                                        fontSize = if (isSmallScreen) 12.sp else 14.sp,
                                        color = TaxiBlack,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(if (isSmallScreen) 12.dp else 16.dp)
            ) {
                content()
            }

            // Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isSmallScreen) 12.dp else 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "© 2025 Taxi Platform",
                    fontSize = if (isSmallScreen) 10.sp else 12.sp,
                    color = TaxiGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
