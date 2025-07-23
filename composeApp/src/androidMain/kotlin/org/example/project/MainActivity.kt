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
import org.example.project.ui.feed.FeedScreen
import org.example.project.ui.home.AndroidUserViewModel



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            MaterialTheme {
                val vm: AndroidUserViewModel = viewModel()
                val currentUid by vm.currentUid.collectAsState()
                val startDestination = if (currentUid != null) "feed" else "home"

                val navController = rememberNavController()

                NavHost(navController, startDestination = startDestination) {

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
                        val currentUid   by vm.currentUid.collectAsState()
                        val isLoading    by vm.isLoading.collectAsState()
                        val errorMessage by vm.errorMessage.collectAsState()

                        LoginScreen(
                            isLoading           = isLoading,
                            errorMessage        = errorMessage,
                            onLogin = { email, pwd ->
                                vm.signIn(email, pwd)
                            },
                            onNavigateToRegister = {
                                navController.navigate("register")
                            }
                        )
                        LaunchedEffect(currentUid) {
                            if (currentUid != null) {
                                navController.navigate("feed") {
                                    popUpTo("home") { inclusive = true }
                                }
                                }
                        }

                    }

                    // 3) register
                    composable("register") {
                        val vm: AndroidUserViewModel = viewModel()
                        val currentUid   by vm.currentUid.collectAsState()
                        val isLoading    by vm.isLoading.collectAsState()
                        val errorMessage by vm.errorMessage.collectAsState()

                        RegisterScreen(
                            isLoading = isLoading,
                            errorMessage = errorMessage,
                            onRegister = { email, pwd ->
                                vm.signUp(email, pwd)
                            },
                            onNavigateToLogin = {
                                navController.popBackStack("login", inclusive = false)
                            }
                        )
                        LaunchedEffect(currentUid) {
                            if (currentUid != null) {
                                navController.navigate("feed") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        }
                    }
                    // 4) feed
                    composable("feed") {
                        val vm: AndroidUserViewModel = viewModel()
                        FeedScreen(
                            onSignOut = {
                                vm.signOut()
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
