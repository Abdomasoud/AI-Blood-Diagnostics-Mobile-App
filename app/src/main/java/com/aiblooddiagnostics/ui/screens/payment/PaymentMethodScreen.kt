package com.aiblooddiagnostics.ui.screens.payment

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aiblooddiagnostics.ui.theme.Blue60

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(navController: NavController) {
    var selectedPaymentMethod by remember { mutableStateOf("card") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Blue60
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Payment Method",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Credit & Debit Card Section
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Credit & Debit Card",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentMethodOption(
                title = "Add New Card",
                icon = Icons.Default.CreditCard,
                isSelected = selectedPaymentMethod == "card",
                onClick = { 
                    selectedPaymentMethod = "card"
                    navController.navigate("add_card")
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "More Payment Option",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentMethodOption(
                title = "Apple Play",
                icon = Icons.Default.PhoneIphone,
                isSelected = selectedPaymentMethod == "apple",
                onClick = { selectedPaymentMethod = "apple" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            PaymentMethodOption(
                title = "Paypal",
                icon = Icons.Default.Payment,
                isSelected = selectedPaymentMethod == "paypal",
                onClick = { selectedPaymentMethod = "paypal" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            PaymentMethodOption(
                title = "Google Play",
                icon = Icons.Default.ShoppingCart,
                isSelected = selectedPaymentMethod == "google",
                onClick = { selectedPaymentMethod = "google" }
            )
        }
    }
}

@Composable
fun PaymentMethodOption(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Blue60.copy(alpha = 0.1f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = Blue60,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                color = Blue60,
                modifier = Modifier.weight(1f)
            )

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Blue60
                )
            )
        }
    }
}