package com.example.collegeportal

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.collegeportal.data.LoginRequest
import com.example.collegeportal.data.NetworkModule
import com.example.collegeportal.data.User
import com.example.collegeportal.ui.screens.AvatarSelectionScreen
import com.example.collegeportal.ui.screens.LoginScreen
import com.example.collegeportal.ui.screens.ProfileScreen
import com.example.collegeportal.ui.screens.SettingsScreen
import com.example.collegeportal.ui.theme.CollegePortalTheme
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            NetworkModule.initialize(this)
        } catch (e: Exception) {
            Toast.makeText(this, "Network initialization error", Toast.LENGTH_LONG).show()
        }
        setContent {
            CollegePortalTheme {
                AppNavigation(
                    onLogin = { token, onSuccess ->
                        lifecycleScope.launch {
                            try {
                                val response = NetworkModule.apiService.login(LoginRequest(token))
                                if (response.isSuccessful) {
                                    Toast.makeText(this@MainActivity, "Код верный", Toast.LENGTH_SHORT).show()
                                    response.body()?.let { onSuccess(it) }
                                } else {
                                    Toast.makeText(this@MainActivity, "Код неверный", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(this@MainActivity, "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onUploadAvatar = { token, uri, onSuccess ->
                        lifecycleScope.launch {
                            try {
                                val file = uriToFile(uri) ?: return@launch
                                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                val body = MultipartBody.Part.createFormData("avatar", file.name, requestFile)
                                val tokenBody = token.toRequestBody("text/plain".toMediaTypeOrNull())

                                val response = NetworkModule.apiService.updateAvatar(tokenBody, body)
                                if (response.isSuccessful) {
                                    Toast.makeText(this@MainActivity, "Аватар обновлен", Toast.LENGTH_SHORT).show()
                                    onSuccess(response.body()?.avatarUrl ?: "")
                                } else {
                                    Toast.makeText(this@MainActivity, "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(this@MainActivity, "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onRefreshProfile = { token, onSuccess ->
                        lifecycleScope.launch {
                            try {
                                val response = NetworkModule.apiService.getProfile(token)
                                if (response.isSuccessful) {
                                    response.body()?.let { onSuccess(it) }
                                }
                            } catch (e: Exception) {
                                // Silent fail for background refresh
                            }
                        }
                    }
                )
            }
        }
    }

    private fun uriToFile(uriString: String): File? {
        return try {
            val uri = android.net.Uri.parse(uriString)
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val file = File(cacheDir, "temp_avatar.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            null
        }
    }
}

@Composable
fun AppNavigation(
    onLogin: (String, (User) -> Unit) -> Unit,
    onUploadAvatar: (String, String, (String) -> Unit) -> Unit,
    onRefreshProfile: (String, (User) -> Unit) -> Unit
) {
    val navController = rememberNavController()
    var currentUser by remember { mutableStateOf<User?>(null) }
    var avatarUri by remember { mutableStateOf<String?>(null) }

    // Sync avatarUri with currentUser.avatarUrl whenever currentUser changes
    LaunchedEffect(currentUser) {
        currentUser?.avatarUrl?.let { avatarUri = it }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { token ->
                    onLogin(token) { user ->
                        currentUser = user
                        navController.navigate("profile")
                    }
                },
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("profile") {
            currentUser?.let { user ->
                ProfileScreen(
                    user = user,
                    onBack = { navController.popBackStack() },
                    avatarUri = avatarUri,
                    onAvatarClick = { navController.navigate("avatar_selection") },
                    onAvatarPicked = { uri -> 
                        onUploadAvatar(user.userToken, uri) { newUrl ->
                            avatarUri = newUrl
                        }
                    }
                )
            }
        }
        composable("avatar_selection") {
            currentUser?.let { user ->
                AvatarSelectionScreen(
                    user = user,
                    onBack = { navController.popBackStack() },
                    avatarUri = avatarUri,
                    onAvatarPicked = { uri -> 
                        onUploadAvatar(user.userToken, uri) { newUrl ->
                            avatarUri = newUrl
                            onRefreshProfile(user.userToken) { updatedUser ->
                                currentUser = updatedUser
                            }
                        }
                    }
                )
            }
        }
    }
}