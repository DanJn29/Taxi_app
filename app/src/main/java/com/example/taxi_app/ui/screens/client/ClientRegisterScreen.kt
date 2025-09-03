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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.ui.components.TaxiTextField
import com.example.taxi_app.ui.theme.*

@Composable
fun ClientRegisterScreen(
    onRegister: (String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onBackToLogin: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    successMessage: String? = null,
    onClearError: () -> Unit = {},
    onClearSuccess: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var acceptTerms by remember { mutableStateOf(false) }

    val isFormValid = name.isNotBlank() && 
                     email.isNotBlank() && 
                     password.isNotBlank() && 
                     password == confirmPassword && 
                     acceptTerms

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
            isLargeScreen -> (screenWidth * 0.15f).coerceAtMost(60.dp)
            isSmallScreen -> 16.dp
            else -> 24.dp
        }
        
        val logoSize = when {
            isSmallScreen -> 60.dp
            isLargeScreen -> 100.dp
            else -> 80.dp
        }
        
        val titleSize = when {
            isSmallScreen -> 20.sp
            isLargeScreen -> 28.sp
            else -> 24.sp
        }
        
        val buttonHeight = when {
            isSmallScreen -> 48.dp
            isLargeScreen -> 64.dp
            else -> 56.dp
        }
        
        val fieldSpacing = when {
            isSmallScreen -> 12.dp
            isLargeScreen -> 20.dp
            else -> 16.dp
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
                        onClick = onBackToLogin,
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding, vertical = if (isSmallScreen) 16.dp else 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Card(
                    modifier = Modifier
                        .size(logoSize)
                        .padding(bottom = if (isSmallScreen) 16.dp else 24.dp),
                    shape = RoundedCornerShape(logoSize / 2),
                    colors = CardDefaults.cardColors(containerColor = TaxiYellow)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Register",
                            modifier = Modifier.size(logoSize * 0.5f),
                            tint = TaxiBlack
                        )
                    }
                }
                
                Text(
                    text = "Գրանցում",
                    fontSize = titleSize,
                    fontWeight = FontWeight.Bold,
                    color = TaxiBlack,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Ստեղծիր նոր հաշիվ",
                    fontSize = if (isSmallScreen) 14.sp else 16.sp,
                    color = TaxiGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = if (isSmallScreen) 6.dp else 8.dp)
                )
                
                Spacer(modifier = Modifier.height(if (isSmallScreen) 16.dp else 24.dp))
                
                // Registration Form
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(if (isSmallScreen) 12.dp else 16.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxiWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(if (isSmallScreen) 16.dp else 24.dp)
                    ) {
                        TaxiTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Անուն Ազգանուն",
                            placeholder = "Մուտքագրիր անունդ",
                            modifier = Modifier.padding(bottom = fieldSpacing)
                        )
                        
                        TaxiTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Էլ․ փոստ",
                            keyboardType = KeyboardType.Email,
                            placeholder = "example@email.com",
                            modifier = Modifier.padding(bottom = fieldSpacing)
                        )
                        
                        // Error message display
                        errorMessage?.let { error ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = fieldSpacing),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Red.copy(alpha = 0.1f))
                            ) {
                                Text(
                                    text = error,
                                    color = androidx.compose.ui.graphics.Color.Red,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                        
                        // Success message display
                        successMessage?.let { success ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = fieldSpacing),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Green.copy(alpha = 0.1f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = androidx.compose.ui.graphics.Color.Green,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = success,
                                        color = androidx.compose.ui.graphics.Color.Green,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        
                        TaxiTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Գաղտնաբառ",
                            isPassword = true,
                            placeholder = "Նվազագույնը 6 նիշ",
                            modifier = Modifier.padding(bottom = fieldSpacing)
                        )
                
                        TaxiTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Հաստատիր գաղտնաբառը",
                            isPassword = true,
                            placeholder = "Կրկնիր գաղտնաբառը",
                            modifier = Modifier.padding(bottom = fieldSpacing)
                        )
                        
                        // Password match indicator
                        if (confirmPassword.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = fieldSpacing)
                            ) {
                                Icon(
                                    imageVector = if (password == confirmPassword) Icons.Default.CheckCircle else Icons.Default.Error,
                                    contentDescription = null,
                                    tint = if (password == confirmPassword) StatusPublished else StatusError,
                                    modifier = Modifier.size(if (isSmallScreen) 14.dp else 16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (password == confirmPassword) "Գաղտնաբառերը համընկնում են" else "Գաղտնաբառերը չեն համընկնում",
                                    fontSize = if (isSmallScreen) 11.sp else 12.sp,
                                    color = if (password == confirmPassword) StatusPublished else StatusError
                                )
                            }
                        }
                        
                        // Terms and conditions
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = if (isSmallScreen) 16.dp else 24.dp)
                        ) {
                            Checkbox(
                                checked = acceptTerms,
                                onCheckedChange = { acceptTerms = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = TaxiYellow,
                                    checkmarkColor = TaxiBlack
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ես համաձայն եմ ",
                                fontSize = if (isSmallScreen) 12.sp else 14.sp,
                                color = TaxiGray
                            )
                            TextButton(
                                onClick = { /* Show terms */ },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "պայմաններին",
                                    fontSize = if (isSmallScreen) 12.sp else 14.sp,
                                    color = TaxiBlack
                                )
                            }
                        }
                        
                        Button(
                            onClick = {
                                if (isFormValid) {
                                    onClearError() // Clear any previous errors
                                    onClearSuccess() // Clear any previous success messages
                                    onRegister(name, email, password)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(buttonHeight),
                            enabled = !isLoading && isFormValid,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TaxiYellow,
                                contentColor = TaxiBlack,
                                disabledContainerColor = TaxiYellow.copy(alpha = 0.6f),
                                disabledContentColor = TaxiBlack.copy(alpha = 0.6f)
                            )
                        ) {
                            if (isLoading) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = TaxiBlack,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Գրանցվում է...",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = if (isSmallScreen) 14.sp else 16.sp
                                    )
                                }
                            } else {
                                Text(
                                    text = "Գրանցվել",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = if (isSmallScreen) 14.sp else 16.sp
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(if (isSmallScreen) 16.dp else 24.dp))
                
                // Login link
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Արդեն ունե՞ս հաշիվ։ ",
                        color = TaxiGray,
                        fontSize = if (isSmallScreen) 12.sp else 14.sp
                    )
                    TextButton(onClick = onNavigateToLogin) {
                        Text(
                            text = "Մուտք գործել",
                            color = TaxiBlack,
                            fontSize = if (isSmallScreen) 12.sp else 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                // Extra bottom padding for keyboard accessibility
                Spacer(modifier = Modifier.height(if (isSmallScreen) 100.dp else 150.dp))
            } // End main content Column
        } // End outer Column
    } // End BoxWithConstraints
}