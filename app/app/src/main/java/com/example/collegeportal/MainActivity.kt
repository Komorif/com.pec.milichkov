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
import com.example.collegeportal.ui.theme.CollegePortalTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                    }
                )
            }
        }
    }
}

@Composable
fun AppNavigation(
    onLogin: (String, (User) -> Unit) -> Unit
) {
    val navController = rememberNavController()
    var currentUser by remember { mutableStateOf<User?>(null) }
    var avatarUri by remember { mutableStateOf<String?>(null) }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { token ->
                    onLogin(token) { user ->
                        currentUser = user
                        navController.navigate("profile")
                    }
                }
            )
        }
        composable("profile") {
            currentUser?.let { user ->
                ProfileScreen(
                    user = user,
                    onBack = { navController.popBackStack() },
                    avatarUri = avatarUri,
                    onAvatarClick = { navController.navigate("avatar_selection") },
                    onAvatarPicked = { uri -> avatarUri = uri }
                )
            }
        }
        composable("avatar_selection") {
            currentUser?.let { user ->
                AvatarSelectionScreen(
                    user = user,
                    onBack = { navController.popBackStack() },
                    avatarUri = avatarUri,
                    onAvatarPicked = { uri -> avatarUri = uri }
                )
            }
        }
    }
}