package com.example.collegeportal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun AvatarSelectionScreen(
    user: User,
    onBack: () -> Unit,
    avatarUri: String?,
    onAvatarPicked: (String, () -> Unit) -> Unit,
    onPresetSelected: (Int, () -> Unit) -> Unit
) {
    val fullName = "${user.lastName} ${user.firstName} ${user.surname}"
    val qrBitmap = remember(fullName) {
        QrCodeGenerator.generate(fullName)
    }

    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { 
                isUploading = true
                onAvatarPicked(it.toString()) {
                    isUploading = false
                }
            }
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Профиль", color = Color.White) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Section: Avatar and Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = Color.LightGray
                    ) {
                        if (avatarUri != null) {
                            if (avatarUri.startsWith("preset:")) {
                                val index = avatarUri.substringAfter(":").toIntOrNull() ?: 0
                                val color = AvatarColors.getOrElse(index) { Color.LightGray }
                                Box(modifier = Modifier.fillMaxSize().background(color))
                            } else {
                                AsyncImage(
                                    model = avatarUri,
                                    contentDescription = "Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                    
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = Color.White
                        )
                    }
                }

                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = user.lastName, color = Color.White, fontSize = 16.sp)
                    Text(text = user.firstName, color = Color.White, fontSize = 16.sp)
                    Text(text = user.surname, color = Color.White, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DesignCardBlue)
            ) {
                Text("Изменить аватар", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ready Avatars Section
            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DesignCardBlue)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Готовые аватары",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        spacing = 8.dp
                    ) {
                        items(AvatarColors.size) { index ->
                            val color = AvatarColors[index]
                            Surface(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .clickable { 
                                        isUploading = true
                                        onPresetSelected(index) {
                                            isUploading = false
                                        }
                                    },
                                color = color
                            ) { }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // QR Code at the bottom
            qrBitmap?.let {
                Surface(
                    modifier = Modifier
                        .size(150.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White
                ) {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "User QR Code",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

// Extension for LazyVerticalGrid spacing as it doesn't have it natively in standard versions
@Composable
fun LazyVerticalGrid(columns: GridCells, spacing: androidx.compose.ui.unit.Dp, content: androidx.compose.foundation.lazy.grid.LazyGridScope.() -> Unit) {
    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
        columns = columns,
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalArrangement = Arrangement.spacedBy(spacing),
        content = content
    )
}
