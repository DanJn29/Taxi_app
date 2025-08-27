package com.example.taxi_app.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.library.R as OSMDroidR
import com.example.taxi_app.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import java.util.Locale

// Helper function to get user's current location
suspend fun getCurrentLocation(context: Context): GeoPoint? {
    return withContext(Dispatchers.IO) {
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            
            // Check if location permission is granted
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            
            if (!hasPermission) {
                return@withContext null
            }
            
            // Get last known location from GPS or Network provider
            val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            
            val bestLocation = when {
                gpsLocation != null && networkLocation != null -> {
                    if (gpsLocation.time > networkLocation.time) gpsLocation else networkLocation
                }
                gpsLocation != null -> gpsLocation
                networkLocation != null -> networkLocation
                else -> null
            }
            
            bestLocation?.let { location ->
                GeoPoint(location.latitude, location.longitude)
            }
        } catch (e: Exception) {
            null
        }
    }
}

@Composable
fun OSMMapView(
    modifier: Modifier = Modifier,
    initialLocation: GeoPoint = GeoPoint(40.1872, 44.5152), // Yerevan, Armenia
    markers: List<Pair<GeoPoint, String>> = emptyList(),
    onMapClick: ((GeoPoint) -> Unit)? = null,
    showLocationButton: Boolean = true,
    centerOnUserLocation: Boolean = true
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    // Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        if (hasLocationPermission) {
            coroutineScope.launch {
                userLocation = getCurrentLocation(context)
                userLocation?.let { location ->
                    if (centerOnUserLocation) {
                        mapView?.controller?.setZoom(18.0)
                        mapView?.controller?.animateTo(location)
                    }
                }
            }
        }
    }
    
    // Check location permission on startup
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        
        hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        if (hasLocationPermission) {
            userLocation = getCurrentLocation(context)
        } else if (centerOnUserLocation) {
            // Request location permission
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        
                        val mapController: IMapController = controller
                        
                        // Use user location if available and centerOnUserLocation is true, otherwise use initialLocation
                        val centerLocation = if (centerOnUserLocation && userLocation != null) {
                            userLocation!!
                        } else {
                            initialLocation
                        }
                        mapController.setCenter(centerLocation)
                        
                        // Set higher zoom when centering on user location
                        val zoomLevel = if (centerOnUserLocation && userLocation != null) 18.0 else 12.0
                        mapController.setZoom(zoomLevel)
                        
                        // Add regular markers
                        markers.forEach { (point, title) ->
                            val marker = Marker(this).apply {
                                position = point
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                this.title = title
                            }
                            overlays.add(marker)
                        }
                        
                        // Add user location marker if available
                        userLocation?.let { location ->
                            val userMarker = Marker(this).apply {
                                position = location
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = "Ձեր գտնվելու վայրը"
                                // Use OSMDroid's person icon
                                try {
                                    icon = context.getDrawable(OSMDroidR.drawable.person)
                                } catch (e: Exception) {
                                    // Fallback to system location icon if person icon not available
                                    icon = context.getDrawable(android.R.drawable.ic_menu_mylocation)
                                }
                            }
                            overlays.add(userMarker)
                        }
                        
                        // Handle map clicks
                        onMapClick?.let { clickHandler ->
                            setOnTouchListener { _, event ->
                                if (event.action == android.view.MotionEvent.ACTION_UP) {
                                    val projection = projection
                                    val geoPoint = projection.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint
                                    clickHandler(geoPoint)
                                }
                                false
                            }
                        }
                        
                        mapView = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { view ->
                // Update markers when they change
                view.overlays.clear()
                
                // Add regular markers
                markers.forEach { (point, title) ->
                    val marker = Marker(view).apply {
                        position = point
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        this.title = title
                    }
                    view.overlays.add(marker)
                }
                
                // Add user location marker if available
                userLocation?.let { location ->
                    val userMarker = Marker(view).apply {
                        position = location
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Ձեր գտնվելու վայրը"
                        try {
                            icon = context.getDrawable(OSMDroidR.drawable.person)
                        } catch (e: Exception) {
                            icon = context.getDrawable(android.R.drawable.ic_menu_mylocation)
                        }
                    }
                    view.overlays.add(userMarker)
                }
                
                view.invalidate()
            }
            
            // Location button
            if (showLocationButton) {
                FloatingActionButton(
                    onClick = {
                        if (hasLocationPermission) {
                            coroutineScope.launch {
                                val currentLocation = getCurrentLocation(context)
                                currentLocation?.let { location ->
                                    userLocation = location
                                    mapView?.controller?.setZoom(18.0)
                                    mapView?.controller?.animateTo(location)
                                }
                            }
                        } else {
                            // Request permission if not granted
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(48.dp),
                    containerColor = TaxiYellow,
                    contentColor = TaxiBlack
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "My Location"
                    )
                }
            }
        }
    }
}

