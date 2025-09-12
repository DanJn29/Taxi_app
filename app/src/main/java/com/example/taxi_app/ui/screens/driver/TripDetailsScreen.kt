package com.example.taxi_app.ui.screens.driver

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.taxi_app.data.api.DriverTripData
import com.example.taxi_app.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsScreen(
    trip: DriverTripData,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ComposeColor.White)
    ) {
        // Top app bar
        TopAppBar(
            title = {
                Text(
                    text = "Ուղևորություն",
                    color = TaxiBlack,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Վերադարձ",
                        tint = TaxiBlack
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = TaxiWhite
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
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
                        // Check if coordinates are valid (not 0.0, 0.0)
                        if (trip.from_lat != 0.0 && trip.from_lng != 0.0 && 
                            trip.to_lat != 0.0 && trip.to_lng != 0.0) {
                            TripRouteMap(
                                fromLat = trip.from_lat,
                                fromLng = trip.from_lng,
                                toLat = trip.to_lat,
                                toLng = trip.to_lng,
                                fromAddr = trip.from_addr,
                                toAddr = trip.to_addr,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clipToBounds()
                            )
                        } else {
                            // Show address-only view when coordinates are not available
                            AddressOnlyView(
                                fromAddr = trip.from_addr,
                                toAddr = trip.to_addr,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
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
                                        color = ComposeColor(0xFF4CAF50),
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
                                        color = ComposeColor(0xFF2196F3),
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
                                        color = ComposeColor(0xFFF44336),
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

            // Trip Information
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TaxiWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Ուղևորության մանրամասներ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxiBlack
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Կարգավիճակ:",
                            fontSize = 14.sp,
                            color = TaxiBlack.copy(alpha = 0.7f)
                        )
                        
                        val (statusText, statusColor) = when (trip.status) {
                            "pending" -> "Սպասակցում" to TaxiYellow
                            "accepted" -> "Ընդունված" to ComposeColor.Green
                            "cancelled" -> "Չեղարկված" to ComposeColor.Red
                            "completed" -> "Ավարտված" to ComposeColor.Blue
                            else -> trip.status to TaxiBlack
                        }
                        
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f))
                        ) {
                            Text(
                                text = statusText,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                color = statusColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Գին:",
                            fontSize = 14.sp,
                            color = TaxiBlack.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "${trip.price_amd} դրամ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxiYellow
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Date and Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ժամանակ:",
                            fontSize = 14.sp,
                            color = TaxiBlack.copy(alpha = 0.7f)
                        )
                        Text(
                            text = trip.departure_at,
                            fontSize = 14.sp,
                            color = TaxiBlack
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Route Information
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TaxiWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Երթուղի",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxiBlack
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // From
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Card(
                            shape = RoundedCornerShape(50),
                            colors = CardDefaults.cardColors(containerColor = ComposeColor.Green.copy(alpha = 0.1f)),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(ComposeColor.Green, shape = RoundedCornerShape(50))
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = "Սկիզբ",
                                fontSize = 12.sp,
                                color = TaxiBlack.copy(alpha = 0.7f)
                            )
                            Text(
                                text = trip.from_addr,
                                fontSize = 14.sp,
                                color = TaxiBlack
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // To
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Card(
                            shape = RoundedCornerShape(50),
                            colors = CardDefaults.cardColors(containerColor = ComposeColor.Red.copy(alpha = 0.1f)),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(ComposeColor.Red, shape = RoundedCornerShape(50))
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = "Վերջ",
                                fontSize = 12.sp,
                                color = TaxiBlack.copy(alpha = 0.7f)
                            )
                            Text(
                                text = trip.to_addr,
                                fontSize = 14.sp,
                                color = TaxiBlack
                            )
                        }
                    }
                }
            }

            // Amenities section
            if (!trip.amenities.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxiWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Հարմարություններ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxiBlack
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        trip.amenities.forEach { amenity ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = TaxiYellow,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = amenity.name,
                                    fontSize = 14.sp,
                                    color = TaxiBlack
                                )
                            }
                        }
                    }
                }
            }

