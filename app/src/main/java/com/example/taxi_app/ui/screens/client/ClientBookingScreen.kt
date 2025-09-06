package com.example.taxi_app.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.taxi_app.data.*
import com.example.taxi_app.ui.components.*
import com.example.taxi_app.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.net.URL
import kotlinx.coroutines.launch
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientBookingScreen(
    trip: Trip,
    user: User,
    onBookTrip: (Int, String, String) -> Unit,
    onBack: () -> Unit
) {
    var selectedSeats by remember { mutableStateOf(1) }
    var selectedPayment by remember { mutableStateOf("cash") }
    var notes by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val availableSeats = trip.seatsTotal - trip.seatsTaken
    val totalPrice = trip.priceAmd * selectedSeats

    val paymentMethods = listOf(
        "cash" to "Կանխիկ",
        "card" to "Բանկային քարտ"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxiBackground)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "Ամրագրում",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TaxiBlack
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Վերադառնալ",
                        tint = TaxiBlack
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = TaxiYellow
            )
        )
        
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Trip Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TaxiWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Երթուղու մանրամասներ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TaxiBlack,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Route
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FiberManualRecord,
                                    contentDescription = null,
                                    tint = StatusPublished,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = trip.fromAddr,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TaxiBlack
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = StatusError,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = trip.toAddr,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TaxiBlack
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Vehicle and Driver Info
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier.size(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = CardDefaults.cardColors(containerColor = TaxiYellow.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = TaxiBlack
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = "${trip.vehicle?.brand} ${trip.vehicle?.model}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TaxiBlack
                            )
                            Text(
                                text = "Վարորդ՝ ${trip.driver?.name}",
                                fontSize = 14.sp,
                                color = TaxiGray
                            )
                            // Add plate number and color
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                trip.vehicle?.plate?.let { plate ->
                                    Text(
                                        text = "Համար՝ $plate",
                                        fontSize = 14.sp,
                                        color = TaxiGray
                                    )
                                    
                                    trip.vehicle.color?.let { color ->
                                        Text(
                                            text = " • $color",
                                            fontSize = 14.sp,
                                            color = TaxiGray
                                        )
                                    }
                                }
                                
                                // Show color only if no plate
                                if (trip.vehicle?.plate == null) {
                                    trip.vehicle?.color?.let { color ->
                                        Text(
                                            text = "Գույն՝ $color",
                                            fontSize = 14.sp,
                                            color = TaxiGray
                                        )
                                    }
                                }
                            }
                            if (trip.departureAt != null) {
                                Text(
                                    text = "Մեկնում՝ ${trip.departureAt}",
                                    fontSize = 14.sp,
                                    color = TaxiGray
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Route Map Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TaxiWhite)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Երթուղի",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TaxiBlack,
                        modifier = Modifier.padding(bottom = 12.dp),
                        maxLines = 1
                    )
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        RouteMap(
                            fromLat = trip.fromLat,
                            fromLng = trip.fromLng,
                            toLat = trip.toLat,
                            toLng = trip.toLng,
                            fromAddr = trip.fromAddr,
                            toAddr = trip.toAddr,
                            modifier = Modifier
                                .fillMaxSize()
                                .clipToBounds()
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Route legend
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            color = Color(0xFF4CAF50),
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Սկիզբ",
                                    fontSize = 12.sp,
                                    color = TaxiGray
                                )
                            }
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(16.dp)
                                        .height(3.dp)
                                        .background(
                                            color = Color(0xFF2196F3),
                                            shape = RoundedCornerShape(2.dp)
                                        )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Երթուղի",
                                    fontSize = 12.sp,
                                    color = TaxiGray,
                                    maxLines = 1
                                )
                            }
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            color = Color(0xFFF44336),
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Վերջ",
                                    fontSize = 12.sp,
                                    color = TaxiGray
                                )
                            }
                        }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Booking Options Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TaxiWhite)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Ամրագրման տվյալներ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TaxiBlack,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Seat Selection
                    Text(
                        text = "Տեղերի քանակ",
                        fontSize = 14.sp,
                        color = TaxiGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        IconButton(
                            onClick = { if (selectedSeats > 1) selectedSeats-- },
                            enabled = selectedSeats > 1
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Նվազեցնել")
                        }
                        
                        Text(
                            text = selectedSeats.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        IconButton(
                            onClick = { if (selectedSeats < availableSeats) selectedSeats++ },
                            enabled = selectedSeats < availableSeats
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Ավելացնել")
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Text(
                            text = "$availableSeats տեղ հասանելի",
                            fontSize = 14.sp,
                            color = TaxiGray
                        )
                    }
                    
                    // Payment Method
                    TaxiDropdown(
                        value = selectedPayment,
                        onValueChange = { selectedPayment = it },
                        label = "Վճարման եղանակ",
                        options = paymentMethods,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Notes
                    TaxiTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = "Լրացուցիչ նշումներ (ընտրովի)",
                        placeholder = "Կարող եք ավելացնել լրացուցիչ տեղեկություններ...",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Price Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TaxiYellow.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Գին մեկ տեղի համար:",
                            fontSize = 14.sp,
                            color = TaxiGray
                        )
                        Text(
                            text = "${trip.priceAmd} AMD",
                            fontSize = 14.sp,
                            color = TaxiBlack
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Տեղերի քանակ:",
                            fontSize = 14.sp,
                            color = TaxiGray
                        )
                        Text(
                            text = selectedSeats.toString(),
                            fontSize = 14.sp,
                            color = TaxiBlack
                        )
                    }
                    
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = TaxiGray.copy(alpha = 0.3f)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ընդամենը:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxiBlack
                        )
                        Text(
                            text = "$totalPrice AMD",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxiBlack
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Book Button
            TaxiButton(
                text = "Ամրագրել ($totalPrice AMD)",
                onClick = { showConfirmDialog = true },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    
    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text("Հաստատել ամրագրումը")
            },
            text = {
                Text("Վստա՞հ եք, որ ցանկանում եք ամրագրել $selectedSeats տեղ $totalPrice AMD գնով:")
            },
            confirmButton = {
                TaxiButton(
                    text = "Հաստատել",
                    onClick = {
                        onBookTrip(selectedSeats, selectedPayment, notes)
                        showConfirmDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Չեղարկել", color = TaxiGray)
                }
            }
        )
    }
}

