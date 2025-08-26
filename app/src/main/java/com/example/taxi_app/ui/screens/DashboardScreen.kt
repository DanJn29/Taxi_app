package com.example.taxi_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.data.Company
import com.example.taxi_app.data.Screen
import com.example.taxi_app.ui.components.TaxiCard
import com.example.taxi_app.ui.components.TaxiLayout
import com.example.taxi_app.ui.theme.TaxiBlack

@Composable
fun DashboardScreen(
    company: Company,
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit
) {
    TaxiLayout(
        company = company,
        currentScreen = Screen.Dashboard,
        onNavigate = onNavigate,
        onLogout = onLogout
    ) {
        BoxWithConstraints {
            val screenWidth = maxWidth
            val isSmallScreen = screenWidth < 360.dp
            val isLargeScreen = screenWidth > 500.dp
            
            val titleSize = when {
                isSmallScreen -> 24.sp
                isLargeScreen -> 32.sp
                else -> 28.sp
            }
            
            val gridMinSize = when {
                isSmallScreen -> 160.dp
                isLargeScreen -> 250.dp
                else -> 200.dp
            }
            
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Ընկերության վահանակ",
                    fontSize = titleSize,
                    fontWeight = FontWeight.Bold,
                    color = TaxiBlack,
                    modifier = Modifier.padding(bottom = if (isSmallScreen) 12.dp else 16.dp)
                )
                
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = gridMinSize),
                    horizontalArrangement = Arrangement.spacedBy(if (isSmallScreen) 12.dp else 16.dp),
                    verticalArrangement = Arrangement.spacedBy(if (isSmallScreen) 12.dp else 16.dp)
                ) {
                    items(
                        listOf(
                            "Մեքենաներ" to company.vehiclesCount.toString(),
                            "Երթուղիներ" to company.tripsCount.toString(),
                            "Սպասվող հայտեր" to company.pendingRequests.toString(),
                            "Սեփականատեր" to (company.owner?.name ?: "—")
                        )
                    ) { (title, value) ->
                        TaxiCard(
                            title = title,
                            value = value,
                            isSmallScreen = isSmallScreen
                        )
                    }
                }
            }
        }
    }
}