@Composable
fun AddressMapPicker(
    label: String,
    address: String,
    onAddressChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    currentMapLocation: GeoPoint? = null,
    onMapLocationUpdate: ((GeoPoint) -> Unit)? = null,
    initialLocation: GeoPoint = GeoPoint(40.1872, 44.5152)
) {
    var showMap by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            label = { Text(label) },
            trailingIcon = {
                IconButton(onClick = { showMap = true }) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Pick location",
                        tint = TaxiYellow
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TaxiYellow,
                unfocusedBorderColor = TaxiGray,
                focusedLabelColor = TaxiYellow
            )
        )
    }
    
    // Full screen map dialog
    if (showMap) {
        FullScreenMapPicker(
            onLocationSelected = { selectedAddress ->
                onAddressChange(selectedAddress)
                showMap = false
            },
            onDismiss = { showMap = false },
            initialLocation = currentMapLocation ?: initialLocation,
            onMapLocationUpdate = onMapLocationUpdate
        )
    }
}

@Composable
fun FullScreenMapPicker(
    onLocationSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    initialLocation: GeoPoint = GeoPoint(40.1872, 44.5152),
    onMapLocationUpdate: ((GeoPoint) -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var selectedAddress by remember { mutableStateOf("") }
    var isLoadingAddress by remember { mutableStateOf(false) }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    val coroutineScope = rememberCoroutineScope()
    
    // Get user location when the picker opens
    LaunchedEffect(Unit) {
        userLocation = getCurrentLocation(context)
        // Update the current map location with user location when first obtained
        userLocation?.let { location ->
            onMapLocationUpdate?.invoke(location)
        }
    }
    
    // Function to get address from coordinates
    suspend fun getAddressFromCoordinates(geoPoint: GeoPoint): String {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(geoPoint.latitude, geoPoint.longitude, 1)
                addresses?.firstOrNull()?.getAddressLine(0) ?: "Անհայտ հասցե"
            } catch (e: Exception) {
                "Հասցեն հասանելի չէ"
            }
        }
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxiYellow)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TaxiBlack
                            )
                        }
                        
                        Text(
                            text = "Ընտրեք հասցեն",
                            style = MaterialTheme.typography.titleLarge,
                            color = TaxiBlack
                        )
                        
                        IconButton(
                            onClick = {
                                if (selectedAddress.isNotEmpty()) {
                                    onLocationSelected(selectedAddress)
                                }
                            },
                            enabled = selectedAddress.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Confirm",
                                tint = if (selectedAddress.isNotEmpty()) TaxiBlack else TaxiGray
                            )
                        }
                    }
                }
                
                // Selected address display
                if (selectedAddress.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = TaxiWhite)
                    ) {
                        Text(
                            text = selectedAddress,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TaxiBlack
                        )
                    }
                }
                
                // Map
                Box(modifier = Modifier.weight(1f)) {
                    OSMMapView(
                        modifier = Modifier.fillMaxSize(),
                        initialLocation = userLocation ?: initialLocation,
                        markers = selectedLocation?.let { listOf(it to selectedAddress) } ?: emptyList(),
                        onMapClick = { geoPoint ->
                            selectedLocation = geoPoint
                            isLoadingAddress = true
                            
                            // Update the current map location in ViewModel
                            onMapLocationUpdate?.invoke(geoPoint)
                            
                            // Get address asynchronously
                            coroutineScope.launch {
                                selectedAddress = getAddressFromCoordinates(geoPoint)
                                isLoadingAddress = false
                            }
                        },
                        showLocationButton = true,
                        centerOnUserLocation = userLocation != null
                    )
                    
                    // Loading indicator
                    if (isLoadingAddress) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = TaxiWhite)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = TaxiYellow
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Բեռնում է հասցեն...",
                                    color = TaxiBlack
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