// Helper function to get real road routing using OSRM (OpenStreetMap Routing Machine)
private suspend fun getRealRoute(from: GeoPoint, to: GeoPoint): Pair<List<GeoPoint>, RouteInfo?> {
    return try {
        withContext(Dispatchers.IO) {
            // Use OSRM demo server for routing (free, no API key needed)
            val url = "https://router.project-osrm.org/route/v1/driving/" +
                    "${from.longitude},${from.latitude};${to.longitude},${to.latitude}" +
                    "?overview=full&geometries=geojson"
            
            val connection = URL(url).openConnection()
            val response = connection.getInputStream().bufferedReader().readText()
            
            val json = JSONObject(response)
            val routes = json.getJSONArray("routes")
            
            if (routes.length() > 0) {
                val route = routes.getJSONObject(0)
                val geometry = route.getJSONObject("geometry")
                val coordinates = geometry.getJSONArray("coordinates")
                
                // Extract route info
                val distance = route.getDouble("distance") // in meters
                val duration = route.getDouble("duration") // in seconds
                val routeInfo = RouteInfo(
                    distanceKm = (distance / 1000).toFloat(),
                    durationMinutes = (duration / 60).toInt()
                )
                
                val routePoints = mutableListOf<GeoPoint>()
                for (i in 0 until coordinates.length()) {
                    val coord = coordinates.getJSONArray(i)
                    val lng = coord.getDouble(0)
                    val lat = coord.getDouble(1)
                    routePoints.add(GeoPoint(lat, lng))
                }
                Pair(routePoints, routeInfo)
            } else {
                // Fallback to simple route if API fails
                Pair(createSimpleRoute(from, to), null)
            }
        }
    } catch (e: Exception) {
        android.util.Log.e("RouteMap", "Error getting real route: ${e.message}")
        // Fallback to simple route if API fails
        Pair(createSimpleRoute(from, to), null)
    }
}

// Data class for route information
data class RouteInfo(
    val distanceKm: Float,
    val durationMinutes: Int
)

// Enhanced helper function for better simple routing (fallback)
private fun createSimpleRoute(from: GeoPoint, to: GeoPoint): List<GeoPoint> {
    val points = mutableListOf<GeoPoint>()
    points.add(from)
    
    val latDiff = to.latitude - from.latitude
    val lngDiff = to.longitude - from.longitude
    val distance = from.distanceToAsDouble(to)
    
    // Create more intermediate points for longer distances
    val numPoints = when {
        distance < 1000 -> 3   // Short distance: 3 points
        distance < 5000 -> 5   // Medium distance: 5 points  
        distance < 20000 -> 8  // Long distance: 8 points
        else -> 12             // Very long distance: 12 points
    }
    
    for (i in 1..numPoints) {
        val progress = i.toDouble() / (numPoints + 1)
        val lat = from.latitude + (latDiff * progress)
        val lng = from.longitude + (lngDiff * progress)
        
        // Add road-like variations (simulate turning at intersections)
        val roadVariation = when {
            progress < 0.3 -> 0.0001 * kotlin.math.sin(progress * kotlin.math.PI * 4)
            progress < 0.7 -> 0.00015 * kotlin.math.cos(progress * kotlin.math.PI * 3)  
            else -> 0.0001 * kotlin.math.sin(progress * kotlin.math.PI * 2)
        }
        
        points.add(GeoPoint(lat + roadVariation, lng + roadVariation * 0.7))
    }
    
    points.add(to)
    return points
}

