package com.example.taxi_app.ui.components

import android.content.Context
import android.location.Geocoder
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
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.example.taxi_app.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun OSMMapView(
    modifier: Modifier = Modifier,
    initialLocation: GeoPoint = GeoPoint(40.1872, 44.5152), // Yerevan, Armenia
    markers: List<Pair<GeoPoint, String>> = emptyList(),
    onMapClick: ((GeoPoint) -> Unit)? = null,
    showLocationButton: Boolean = true
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    
    // Initialize OSMDroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
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
                        mapController.setZoom(12.0)
                        mapController.setCenter(initialLocation)
                        
                        // Add markers
                        markers.forEach { (point, title) ->
                            val marker = Marker(this).apply {
                                position = point
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                this.title = title
                            }
                            overlays.add(marker)
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
                markers.forEach { (point, title) ->
                    val marker = Marker(view).apply {
                        position = point
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        this.title = title
                    }
                    view.overlays.add(marker)
                }
                view.invalidate()
            }
            
            // Location button
            if (showLocationButton) {
                FloatingActionButton(
                    onClick = {
                        mapView?.controller?.animateTo(initialLocation)
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
            initialLocation = initialLocation
        )
    }
}

@Composable
fun FullScreenMapPicker(
    onLocationSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    initialLocation: GeoPoint = GeoPoint(40.1872, 44.5152)
) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var selectedAddress by remember { mutableStateOf("") }
    var isLoadingAddress by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
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
                        initialLocation = initialLocation,
                        markers = selectedLocation?.let { listOf(it to selectedAddress) } ?: emptyList(),
                        onMapClick = { geoPoint ->
                            selectedLocation = geoPoint
                            isLoadingAddress = true
                            
                            // Get address asynchronously
                            coroutineScope.launch {
                                selectedAddress = getAddressFromCoordinates(geoPoint)
                                isLoadingAddress = false
                            }
                        },
                        showLocationButton = true
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
