package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import dev.gitlive.firebase.Firebase
import org.example.project.ui.home.HomeScreen
import org.example.project.ui.home.LoginScreen
import org.example.project.ui.home.RegisterScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.project.ui.home.AndroidUserViewModel



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(navController, startDestination = "home") {

                    // 1) home
                    composable("home") {
                        HomeScreen(
                            onGetStarted = { navController.navigate("register") },
                            onLogIn = { navController.navigate("login") }
                        )
                    }

                    // 2) login
                    composable("login") {
                        val vm: AndroidUserViewModel = viewModel()

                        LoginScreen(
                            onLogin = { email, pwd ->
                                vm.signIn(email, pwd)
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate("register")
                            }
                        )
                    }

                    // 3) register
                    composable("register") {
                        val vm: AndroidUserViewModel = viewModel()

                        RegisterScreen(
                            onRegister = { email, pwd ->
                                vm.signUp(email, pwd)
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
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
