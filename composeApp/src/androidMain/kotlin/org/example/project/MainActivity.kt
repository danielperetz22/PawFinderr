package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import dev.gitlive.firebase.Firebase
import org.example.project.ui.bottomBar.AppTopBar
import org.example.project.ui.bottomBar.BottomBar
import org.example.project.ui.home.HomeScreen
import org.example.project.ui.home.LoginScreen
import org.example.project.ui.home.RegisterScreen
import org.example.project.ui.home.AndroidUserViewModel
import org.example.project.ui.feed.FeedScreen
import org.example.project.ui.report.NewReportScreen

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

                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        val backStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = backStackEntry?.destination?.route
                        val titleText = currentRoute
                            ?.replaceFirstChar { it.uppercaseChar() }
                            ?: ""
                        if (currentRoute in listOf("feed", "profile", "reports")) {
                            AppTopBar(
                                title= titleText,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    },
                    bottomBar = {
                        val backStackEntry by navController.currentBackStackEntryAsState()
                        val route = backStackEntry?.destination?.route
                        if (route in listOf("feed", "profile", "reports")) {
                            BottomBar(navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        Modifier.consumeWindowInsets(innerPadding)
                    ) {
                        // 1) home
                        composable("home") {
                            HomeScreen(
                                onGetStarted = { navController.navigate("register") },
                                onLogIn      = { navController.navigate("login") }
                            )
                        }

                        // 2) login
                        composable("login") {
                            val vmLogin: AndroidUserViewModel = viewModel()
                            val isLoading    by vmLogin.isLoading.collectAsState()
                            val errorMessage by vmLogin.errorMessage.collectAsState()
                            val uid          by vmLogin.currentUid.collectAsState()

                            LoginScreen(
                                isLoading           = isLoading,
                                errorMessage        = errorMessage,
                                onLogin             = { email, pwd -> vmLogin.signIn(email, pwd) },
                                onNavigateToRegister= { navController.navigate("register") }
                            )

                            LaunchedEffect(uid) {
                                if (uid != null) {
                                    navController.navigate("feed") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            }
                        }

                        // 3) register
                        composable("register") {
                            val vmReg: AndroidUserViewModel = viewModel()
                            val isLoading    by vmReg.isLoading.collectAsState()
                            val errorMessage by vmReg.errorMessage.collectAsState()
                            val uid          by vmReg.currentUid.collectAsState()

                            RegisterScreen(
                                isLoading           = isLoading,
                                errorMessage        = errorMessage,
                                onRegister          = { email, pwd -> vmReg.signUp(email, pwd) },
                                onNavigateToLogin   = { navController.navigate("login") }
                            )

                            LaunchedEffect(uid) {
                                if (uid != null) {
                                    navController.navigate("feed") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            }
                        }

                        // 4) feed
                        composable("feed") {
                            val vmFeed: AndroidUserViewModel = viewModel()
                            FeedScreen(
                                onSignOut = {
                                    vmFeed.signOut()
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 5) profile
                        composable("profile") {
                            // ProfileScreen(...)
                        }

                        // 6) reports
                        composable("reports") {
                            NewReportScreen()
                        }
                    }
                }
            }
        }
    }
}
