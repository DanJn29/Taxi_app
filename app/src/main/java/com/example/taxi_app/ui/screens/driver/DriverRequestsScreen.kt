package com.example.taxi_app.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.data.Screen
import com.example.taxi_app.data.api.DriverRequestData
import com.example.taxi_app.ui.theme.*
import com.example.taxi_app.viewmodel.TaxiViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverRequestsScreen(
    viewModel: TaxiViewModel,
    navigationScrollState: androidx.compose.foundation.lazy.LazyListState,
    onNavigate: (com.example.taxi_app.data.Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val driverRequests by viewModel.driverRequests.collectAsState()
    val isLoadingRequests by viewModel.isLoadingRequests.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    
    // Get screen configuration for responsive design
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
    
    // Calculate responsive dimensions
    val horizontalPadding = when {
        screenWidth < 360.dp -> 12.dp
        screenWidth < 480.dp -> 16.dp
        screenWidth < 600.dp -> 20.dp
        else -> 24.dp
    }
    
    val verticalSpacing = when {
        screenHeight < 600.dp -> 8.dp
        screenHeight < 800.dp -> 12.dp
        else -> 16.dp
    }
    
    val bottomPadding = when {
        isLandscape -> 60.dp
        screenHeight < 600.dp -> 70.dp
        else -> 80.dp
    }

    // Additional padding to account for system navigation bar
    val systemBarPadding = WindowInsets.systemBars.asPaddingValues()

    LaunchedEffect(Unit) {
        viewModel.fetchDriverRequests()
    }

    // Show error message
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            kotlinx.coroutines.delay(3000)
            viewModel.clearErrorMessage()
        }
    }

    // Show success message  
    LaunchedEffect(successMessage) {
        successMessage?.let {
            kotlinx.coroutines.delay(3000)
            viewModel.clearSuccessMessage()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TaxiBackground)
            .statusBarsPadding(), // Add status bar padding
        contentPadding = PaddingValues(
            start = horizontalPadding,
            end = horizontalPadding,
            top = verticalSpacing,
            bottom = bottomPadding + systemBarPadding.calculateBottomPadding()
        ),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        item {
            // Header with title and refresh
            HeaderSection(
                onRefresh = { viewModel.fetchDriverRequests() },
                onBack = { viewModel.navigateToScreen(Screen.DriverDashboard) },
                isLoading = isLoadingRequests
            )
        }

        // Error and Success Messages
        errorMessage?.let { message ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(12.dp),
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }

        successMessage?.let { message ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(12.dp),
                        color = Color.Green,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Requests List
        if (isLoadingRequests) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TaxiYellow)
                }
            }
        } else if (driverRequests.isEmpty()) {
            item {
                EmptyRequestsSection()
            }
        } else {
            items(driverRequests) { request ->
                RequestCard(
                    request = request,
                    onAccept = { viewModel.acceptDriverRequest(request.id.toString()) },
                    onReject = { viewModel.rejectDriverRequest(request.id.toString()) }
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isSmallScreen = screenWidth < 360.dp
    val titleFontSize = if (isSmallScreen) 18.sp else 20.sp
    val iconSize = if (isSmallScreen) 20.dp else 24.dp
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        IconButton(
            onClick = onBack
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Վերադառնալ",
                tint = TaxiBlue,
                modifier = Modifier.size(iconSize)
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Assignment,
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = TaxiBlue
            )
            Spacer(modifier = Modifier.width(if (isSmallScreen) 6.dp else 8.dp))
            Text(
                text = "Ուղևորների հայտեր",
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = TaxiBlack,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        IconButton(
            onClick = onRefresh,
            enabled = !isLoading
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Թարմացնել",
                tint = if (isLoading) TaxiGray else TaxiBlue,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
private fun EmptyRequestsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
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
            text = "Նոր հայտեր չկան",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = TaxiGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Երբ ուղևորները հայտ ներկայացնեն ձեր ուղևորություններին, դրանք կտեսնեք այստեղ:",
            fontSize = 14.sp,
            color = TaxiGray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RequestCard(
    request: DriverRequestData,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isSmallScreen = screenWidth < 360.dp
    val isCompactHeight = configuration.screenHeightDp < 600
    
    // Responsive dimensions
    val cardPadding = if (isSmallScreen) 12.dp else 16.dp
    val titleFontSize = if (isSmallScreen) 14.sp else 16.sp
    val bodyFontSize = if (isSmallScreen) 12.sp else 14.sp
    val iconSize = if (isSmallScreen) 16.dp else 20.dp
    val smallIconSize = if (isSmallScreen) 14.dp else 16.dp
    val verticalSpacing = if (isCompactHeight) 8.dp else 12.dp
    val cornerRadius = if (isSmallScreen) 8.dp else 12.dp
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TaxiLightGray, RoundedCornerShape(cornerRadius)),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(cardPadding),
            verticalArrangement = Arrangement.spacedBy(verticalSpacing)
        ) {
            // Request Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Հայտ #${request.id}",
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        color = TaxiBlack,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
                
                StatusBadge(
                    status = request.status,
                    isSmallScreen = isSmallScreen
                )
            }

            // Client Information
            ClientInfoSection(
                request = request,
                iconSize = iconSize,
                bodyFontSize = bodyFontSize,
                isSmallScreen = isSmallScreen
            )

            // Trip Information
            request.trip?.let { trip ->
                TripInfoSection(
                    trip = trip,
                    seats = request.seats,
                    smallIconSize = smallIconSize,
                    bodyFontSize = bodyFontSize,
                    isSmallScreen = isSmallScreen
                )
            }

            // Action Buttons (only show for pending requests)
            if (request.status == "pending") {
                ActionButtonsSection(
                    onAccept = onAccept,
                    onReject = onReject,
                    isSmallScreen = isSmallScreen,
                    isCompactHeight = isCompactHeight
                )
            }
        }
    }
}

@Composable
private fun ClientInfoSection(
    request: DriverRequestData,
    iconSize: Dp,
    bodyFontSize: TextUnit,
    isSmallScreen: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = TaxiBlue,
            modifier = Modifier.size(iconSize)
        )
        Spacer(modifier = Modifier.width(if (isSmallScreen) 6.dp else 8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = request.passenger_name,
                fontSize = bodyFontSize,
                fontWeight = FontWeight.Medium,
                color = TaxiBlack,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Text(
                text = request.phone,
                fontSize = (bodyFontSize.value - 2).sp,
                color = TaxiGray,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
        
        // Payment method badge
        Box(
            modifier = Modifier
                .background(
                    TaxiBlue.copy(alpha = 0.1f),
                    RoundedCornerShape(if (isSmallScreen) 6.dp else 8.dp)
                )
                .padding(
                    horizontal = if (isSmallScreen) 6.dp else 8.dp,
                    vertical = if (isSmallScreen) 3.dp else 4.dp
                )
        ) {
            Text(
                text = request.payment.uppercase(),
                fontSize = (bodyFontSize.value - 2).sp,
                fontWeight = FontWeight.Medium,
                color = TaxiBlue
            )
        }
    }
}

@Composable
private fun TripInfoSection(
    trip: com.example.taxi_app.data.api.TripRequestData,
    seats: Int,
    smallIconSize: Dp,
    bodyFontSize: TextUnit,
    isSmallScreen: Boolean
) {
    Column {
        // From location
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.Green,
                modifier = Modifier.size(smallIconSize)
            )
            Spacer(modifier = Modifier.width(if (isSmallScreen) 6.dp else 8.dp))
            Text(
                text = trip.from_addr,
                fontSize = bodyFontSize,
                color = TaxiBlack,
                modifier = Modifier.weight(1f),
                maxLines = if (isSmallScreen) 1 else 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // To location
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(smallIconSize)
            )
            Spacer(modifier = Modifier.width(if (isSmallScreen) 6.dp else 8.dp))
            Text(
                text = trip.to_addr,
                fontSize = bodyFontSize,
                color = TaxiBlack,
                modifier = Modifier.weight(1f),
                maxLines = if (isSmallScreen) 1 else 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
        
        Spacer(modifier = Modifier.height(if (isSmallScreen) 8.dp else 12.dp))
        
        // Trip details in responsive layout
        if (isSmallScreen) {
            // Stack vertically on small screens
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = TaxiBlue,
                            modifier = Modifier.size(smallIconSize)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatDateTime(trip.departure_at),
                            fontSize = (bodyFontSize.value - 2).sp,
                            color = TaxiGray,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                    
                    Text(
                        text = "${trip.price_amd} դր.",
                        fontSize = bodyFontSize,
                        fontWeight = FontWeight.Bold,
                        color = TaxiBlue
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TaxiBlue,
                        modifier = Modifier.size(smallIconSize)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$seats նստատեղ",
                        fontSize = (bodyFontSize.value - 2).sp,
                        color = TaxiGray
                    )
                }
            }
        } else {
            // Horizontal layout for larger screens
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = TaxiBlue,
                            modifier = Modifier.size(smallIconSize)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatDateTime(trip.departure_at),
                            fontSize = (bodyFontSize.value - 2).sp,
                            color = TaxiGray,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                    
                    Text(
                        text = "${trip.price_amd} դր.",
                        fontSize = bodyFontSize,
                        fontWeight = FontWeight.Bold,
                        color = TaxiBlue
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TaxiBlue,
                        modifier = Modifier.size(smallIconSize)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$seats նստատեղ",
                        fontSize = (bodyFontSize.value - 2).sp,
                        color = TaxiGray
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButtonsSection(
    onAccept: () -> Unit,
    onReject: () -> Unit,
    isSmallScreen: Boolean,
    isCompactHeight: Boolean
) {
    val buttonPadding = if (isSmallScreen || isCompactHeight) {
        PaddingValues(vertical = 8.dp, horizontal = 12.dp)
    } else {
        PaddingValues(vertical = 12.dp, horizontal = 16.dp)
    }
    
    val buttonFontSize = if (isSmallScreen) 12.sp else 14.sp
    val iconSize = if (isSmallScreen) 14.dp else 16.dp
    val buttonSpacing = if (isSmallScreen) 6.dp else 8.dp
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
    ) {
        OutlinedButton(
            onClick = onReject,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Red
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
            contentPadding = buttonPadding
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Մերժել",
                fontSize = buttonFontSize,
                maxLines = 1
            )
        }
        
        Button(
            onClick = onAccept,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Green,
                contentColor = TaxiWhite
            ),
            contentPadding = buttonPadding
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Ընդունել",
                fontSize = buttonFontSize,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun StatusBadge(
    status: String,
    isSmallScreen: Boolean = false
) {
    val (backgroundColor, textColor, text) = when (status) {
        "pending" -> Triple(TaxiYellow.copy(alpha = 0.2f), Color(0xFF8B5000), "Սպասող")
        "accepted" -> Triple(Color.Green.copy(alpha = 0.2f), Color.Green, "Ընդունված")
        "rejected" -> Triple(Color.Red.copy(alpha = 0.2f), Color.Red, "Մերժված")
        else -> Triple(TaxiGray.copy(alpha = 0.2f), TaxiGray, status)
    }
    
    val fontSize = if (isSmallScreen) 10.sp else 12.sp
    val cornerRadius = if (isSmallScreen) 8.dp else 12.dp
    val padding = if (isSmallScreen) {
        PaddingValues(horizontal = 6.dp, vertical = 3.dp)
    } else {
        PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    }
    
    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(cornerRadius))
            .padding(padding)
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

private fun formatDateTime(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}
