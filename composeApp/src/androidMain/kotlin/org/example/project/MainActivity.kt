package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.common.api.internal.GoogleServices.initialize
import com.google.firebase.FirebaseApp
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import org.example.project.ui.home.HomeScreen
import org.example.project.ui.home.LoginScreen
import org.example.project.ui.home.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize the official Firebase Android SDK
        FirebaseApp.initializeApp(this)
        // initialize GitLive Firebase for commonMain usage
        initialize(this)

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
                                // TODO: viewModel.signIn(email, pwd)
                            },
                            onNavigateToRegister = {
                                navController.navigate("register")
                            }
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            onNavigateToLogin  = {
                                navController.popBackStack("login", inclusive = false)
                            },
                            onRegisterSuccess  = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
