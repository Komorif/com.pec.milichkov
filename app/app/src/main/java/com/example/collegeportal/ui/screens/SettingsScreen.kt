package com.example.collegeportal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collegeportal.data.NetworkModule
import com.example.collegeportal.ui.theme.DesignBlue
import com.example.collegeportal.ui.theme.DesignCardBlue
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var ipAddress by remember { mutableStateOf(NetworkModule.getCurrentIp(context)) }
    var port by remember { mutableStateOf(NetworkModule.getCurrentPort(context)) }
    var connectionStatus by remember { mutableStateOf<Boolean?>(null) } // null = none, true = success, false = fail
    var isTesting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Настройки", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = DesignBlue)
            )
        },
        containerColor = DesignBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DesignCardBlue)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "IP Адрес Сервера",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    TextField(
                        value = ipAddress,
                        onValueChange = { 
                            ipAddress = it
                            connectionStatus = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = { Text("Например: 10.0.2.2", color = Color.White.copy(alpha = 0.4f)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Порт",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    TextField(
                        value = port,
                        onValueChange = { 
                            port = it
                            connectionStatus = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = { Text("Например: 8000", color = Color.White.copy(alpha = 0.4f)) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    isTesting = true
                                    connectionStatus = testConnection(ipAddress, port)
                                    isTesting = false
                                }
                            },
                            enabled = !isTesting,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
                        ) {
                            if (isTesting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(text = "Тест", color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        val statusColor = when(connectionStatus) {
                            true -> Color.Green
                            false -> Color.Red
                            else -> Color.Transparent
                        }

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(statusColor, RoundedCornerShape(4.dp))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    NetworkModule.saveSettings(context, ipAddress, port)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.3f))
            ) {
                Text("Сохранить", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

suspend fun testConnection(ip: String, portStr: String): Boolean {
    return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            var finalIp = ip.trim()
            if (finalIp == "127.0.0.1" || finalIp == "localhost") {
                finalIp = "10.0.2.2"
            }
            val port = portStr.toIntOrNull() ?: 8000
            val socket = Socket()
            socket.connect(InetSocketAddress(finalIp, port), 2000)
            socket.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}
