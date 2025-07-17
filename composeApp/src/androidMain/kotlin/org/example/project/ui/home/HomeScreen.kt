package org.example.project.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onGetStarted: () -> Unit,
    onLogIn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        // (You can swap this for your actual logo composable)
        Text(
            text = "PawFinder",
            style = MaterialTheme.typography.headlineLarge
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("welcome to PawFinder", style = MaterialTheme.typography.titleMedium)
            Text("help fins lost dogs quickly!", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(24.dp))

            Text("•  Quick Reports", style = MaterialTheme.typography.bodyLarge)
            Text("   file a report in seconds", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(12.dp))

            Text("•  Live Map", style = MaterialTheme.typography.bodyLarge)
            Text("   see every pin at a glance", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(12.dp))

            Text("•  Direct Pickup", style = MaterialTheme.typography.bodyLarge)
            Text("   call the reporter", style = MaterialTheme.typography.bodySmall)
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onGetStarted,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("get started")
            }
            OutlinedButton(
                onClick = onLogIn,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("log in")
            }
        }
    }
}
