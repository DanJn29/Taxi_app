package com.example.taxi_app.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientLoginScreen(
    onLogin: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onBackToModeSelector: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxiBackground)
    ) {
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
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Վերադառնալ",
                            tint = TaxiBlack
                        )
                    }
                }
            }

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            // Logo
            Card(
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(40.dp),
                colors = CardDefaults.cardColors(containerColor = TaxiYellow)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Client",
                        modifier = Modifier.size(40.dp),
                        tint = TaxiBlack
                    )
                }
            }
            
            Text(
                text = "Ուղևորի մուտք",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TaxiBlack,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Էլ. փոստ") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = TaxiYellow
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TaxiYellow,
                    unfocusedBorderColor = TaxiGray,
                    focusedLabelColor = TaxiYellow
                )
            )
            
            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Գաղտնաբառ") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = TaxiYellow
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TaxiYellow,
                    unfocusedBorderColor = TaxiGray,
                    focusedLabelColor = TaxiYellow
                )
            )
            
            // Login button
            Button(
                onClick = { onLogin(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow)
            ) {
                Text(
                    text = "Մուտք",
                    color = TaxiBlack,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Register link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Չունե՞ս հաշիվ։ ",
                    color = TaxiGray,
                    fontSize = 14.sp
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Գրանցվել",
                        color = TaxiBlack,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Alternative login methods
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Կամ մուտք գործիր",
                    fontSize = 14.sp,
                    color = TaxiGray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* Google login */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Google", color = TaxiBlack)
                    }
                    
                    OutlinedButton(
                        onClick = { /* Facebook login */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Facebook", color = TaxiBlack)
                    }
                }
            }
            } // End main content Column
        } // End outer Column
    } // End Box
}
