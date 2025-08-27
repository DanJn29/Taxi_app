package com.example.taxi_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxi_app.data.Company
import com.example.taxi_app.data.Screen
import com.example.taxi_app.data.User
import com.example.taxi_app.ui.components.*
import com.example.taxi_app.ui.theme.*

@Composable
fun MembersScreen(
    company: Company,
    members: List<User>,
    navigationScrollState: LazyListState,
    onNavigate: (Screen) -> Unit,
    onAddMember: (String, String, String, String) -> Unit,
    onLogout: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("driver") }

    val roleOptions = listOf(
        "driver" to "Վարորդ",
        "dispatcher" to "Դիսպետչեր"
    )

    TaxiLayout(
        company = company,
        currentScreen = Screen.Members,
        navigationScrollState = navigationScrollState,
        onNavigate = onNavigate,
        onLogout = onLogout
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Վարորդներ / աշխատակիցներ",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxiBlack
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                // Add member form
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxiWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Ավելացնել աշխատակից",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TaxiBlack,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        TaxiTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Անուն Ազգանուն",
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        TaxiTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Էլ․ փոստ",
                            keyboardType = KeyboardType.Email,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        TaxiTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Գաղտնաբառ",
                            isPassword = true,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        TaxiDropdown(
                            value = role,
                            onValueChange = { role = it },
                            label = "Դեր",
                            options = roleOptions,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        TaxiButton(
                            text = "Պահպանել",
                            onClick = {
                                if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                                    onAddMember(name, email, password, role)
                                    // Clear form
                                    name = ""
                                    email = ""
                                    password = ""
                                    role = "driver"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Text(
                            text = "Վարորդը կստանա նամակ մուտք գործելու համար (email verify).",
                            fontSize = 12.sp,
                            color = TaxiGray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                
                // Members list
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Աշխատակիցների ցուցակ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TaxiBlack,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    if (members.isEmpty()) {
                        EmptyState(message = "Դատարկ է")
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            members.forEach { member ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = TaxiWhite)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = member.name,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = TaxiBlack
                                            )
                                            Text(
                                                text = "${member.email} · դեր՝ ${roleOptions.find { it.first == member.role }?.second ?: member.role}",
                                                fontSize = 14.sp,
                                                color = TaxiGray,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                }
            }
        }
    }
}
