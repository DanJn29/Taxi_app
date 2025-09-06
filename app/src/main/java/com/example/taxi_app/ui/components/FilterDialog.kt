package com.example.taxi_app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.ui.theme.*
import com.example.taxi_app.viewmodel.TaxiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    viewModel: TaxiViewModel?,
    onDismiss: () -> Unit
) {
    val filterMinPrice = viewModel?.filterMinPrice?.collectAsState()?.value
    val filterMaxPrice = viewModel?.filterMaxPrice?.collectAsState()?.value
    val filterMinSeats = viewModel?.filterMinSeats?.collectAsState()?.value
    val filterPaymentMethods = viewModel?.filterPaymentMethods?.collectAsState()?.value ?: emptyList()
    
    var tempMinPrice by remember { mutableStateOf(filterMinPrice?.toString() ?: "") }
    var tempMaxPrice by remember { mutableStateOf(filterMaxPrice?.toString() ?: "") }
    var tempMinSeats by remember { mutableStateOf(filterMinSeats?.toString() ?: "") }
    var tempPaymentMethods by remember { mutableStateOf(filterPaymentMethods) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Ֆիլտրեր",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TaxiBlack
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Price Range
                Text(
                    text = "Գնային շրջակ (AMD)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = tempMinPrice,
                        onValueChange = { tempMinPrice = it },
                        label = { Text("Նվազագույնը") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TaxiBlue,
                            focusedLabelColor = TaxiBlue
                        )
                    )
                    
                    OutlinedTextField(
                        value = tempMaxPrice,
                        onValueChange = { tempMaxPrice = it },
                        label = { Text("Առավելագույնը") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TaxiBlue,
                            focusedLabelColor = TaxiBlue
                        )
                    )
                }
                
                // Minimum Seats
                Text(
                    text = "Նվազագույն տեղերի քանակ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                
                OutlinedTextField(
                    value = tempMinSeats,
                    onValueChange = { tempMinSeats = it },
                    label = { Text("Տեղերի քանակ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TaxiBlue,
                        focusedLabelColor = TaxiBlue
                    )
                )
                
                // Payment Methods
                Text(
                    text = "Վճարման եղանակ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxiBlack
                )
                
                val paymentOptions = listOf("cash" to "Կանխիկ", "card" to "Քարտ")
                
                paymentOptions.forEach { (value, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = tempPaymentMethods.contains(value),
                            onCheckedChange = { isChecked ->
                                tempPaymentMethods = if (isChecked) {
                                    tempPaymentMethods + value
                                } else {
                                    tempPaymentMethods - value
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = TaxiBlue
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            color = TaxiBlack
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Apply filters
                    val minPrice = tempMinPrice.toIntOrNull()
                    val maxPrice = tempMaxPrice.toIntOrNull()
                    val minSeats = tempMinSeats.toIntOrNull()
                    
                    viewModel?.updateFilterPriceRange(minPrice, maxPrice)
                    viewModel?.updateFilterMinSeats(minSeats)
                    viewModel?.updateFilterPaymentMethods(tempPaymentMethods)
                    
                    onDismiss()
                }
            ) {
                Text(
                    text = "Կիրառել",
                    color = TaxiBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    // Clear all filters
                    viewModel?.clearAllFilters()
                    onDismiss()
                }
            ) {
                Text(
                    text = "Մաքրել բոլորը",
                    color = TaxiGray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}
