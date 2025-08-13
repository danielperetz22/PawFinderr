package org.example.project

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import org.example.project.ui.ProfileScreen
import org.example.project.ui.bottomBar.AppTopBar
import org.example.project.ui.bottomBar.BottomBar
import org.example.project.ui.home.HomeScreen
import org.example.project.ui.home.LoginScreen
import org.example.project.ui.home.RegisterScreen
import org.example.project.ui.home.AndroidUserViewModel
import org.example.project.ui.feed.FeedScreen
import org.example.project.ui.report.NewReportScreen
import org.example.project.data.report.ReportViewModel
import androidx.compose.runtime.remember
import org.example.project.data.report.ReportModel
import org.example.project.data.report.ReportUiState
import org.example.project.ui.report.MyReportsScreen
import org.example.project.ui.report.ReportDetailsScreen
import java.net.URLDecoder


@Suppress("NAME_SHADOWING")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            MaterialTheme {
                val vm: AndroidUserViewModel = viewModel()
                val currentUid by vm.currentUid.collectAsState()
                val startDestination = if (currentUid != null) "feed" else "home"

                val barRoutes = setOf("feed", "profile", "reports")
                fun shouldShowBars(route: String?): Boolean =
                    route in barRoutes ||
                            route?.startsWith("new-report") == true ||
                            route?.startsWith("report-details") == true

                fun titleFor(route: String?): String = when {
                    route == "feed"                 -> "Feed"
                    route == "profile"              -> "Profile"
                    route == "reports"              -> "My Reports"
                    route?.startsWith("new-report") == true -> "New Report"
                    route?.startsWith("report-details") == true -> "Report Details"
                    else -> route.orEmpty()
                        .replaceFirstChar { it.uppercaseChar() }
                        .replace('-', ' ')
                }

                val navController = rememberNavController()

                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        val backStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = backStackEntry?.destination?.route
                        val titleText = currentRoute
                            ?.replaceFirstChar { it.uppercaseChar() }
                            ?: ""
                        if (shouldShowBars(currentRoute)) {
//                            val titleText = titleMap[currentRoute] ?: currentRoute.orEmpty()
//                                .replaceFirstChar { it.uppercaseChar() }
//                                .replace('-', ' ')
                            AppTopBar(
                                title = titleFor(currentRoute),
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    },
                    bottomBar = {
                        val backStackEntry by navController.currentBackStackEntryAsState()
                        val route = backStackEntry?.destination?.route
                        if (shouldShowBars(route)) {
                            BottomBar(navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
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
                            val reportVm = remember { ReportViewModel() }
                            val uiState by reportVm.uiState.collectAsState()
                            LaunchedEffect(Unit) { reportVm.loadAllReports() }
                            val reports = when (uiState) {
                                is ReportUiState.ReportsLoaded -> (uiState as ReportUiState.ReportsLoaded).reports
                                else -> emptyList()
                            }
                            val vmFeed: AndroidUserViewModel = viewModel()
                            FeedScreen(
                                reports = reports,
                                onReportClicked = { rpt ->
                                    val json = kotlinx.serialization.json.Json.encodeToString(rpt)
                                    val encoded = java.net.URLEncoder.encode(json, Charsets.UTF_8.name())
                                    navController.navigate("report-details/$encoded")
                                },
                                onPublishClicked = { navController.navigate("new-report") },
                            )
                        }

                        // 5) profile
                        composable("profile") {
                            val vmProfile: AndroidUserViewModel = viewModel()
                            val isLoading by vmProfile.isLoading.collectAsState()
                            val errorMessage by vmProfile.errorMessage.collectAsState()
                            val uid by vmProfile.currentUid.collectAsState()
                            ProfileScreen(
                                isLoading           = isLoading,
                                errorMessage        = errorMessage,
                                onSignOut = {
                                    vmProfile.signOut()
                                }
                            )
                            LaunchedEffect(uid) {
                                if (uid == null && !isLoading) {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            }
                        }


                        // 6) reports
                        composable("new-report") {
                            val reportVm = remember { ReportViewModel() }
                            val uiState by reportVm.uiState.collectAsState()

                            val pickedLocationFlow = navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.getStateFlow("picked_location", null as Pair<Double, Double>?)
                            val pickedLocation by (pickedLocationFlow?.collectAsState() ?: remember { mutableStateOf(null) })


                            // Your screen
                            NewReportScreen(
                                pickedLocation = pickedLocation,
                                onImagePicked = { /* ... */ },
                            ) { description, name, phone, isLost, imageUrl, lat, lng ->
                                reportVm.saveReport(
                                    description = description,
                                    name        = name,
                                    phone       = phone,
                                    imageUrl    = imageUrl,
                                    isLost      = isLost,
                                    location    = null,
                                    lat = lat,
                                    lng=lng
                                )
                            }

                            // When save succeeds, go back to "reports"
                            LaunchedEffect(uiState) {
                                if (uiState is ReportUiState.SaveSuccess) {
                                    navController.navigate("reports") {
                                        // recreate the screen so it reloads the list
                                        popUpTo("reports") { inclusive = true }
                                    }
                                }
                            }
                        }

                        composable("reports") {
                            val reportVm = remember { ReportViewModel() }
                            val userVm: AndroidUserViewModel = viewModel()
                            val currentUid by userVm.currentUid.collectAsState()

                            LaunchedEffect(currentUid) {
                                println("▶️ Compose sees currentUid = $currentUid")

                                currentUid?.let { reportVm.loadReportsForUser(it) }
                            }

                            val uiState by reportVm.uiState.collectAsState()
                            val reports = when (uiState) {
                                is ReportUiState.ReportsLoaded -> (uiState as ReportUiState.ReportsLoaded).reports
                                else                           -> emptyList()
                            }

                            MyReportsScreen(
                                reports = reports,
                                onPublishClicked = { navController.navigate("new-report") },
                                onItemClick = { rpt ->
                                    val json = kotlinx.serialization.json.Json.encodeToString(rpt)
                                    val encoded = java.net.URLEncoder.encode(json, Charsets.UTF_8.name())
                                    navController.navigate("report-details/$encoded")
                                }
                            )
                        }
                        composable("report-details/{reportJson}") { backStackEntry ->
                            val raw = backStackEntry.arguments?.getString("reportJson").orEmpty()
                            val decoded = URLDecoder.decode(raw, Charsets.UTF_8.name())
                            val report = Json.decodeFromString<ReportModel>(decoded)

                            ReportDetailsScreen(
                                report = report,
                                onBack = { navController.popBackStack() }
                            )
                        }

                    }

                }
            }
        }
    }
}
