package com.example.taxi_app.ui.screens.driver

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.data.User
import com.example.taxi_app.ui.components.*
import com.example.taxi_app.ui.theme.*
import com.example.taxi_app.viewmodel.TaxiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverVehicleSetupScreen(
    user: User,
    viewModel: TaxiViewModel,
    onVehicleRegistered: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // Form state
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var seats by remember { mutableStateOf("4") }
    var color by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // UI state
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }
    
    // Car brands list
    val carBrands = listOf(
        "Toyota", "Honda", "Nissan", "Mazda", "Hyundai", "Kia", 
        "BMW", "Mercedes-Benz", "Audi", "Volkswagen", "Ford", "Chevrolet",
        "Peugeot", "Renault", "Opel", "Skoda", "LADA", "UAZ", "GAZ"
    )
    
    // Colors list
    val carColors = listOf(
        "Սեև" to "black",
        "Սպիտակ" to "white", 
        "Մոխրագույն" to "gray",
        "Կապույտ" to "blue",
        "Կարմիր" to "red",
        "Կանաչ" to "green",
        "Դեղին" to "yellow",
        "Արծաթագույն" to "silver"
    )
    
    var showBrandDropdown by remember { mutableStateOf(false) }
    var showColorDropdown by remember { mutableStateOf(false) }
    
    LaunchedEffect(successMessage) {
        if (successMessage == "Ավտոմեքենան հաջողությամբ գրանցվեց") {
            onVehicleRegistered()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxiLightGray)
            .statusBarsPadding() // Add safe area padding for status bar
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TaxiYellow),
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp) // Reduced padding for better fit
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp) // Slightly smaller for better fit
                            .clip(RoundedCornerShape(10.dp))
                            .background(TaxiBlack.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Վերադառնալ",
                            tint = TaxiBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp) // Use weight for flexible sizing
                    ) {
                        Text(
                            text = "Ավտոմեքենայի գրանցում",
                            fontSize = 18.sp, // Slightly smaller font
                            fontWeight = FontWeight.Bold,
                            color = TaxiBlack
                        )
                        Text(
                            text = "Ավելացրեք ձեր ավտոմեքենայի տվյալները",
                            fontSize = 13.sp, // Slightly smaller font
                            color = TaxiBlack.copy(alpha = 0.7f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.size(40.dp)) // Match back button size
                }
            }
        }

        // Form content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp), // More responsive padding
            verticalArrangement = Arrangement.spacedBy(12.dp) // Slightly reduced spacing
        ) {
            // Car photo section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = TaxiWhite)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ավտոմեքենայի նկար",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TaxiBlack
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(160.dp) // Smaller size for better fit on small screens
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                2.dp,
                                if (selectedImageUri != null) TaxiBlue else TaxiGray.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                            .background(TaxiLightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            // Show selected image placeholder
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Նկարը ընտրված է",
                                    modifier = Modifier.size(40.dp), // Smaller icon
                                    tint = TaxiBlue
                                )
                                Text(
                                    text = "Նկարը ընտրված է",
                                    fontSize = 12.sp, // Smaller text
                                    color = TaxiBlue
                                )
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp), // Smaller icon
                                    tint = TaxiGray
                                )
                                Text(
                                    text = "Ավելացնել նկար",
                                    fontSize = 12.sp, // Smaller text
                                    color = TaxiGray
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TaxiBlue
                        ),
                        border = BorderStroke(1.dp, TaxiBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedImageUri != null) "Փոխել նկարը" else "Ընտրել նկար",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // Brand selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = TaxiWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Մակնիշ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TaxiBlack
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ExposedDropdownMenuBox(
                        expanded = showBrandDropdown,
                        onExpandedChange = { showBrandDropdown = it }
                    ) {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = { 
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showBrandDropdown) 
                            },
                            placeholder = { Text("Ընտրեք մակնիշը") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TaxiBlue,
                                unfocusedBorderColor = TaxiGray.copy(alpha = 0.5f)
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = showBrandDropdown,
                            onDismissRequest = { showBrandDropdown = false }
                        ) {
                            carBrands.forEach { brandOption ->
                                DropdownMenuItem(
                                    text = { Text(brandOption) },
                                    onClick = {
                                        brand = brandOption
                                        showBrandDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Model input
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = TaxiWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Մոդել",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TaxiBlack
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        placeholder = { Text("Մուտքագրեք մոդելը") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TaxiBlue,
                            unfocusedBorderColor = TaxiGray.copy(alpha = 0.5f)
                        )
                    )
                }
            }
            
            // Seats and Color row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Seats input
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = TaxiWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Տեղերի քանակ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TaxiBlack
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = seats,
                            onValueChange = { 
                                if (it.all { char -> char.isDigit() } && it.length <= 1) {
                                    seats = it
                                }
                            },
                            placeholder = { Text("4") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TaxiBlue,
                                unfocusedBorderColor = TaxiGray.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
                
                // Color selection
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = TaxiWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Գույն",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TaxiBlack
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        ExposedDropdownMenuBox(
                            expanded = showColorDropdown,
                            onExpandedChange = { showColorDropdown = it }
                        ) {
                            OutlinedTextField(
                                value = carColors.find { it.second == color }?.first ?: "",
                                onValueChange = { },
                                readOnly = true,
                                trailingIcon = { 
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showColorDropdown) 
                                },
                                placeholder = { Text("Ընտրեք գույնը") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TaxiBlue,
                                    unfocusedBorderColor = TaxiGray.copy(alpha = 0.5f)
                                )
                            )
                            
                            ExposedDropdownMenu(
                                expanded = showColorDropdown,
                                onDismissRequest = { showColorDropdown = false }
                            ) {
                                carColors.forEach { (armenianName, englishName) ->
                                    DropdownMenuItem(
                                        text = { Text(armenianName) },
                                        onClick = {
                                            color = englishName
                                            showColorDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // License plate input
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = TaxiWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Պետական համարանիշ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TaxiBlack
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = plate,
                        onValueChange = { plate = it.uppercase() },
                        placeholder = { Text("Օրինակ: 34 AA 123") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TaxiBlue,
                            unfocusedBorderColor = TaxiGray.copy(alpha = 0.5f)
                        )
                    )
                }
            }
            
            // Error message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
            
            // Success message
            successMessage?.let { success ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = success,
                        modifier = Modifier.padding(16.dp),
                        color = Color.Green,
                        fontSize = 14.sp
                    )
                }
            }
            
            // Register button
            Button(
                onClick = {
                    if (brand.isNotEmpty() && model.isNotEmpty() && seats.isNotEmpty() && 
                        color.isNotEmpty() && plate.isNotEmpty()) {
                        viewModel.registerVehicle(
                            brand = brand,
                            model = model,
                            seats = seats.toIntOrNull() ?: 4,
                            color = color,
                            plate = plate,
                            photoUri = selectedImageUri,
                            context = context
                        )
                    } else {
                        // Show validation error
                        viewModel.setErrorMessage("Խնդրում ենք լրացնել բոլոր դաշտերը")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TaxiBlue),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && brand.isNotEmpty() && model.isNotEmpty() && 
                         seats.isNotEmpty() && color.isNotEmpty() && plate.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = TaxiWhite,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Գրանցել ավտոմեքենան",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TaxiWhite
                    )
                }
            }
            
            // Add bottom padding for navigation bar
            Spacer(modifier = Modifier
                .height(20.dp)
                .navigationBarsPadding() // Add safe area padding for navigation bar
            )
        }
    }
}
