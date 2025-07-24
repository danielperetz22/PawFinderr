package org.example.project.ui.bottomBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home      : BottomNavItem("feed", Icons.Default.Place,      "Home")
    object Profile   : BottomNavItem("profile", Icons.Default.Person,  "Profile")
    object MyReports : BottomNavItem("reports", Icons.Default.List,    "My Reports")
}
