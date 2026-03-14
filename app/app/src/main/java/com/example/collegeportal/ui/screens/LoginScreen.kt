package com.example.collegeportal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collegeportal.ui.theme.DesignBlue
import com.example.collegeportal.ui.theme.DesignCardBlue
import com.example.collegeportal.ui.theme.DesignWhite
import com.example.collegeportal.util.DateUtils

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit
) {
    var accessCode by remember { mutableStateOf("") }
    val dayOfWeek = remember { DateUtils.getCurrentDayOfWeek() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DesignWhite), // Outer background
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.9f)
                .background(DesignBlue, RoundedCornerShape(32.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Title
            Text(
                text = "Вход в аккаунт",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            // Logo Placeholder (Using a generic icon for now)
            Surface(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(60.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // This would be the "ПЭК ГГТУ" logo
                    Text(
                        text = "ПЭК ГГТУ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DesignBlue
                    )
                }
            }

            // Day of Week Button/Text
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DesignCardBlue)
            ) {
                Text(
                    text = "СЕГОДНЯ: $dayOfWeek",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Access Code Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Код доступа",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = accessCode,
                    onValueChange = { accessCode = it },
                    placeholder = { Text("Ввести код доступа") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { onLoginSuccess(accessCode) }) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Вход",
                                tint = DesignBlue
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFEFEFEF),
                        unfocusedContainerColor = Color(0xFFEFEFEF),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // QR Scanner Placeholder Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // QR Icon placeholder
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("QR", color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Logic for QR scanning */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.3f))
                ) {
                    Text("Tap to Scan QR Code", color = Color.White)
                }
            }
        }
    }
}
