package com.example.taxi_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.ui.theme.*

@Composable
fun TaxiCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    isSmallScreen: Boolean = false
) {
    val titleSize = if (isSmallScreen) 12.sp else 14.sp
    val valueSize = if (isSmallScreen) 20.sp else 24.sp
    val padding = if (isSmallScreen) 12.dp else 16.dp
    
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(padding)
        ) {
            Text(
                text = title,
                fontSize = titleSize,
                color = TaxiGray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = value,
                fontSize = valueSize,
                fontWeight = FontWeight.Bold,
                color = TaxiBlack
            )
        }
    }
}

@Composable
fun TaxiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    placeholder: String = ""
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TaxiGray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            placeholder = { Text(placeholder) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TaxiYellow,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxiDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<Pair<String, String>>, // value to display text
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TaxiGray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = options.find { it.first == value }?.second ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TaxiYellow,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                )
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.second) },
                        onClick = {
                            onValueChange(option.first)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaxiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = TaxiBlack,
    textColor: Color = TaxiYellow,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.6f),
            disabledContentColor = textColor.copy(alpha = 0.6f)
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, displayText) = when (status) {
        "draft" -> Triple(StatusDraft.copy(alpha = 0.2f), StatusDraft, "Սևագիր")
        "published" -> Triple(StatusPublished.copy(alpha = 0.2f), StatusPublished, "Հրապարակված")
        "archived" -> Triple(StatusArchived.copy(alpha = 0.2f), StatusArchived, "Արխիվ")
        else -> Triple(StatusError.copy(alpha = 0.2f), StatusError, status)
    }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = displayText,
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TaxiWhite)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                color = TaxiGray,
                fontSize = 16.sp
            )
        }
    }
}
