package com.example.taxi_app.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import com.example.taxi_app.ui.components.TaxiTextField
import com.example.taxi_app.ui.theme.*
import com.example.taxi_app.viewmodel.TaxiViewModel

@Composable
fun CompanyRegisterScreen(
    viewModel: TaxiViewModel,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit = {}
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var acceptTerms by remember { mutableStateOf(false) }

    val isFormValid = name.isNotBlank() && 
                     email.isNotBlank() && 
                     password.isNotBlank() && 
                     password == confirmPassword && 
                     companyName.isNotBlank() &&
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
                        onClick = onBack,
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
                // Company icon
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = TaxiYellow,
                    modifier = Modifier.size(logoSize)
                )

                Spacer(modifier = Modifier.height(if (isSmallScreen) 12.dp else 16.dp))

                // Title
                Text(
                    text = "Ընկերության գրանցում",
                    fontSize = titleSize,
                    fontWeight = FontWeight.Bold,
                    color = TaxiBlack,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Ստեղծիր ընկերության հաշիվ",
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
                        
                        TaxiTextField(
                            value = companyName,
                            onValueChange = { companyName = it },
                            label = "Ընկերության անվանում",
                            placeholder = "Մուտքագրիր ընկերության անվանումը",
                            modifier = Modifier.padding(bottom = fieldSpacing)
                        )
                        
                        TaxiTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Գաղտնաբառ",
                            placeholder = "Մուտքագրիր գաղտնաբառ",
                            isPassword = true,
                            modifier = Modifier.padding(bottom = fieldSpacing)
                        )
                        
                        TaxiTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Կրկնել գաղտնաբառը",
                            placeholder = "Կրկնիր գաղտնաբառը",
                            isPassword = true,
                            modifier = Modifier.padding(bottom = fieldSpacing)
                        )
                        
                        // Password mismatch warning
                        if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = fieldSpacing),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Red.copy(alpha = 0.1f))
                            ) {
                                Text(
                                    text = "Գաղտնաբառերը չեն համընկնում",
                                    color = androidx.compose.ui.graphics.Color.Red,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                        
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
                                Text(
                                    text = success,
                                    color = androidx.compose.ui.graphics.Color.Green,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                        
                        // Terms and conditions checkbox
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = fieldSpacing),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = acceptTerms,
                                onCheckedChange = { acceptTerms = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = TaxiYellow,
                                    uncheckedColor = TaxiGray
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ես համաձայն եմ օգտագործման պայմանների հետ",
                                fontSize = if (isSmallScreen) 12.sp else 14.sp,
                                color = TaxiBlack
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(if (isSmallScreen) 16.dp else 24.dp))
                        
                        // Register button
                        Button(
                            onClick = {
                                if (isFormValid) {
                                    viewModel.registerCompany(name, email, password, confirmPassword, companyName)
                                }
                            },
                            enabled = isFormValid && !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(buttonHeight),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TaxiYellow,
                                contentColor = TaxiBlack
                            ),
                            shape = RoundedCornerShape(if (isSmallScreen) 8.dp else 12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = TaxiBlack,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Գրանցվել",
                                    fontSize = if (isSmallScreen) 14.sp else 16.sp,
                                    fontWeight = FontWeight.Medium
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
                
                Spacer(modifier = Modifier.height(if (isSmallScreen) 16.dp else 24.dp))
            }
        }
    }
}
