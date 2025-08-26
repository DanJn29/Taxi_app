package com.example.taxi_app.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ResponsiveDimensions(
    val isSmallScreen: Boolean,
    val isMediumScreen: Boolean,
    val isLargeScreen: Boolean,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val cardSpacing: Dp,
    val gridMinSize: Dp,
    val iconSize: Dp,
    val titleSize: TextUnit,
    val bodySize: TextUnit,
    val buttonHeight: Dp
)

@Composable
fun getResponsiveDimensions(screenWidth: Dp, screenHeight: Dp): ResponsiveDimensions {
    val isSmallScreen = screenWidth < 360.dp || screenHeight < 640.dp
    val isMediumScreen = screenWidth >= 360.dp && screenWidth <= 500.dp
    val isLargeScreen = screenWidth > 500.dp
    
    return ResponsiveDimensions(
        isSmallScreen = isSmallScreen,
        isMediumScreen = isMediumScreen,
        isLargeScreen = isLargeScreen,
        horizontalPadding = when {
            isLargeScreen -> (screenWidth * 0.15f).coerceAtMost(80.dp)
            isSmallScreen -> 16.dp
            else -> 24.dp
        },
        verticalPadding = when {
            isSmallScreen -> 16.dp
            else -> 24.dp
        },
        cardSpacing = when {
            isSmallScreen -> 12.dp
            else -> 16.dp
        },
        gridMinSize = when {
            isSmallScreen -> 160.dp
            isLargeScreen -> 250.dp
            else -> 200.dp
        },
        iconSize = when {
            isSmallScreen -> 60.dp
            isLargeScreen -> 100.dp
            else -> 80.dp
        },
        titleSize = when {
            isSmallScreen -> 24.sp
            isLargeScreen -> 32.sp
            else -> 28.sp
        },
        bodySize = when {
            isSmallScreen -> 14.sp
            else -> 16.sp
        },
        buttonHeight = when {
            isSmallScreen -> 48.dp
            else -> 56.dp
        }
    )
}
