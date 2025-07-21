package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.google.firebase.FirebaseApp
import org.example.project.ui.home.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "home") {

                    composable("home") {
                        HomeScreen(
                            onGetStarted = { navController.navigate("register") },
                            onLogIn       = { navController.navigate("login") }
                        )
                    }

                    composable("login") {
                        LoginScreen(
                            onLogin = { email, pwd ->
                                // TODO: call your ViewModel / auth logic
                            },
                            onNavigateToRegister = {
                                navController.navigate("register")
                            }
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            onRegister = { email, pwd ->
                                // TODO: register then nav back or to main content
                            },
                            onNavigateToLogin = {
                                navController.popBackStack("login", inclusive = false)
                            }
                        )
                    }
                }
            }
        }
    }
}