@Composable
private fun RouteMap(
    fromLat: Double,
    fromLng: Double,
    toLat: Double,
    toLng: Double,
    fromAddr: String,
    toAddr: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var routePoints by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }
    var routeInfo by remember { mutableStateOf<RouteInfo?>(null) }
    var isLoadingRoute by remember { mutableStateOf(true) }
    
    // Load real route when component initializes
    LaunchedEffect(fromLat, fromLng, toLat, toLng) {
        isLoadingRoute = true
        val fromPoint = GeoPoint(fromLat, fromLng)
        val toPoint = GeoPoint(toLat, toLng)
        
        // Try to get real route, fallback to simple route
        scope.launch {
            val (points, info) = getRealRoute(fromPoint, toPoint)
            routePoints = points
            routeInfo = info
            isLoadingRoute = false
        }
    }
    
    Box(modifier = modifier.clipToBounds()) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            factory = { ctx ->
                Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", 0))
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    
                    // Restrict zoom levels to prevent overflow
                    minZoomLevel = 8.0
                    maxZoomLevel = 18.0
                    
                    // Restrict scrolling to prevent overflow
                    setScrollableAreaLimitLatitude(89.5, -89.5, 0)
                    setScrollableAreaLimitLongitude(-179.5, 179.5, 0)
                    
                    // Disable fling gesture to prevent uncontrolled scrolling
                    isFlingEnabled = false
                    
                    // Set up the map view
                    val mapController = controller
                    
                    // Create route points
                    val fromPoint = GeoPoint(fromLat, fromLng)
                    val toPoint = GeoPoint(toLat, toLng)
                    
                    // Center map between the two points
                    val centerLat = (fromLat + toLat) / 2
                    val centerLng = (fromLng + toLng) / 2
                    val centerPoint = GeoPoint(centerLat, centerLng)
                    
                    mapController.setCenter(centerPoint)
                    
                    // Calculate zoom level based on distance
                    val distance = fromPoint.distanceToAsDouble(toPoint)
                    val zoomLevel = when {
                        distance < 1000 -> 16.0 // Less than 1km
                        distance < 5000 -> 14.0 // Less than 5km
                        distance < 20000 -> 12.0 // Less than 20km
                        else -> 10.0
                    }
                    mapController.setZoom(zoomLevel)
                    
                    // Add markers with better styling
                    val fromMarker = Marker(this).apply {
                        position = fromPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Սկիզբ: $fromAddr"
                        icon = context.getDrawable(android.R.drawable.ic_menu_mylocation)?.apply {
                            setTint(android.graphics.Color.parseColor("#4CAF50")) // Material Green
                        }
                    }
                    
                    val toMarker = Marker(this).apply {
                        position = toPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Վերջ: $toAddr"
                        icon = context.getDrawable(android.R.drawable.ic_menu_mylocation)?.apply {
                            setTint(android.graphics.Color.parseColor("#F44336")) // Material Red
                        }
                    }
                    
                    // Add markers to map
                    overlays.add(fromMarker)
                    overlays.add(toMarker)
                    
                    // Add padding to show both markers
                    post {
                        zoomToBoundingBox(
                            org.osmdroid.util.BoundingBox(
                                maxOf(fromLat, toLat) + 0.001,
                                maxOf(fromLng, toLng) + 0.001,
                                minOf(fromLat, toLat) - 0.001,
                                minOf(fromLng, toLng) - 0.001
                            ),
                            true,
                            50
                        )
                    }
                }
            },
            update = { mapView ->
                // Update route when routePoints change
                if (routePoints.isNotEmpty()) {
                    // Remove old route overlays (keep markers)
                    val markersToKeep = mapView.overlays.filterIsInstance<Marker>()
                    mapView.overlays.clear()
                    
                    // Add route line with real road data
                    val routeLine = Polyline().apply {
                        setPoints(routePoints)
                        outlinePaint.color = android.graphics.Color.parseColor("#2196F3") // Material Blue
                        outlinePaint.strokeWidth = 12f
                        outlinePaint.strokeCap = android.graphics.Paint.Cap.ROUND
                        outlinePaint.strokeJoin = android.graphics.Paint.Join.ROUND
                        // Add shadow effect
                        outlinePaint.setShadowLayer(4f, 2f, 2f, android.graphics.Color.parseColor("#80000000"))
                    }
                    
                    // Re-add route and markers
                    mapView.overlays.add(routeLine)
                    mapView.overlays.addAll(markersToKeep)
                    mapView.invalidate()
                }
            }
        )
        
        // Loading indicator for route
        if (isLoadingRoute) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxiWhite.copy(alpha = 0.9f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = TaxiYellow,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Երթուղի նախագծվում է...",
                            fontSize = 12.sp,
                            color = TaxiBlack
                        )
                    }
                }
            }
        }
    }
}
