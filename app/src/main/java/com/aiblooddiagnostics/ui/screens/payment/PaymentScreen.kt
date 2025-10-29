package com.aiblooddiagnostics.ui.screens.payment

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.aiblooddiagnostics.ui.theme.Blue60
import com.aiblooddiagnostics.ui.viewmodel.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    appointmentId: String,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blue60)
    ) {
        // Header
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
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Payment",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Amount
        Text(
            text = "100.00 EGP",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Content Card
        Card(
            modifier = Modifier
                .fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Doctor Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = "https://via.placeholder.com/60",
                        contentDescription = "Doctor Avatar",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dr. Amira Mohamed, M.D.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Hematologist",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "★ 5",
                                fontSize = 12.sp,
                                color = Color(0xFFFFA000),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "(60)",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Blue60,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Divider(color = Color.LightGray)

                Spacer(modifier = Modifier.height(16.dp))

                // Appointment Details
                AppointmentDetailRow("Date / Hour", "Month 24, Year / 10:00 AM")
                AppointmentDetailRow("Duration", "30 Minutes")
                AppointmentDetailRow("Booking for", "Another Person")

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = Color.LightGray)

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Details
                AppointmentDetailRow("Amount", "100.00 EGP")
                AppointmentDetailRow("Duration", "30 Minutes")

                Spacer(modifier = Modifier.height(8.dp))

                AppointmentDetailRow("Total", "100 EGP", isTotal = true)

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = Color.LightGray)

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Method
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Payment Method",
                        fontSize = 14.sp,
                        color = Blue60
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Card",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = { navController.navigate("payment_method") }) {
                            Text(
                                text = "Change",
                                fontSize = 14.sp,
                                color = Blue60
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Pay Now Button
                Button(
                    onClick = {
                        viewModel.processPayment(appointmentId) { success ->
                            if (success) {
                                navController.navigate("success/payment") {
                                    popUpTo("dashboard") { inclusive = false }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue60),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "Pay Now",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun AppointmentDetailRow(
    label: String,
    value: String,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = if (isTotal) Color.Black else Blue60,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
    }
}