//            // Trip Statistics
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = CardDefaults.cardColors(containerColor = TaxiWhite),
//                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    Text(
//                        text = "Վիճակագրություն",
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = TaxiBlack
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text(
//                            text = "Ընդհանուր նստատեղեր:",
//                            fontSize = 14.sp,
//                            color = TaxiBlack.copy(alpha = 0.7f)
//                        )
//                        Text(
//                            text = "${trip.seats_total}",
//                            fontSize = 14.sp,
//                            color = TaxiBlack
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text(
//                            text = "Գրավված նստատեղեր:",
//                            fontSize = 14.sp,
//                            color = TaxiBlack.copy(alpha = 0.7f)
//                        )
//                        Text(
//                            text = "${trip.seats_taken}",
//                            fontSize = 14.sp,
//                            color = TaxiBlack
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text(
//                            text = "Առկա նստատեղեր:",
//                            fontSize = 14.sp,
//                            color = TaxiBlack.copy(alpha = 0.7f)
//                        )
//                        Text(
//                            text = "${trip.seats_total - trip.seats_taken}",
//                            fontSize = 14.sp,
//                            color = ComposeColor.Green,
//                            fontWeight = FontWeight.Medium
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text(
//                            text = "Սպասակցման հայտեր:",
//                            fontSize = 14.sp,
//                            color = TaxiBlack.copy(alpha = 0.7f)
//                        )
//                        Text(
//                            text = "${trip.pending_requests_count}",
//                            fontSize = 14.sp,
//                            color = TaxiYellow,
//                            fontWeight = FontWeight.Medium
//                        )
//                    }
//                }
//            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Enhanced helper function to get real road routing with navigator-style details
private suspend fun getRealRoute(from: GeoPoint, to: GeoPoint): Pair<List<GeoPoint>, String> {
    return try {
        withContext(Dispatchers.IO) {
            // Use OSRM with more detailed parameters for navigator-style routing
            val url = "https://router.project-osrm.org/route/v1/driving/${from.longitude},${from.latitude};${to.longitude},${to.latitude}?overview=full&geometries=geojson&steps=true&annotations=duration,distance"
            
            val connection = URL(url).openConnection()
            connection.connectTimeout = 10000 // 10 seconds timeout
            connection.readTimeout = 10000
            val response = connection.getInputStream().bufferedReader().readText()
            
            // Parse JSON response using more robust JSON parsing
            val json = org.json.JSONObject(response)
            val routes = json.getJSONArray("routes")
            
            if (routes.length() > 0) {
                val route = routes.getJSONObject(0)
                val geometry = route.getJSONObject("geometry")
                val coordinates = geometry.getJSONArray("coordinates")
                
                // Extract route info
                val distance = route.getDouble("distance") // in meters
                val duration = route.getDouble("duration") // in seconds
                
                val points = mutableListOf<GeoPoint>()
                
                // Extract coordinate points for smooth route display
                for (i in 0 until coordinates.length()) {
                    val coord = coordinates.getJSONArray(i)
                    val lng = coord.getDouble(0)
                    val lat = coord.getDouble(1)
                    points.add(GeoPoint(lat, lng))
                }
                
                // Format navigator-style route info
                val distanceKm = String.format("%.1f", distance / 1000)
                val durationMin = (duration / 60).toInt()
                val hours = durationMin / 60
                val minutes = durationMin % 60
                
                val timeText = if (hours > 0) {
                    "${hours}ժ ${minutes}ր"
                } else {
                    "${minutes} րոպե"
                }
                
                // Navigator-style route info with emojis
                val routeInfo = "� ${distanceKm} կմ • ⏱️ ${timeText}"
                
                points to routeInfo
            } else {
                // Fallback if no routes found
                createEnhancedSimpleRoute(from, to) to "📍 Ուղիղ գիծ • ⚠️ Երթուղի չգտնվեց"
            }
        }
    } catch (e: Exception) {
        android.util.Log.e("NavigatorRoute", "Error getting route: ${e.message}")
        createEnhancedSimpleRoute(from, to) to "📍 Ուղիղ գիծ • ⚠️ Ցանցային սխալ"
    }
}

// Enhanced helper function for better simple routing (fallback) - Navigator style
private fun createEnhancedSimpleRoute(from: GeoPoint, to: GeoPoint): List<GeoPoint> {
    val points = mutableListOf<GeoPoint>()
    points.add(from)
    
    val latDiff = to.latitude - from.latitude
    val lngDiff = to.longitude - from.longitude
    val distance = from.distanceToAsDouble(to)
    
    // Create more intermediate points for smoother navigator-style route
    val numPoints = when {
        distance > 50000 -> 15 // > 50km: 15 points for very smooth line
        distance > 20000 -> 12 // > 20km: 12 points
        distance > 10000 -> 8  // > 10km: 8 points
        distance > 5000 -> 6   // > 5km: 6 points
        distance > 1000 -> 4   // > 1km: 4 points
        else -> 3              // <= 1km: 3 points
    }
    
    // Add slight curve to make it look more like a real road
    for (i in 1 until numPoints) {
        val ratio = i.toDouble() / numPoints
        
        // Add slight sine wave for more realistic path
        val curveOffset = kotlin.math.sin(ratio * kotlin.math.PI) * 0.0001 * (distance / 10000)
        
        val lat = from.latitude + (latDiff * ratio) + curveOffset
        val lng = from.longitude + (lngDiff * ratio)
        points.add(GeoPoint(lat, lng))
    }
    
    points.add(to)
    return points
}

@Composable
private fun TripRouteMap(
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
    var routeInfo by remember { mutableStateOf<String>("") }
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

    Column(modifier = modifier) {
        // Navigator-style route info bar
        if (routeInfo.isNotEmpty() && !isLoadingRoute) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ComposeColor(0xFF1976D2).copy(alpha = 0.1f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = routeInfo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = ComposeColor(0xFF1976D2)
                    )
                }
            }
        }

        // Map container
        Box(modifier = Modifier.fillMaxSize().clipToBounds()) {
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
                    
                    // Add enhanced markers with navigator styling
                    val fromMarker = Marker(this).apply {
                        position = fromPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Սկիզբ: $fromAddr"
                        // Use location pin style marker
                        icon = context.getDrawable(android.R.drawable.ic_dialog_map)?.apply {
                            setTint(android.graphics.Color.parseColor("#4CAF50")) // Green for start
                            setBounds(0, 0, 60, 80) // Larger for better visibility
                        }
                        alpha = 0.9f
                    }
                    
                    val toMarker = Marker(this).apply {
                        position = toPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Վերջ: $toAddr"
                        // Use different icon for destination
                        icon = context.getDrawable(android.R.drawable.ic_dialog_map)?.apply {
                            setTint(android.graphics.Color.parseColor("#F44336")) // Red for destination
                            setBounds(0, 0, 60, 80) // Larger for better visibility
                        }
                        alpha = 0.9f
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
                    
                    // Add route outline (shadow/border) - Navigator style
                    val routeOutline = Polyline().apply {
                        setPoints(routePoints)
                        outlinePaint.color = android.graphics.Color.parseColor("#1A000000") // Dark shadow
                        outlinePaint.strokeWidth = 18f // Wider for outline effect
                        outlinePaint.strokeCap = android.graphics.Paint.Cap.ROUND
                        outlinePaint.strokeJoin = android.graphics.Paint.Join.ROUND
                    }
                    
                    // Add main route line - Navigator style with gradient-like effect
                    val routeLine = Polyline().apply {
                        setPoints(routePoints)
                        outlinePaint.color = android.graphics.Color.parseColor("#1976D2") // Deep Blue like Google Maps
                        outlinePaint.strokeWidth = 12f
                        outlinePaint.strokeCap = android.graphics.Paint.Cap.ROUND
                        outlinePaint.strokeJoin = android.graphics.Paint.Join.ROUND
                        // Add slight shadow for depth
                        outlinePaint.setShadowLayer(2f, 1f, 1f, android.graphics.Color.parseColor("#40000000"))
                    }
                    
                    // Add inner route line for navigator effect
                    val innerRouteLine = Polyline().apply {
                        setPoints(routePoints)
                        outlinePaint.color = android.graphics.Color.parseColor("#42A5F5") // Lighter blue center
                        outlinePaint.strokeWidth = 6f
                        outlinePaint.strokeCap = android.graphics.Paint.Cap.ROUND
                        outlinePaint.strokeJoin = android.graphics.Paint.Join.ROUND
                    }
                    
                    // Add direction arrows along the route - Navigator style
                    if (routePoints.size > 2) {
                        val arrowPoints = mutableListOf<GeoPoint>()
                        val totalPoints = routePoints.size
                        val arrowInterval = maxOf(1, totalPoints / 5) // Add 5 arrows max
                        
                        for (i in arrowInterval until totalPoints - arrowInterval step arrowInterval) {
                            arrowPoints.add(routePoints[i])
                        }
                        
                        arrowPoints.forEach { point ->
                            val arrowMarker = Marker(mapView).apply {
                                position = point
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                                // Use a simple arrow icon (can be replaced with custom drawable)
                                icon = context.getDrawable(android.R.drawable.ic_media_play)?.apply {
                                    setTint(android.graphics.Color.parseColor("#1976D2"))
                                    setBounds(0, 0, 20, 20)
                                }
                                alpha = 0.8f
                            }
                            mapView.overlays.add(arrowMarker)
                        }
                    }
                    
                    // Re-add routes in correct order (outline, main, inner) and markers
                    mapView.overlays.add(routeOutline)
                    mapView.overlays.add(routeLine)
                    mapView.overlays.add(innerRouteLine)
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
}

@Composable
private fun AddressOnlyView(
    fromAddr: String,
    toAddr: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(ComposeColor(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Map unavailable icon
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location",
            tint = TaxiYellow,
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Քարտեզ հասանելի չէ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TaxiGray,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // From address
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = ComposeColor(0xFF4CAF50),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = fromAddr,
                fontSize = 12.sp,
                color = TaxiBlack,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Dashed line
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(16.dp)
                .background(
                    color = TaxiGray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(1.dp)
                )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // To address
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = ComposeColor(0xFFF44336),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = toAddr,
                fontSize = 12.sp,
                color = TaxiBlack,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
