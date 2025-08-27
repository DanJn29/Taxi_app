package com.example.taxi_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.ui.theme.TaxiBackground
import com.example.taxi_app.ui.theme.TaxiYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyLoginScreen(
    onLogin: (String, String) -> Unit,
    onBackToModeSelector: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

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
            isLargeScreen -> (screenWidth * 0.2f).coerceAtMost(80.dp)
            isSmallScreen -> 16.dp
            else -> 24.dp
        }
        
        val iconSize = when {
            isSmallScreen -> 60.dp
            isLargeScreen -> 100.dp
            else -> 80.dp
        }
        
        val titleSize = when {
            isSmallScreen -> 24.sp
            isLargeScreen -> 32.sp
            else -> 28.sp
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Yellow header with back button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = TaxiYellow,
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .statusBarsPadding()
                ) {
                    IconButton(
                        onClick = onBackToModeSelector,
                        modifier = Modifier.align(Alignment.CenterStart),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Վերադառնալ"
                        )
                    }
                }
            }

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding, vertical = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
        // Company icon
        Icon(
            imageVector = Icons.Default.Business,
            contentDescription = null,
            tint = TaxiYellow,
            modifier = Modifier.size(iconSize)
        )

        Spacer(modifier = Modifier.height(if (isSmallScreen) 16.dp else 24.dp))

        // Title
        Text(
            text = "Ընկերության մուտք",
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Մուտք գործեք ձեր ընկերության հաշիվ",
            fontSize = if (isSmallScreen) 14.sp else 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(if (isSmallScreen) 32.dp else 48.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Էլ. փոստ") },
            placeholder = { Text("admin@company.com") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = TaxiYellow
                )
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TaxiYellow,
                focusedLabelColor = TaxiYellow,
                cursorColor = TaxiYellow
            )
        )

        Spacer(modifier = Modifier.height(if (isSmallScreen) 12.dp else 16.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Գաղտնաբառ") },
            placeholder = { Text("Ձեր գաղտնաբառը") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = TaxiYellow
                )
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TaxiYellow,
                focusedLabelColor = TaxiYellow,
                cursorColor = TaxiYellow
            )
        )

        Spacer(modifier = Modifier.height(if (isSmallScreen) 24.dp else 32.dp))

        // Login button
        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    isLoading = true
                    onLogin(email, password)
                }
            },
            enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isSmallScreen) 48.dp else 56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TaxiYellow,
                contentColor = Color.Black
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Մուտք",
                    fontSize = if (isSmallScreen) 16.sp else 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(if (isSmallScreen) 16.dp else 24.dp))

        // Demo credentials info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.7f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Դեմո տվյալներ:",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Էլ. փոստ: admin@taxi.am",
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Գաղտնաբառ: կամայական",
                    color = Color.Gray
                )
            } // End Card's Column
        } // End Card
            } // End main content Column
        } // End outer Column
    } // End BoxWithConstraints
}
