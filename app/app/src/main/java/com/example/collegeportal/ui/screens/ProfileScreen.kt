package com.example.collegeportal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.collegeportal.data.User
import com.example.collegeportal.ui.theme.*
import com.example.collegeportal.util.QrCodeGenerator
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User,
    onBack: () -> Unit,
    avatarUri: String?,
    onAvatarClick: () -> Unit,
    onAvatarPicked: (String) -> Unit
) {
    val fullName = "${user.lastName} ${user.firstName} ${user.surname}"
    val qrBitmap = remember(fullName) {
        QrCodeGenerator.generate(fullName)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { onAvatarPicked(it.toString()) }
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Аккаунт", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = onAvatarClick) {
                        Text("изменить", color = Color.White)
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar with edit icon
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(150.dp),
                    shape = CircleShape,
                    border = BorderStroke(2.dp, Color.White),
                    color = Color.LightGray
                ) {
                    if (avatarUri != null) {
                        AsyncImage(
                            model = avatarUri,
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                IconButton(
                    onClick = {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.DarkGray, CircleShape)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Avatar", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Full Name instead of Date
            Text(
                text = fullName,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DesignCardBlue)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    DetailRow("группа", user.direction)
                    DetailRow("организация", user.educationalOrganization)
                    DetailRow("курс", user.course)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // QR Code at the bottom
            qrBitmap?.let {
                Surface(
                    modifier = Modifier
                        .size(180.dp)
                        .padding(bottom = 32.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White
                ) {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "User QR Code",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        Text(text = value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Divider(color = Color.White.copy(alpha = 0.3f), modifier = Modifier.padding(top = 4.dp))
    }
}
