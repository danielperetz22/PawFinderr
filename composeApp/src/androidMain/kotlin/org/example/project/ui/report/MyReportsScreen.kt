package org.example.project.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.data.report.ReportModel

@Composable
fun MyReportsScreen(
    reports: List<ReportModel>,
    onPublishClicked: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onPublishClicked,
                modifier = Modifier
                    // lift it above any bottom bar (56dp) + margin
                    .padding(end = 16.dp, bottom = 56.dp + 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New report")
            }
        }
    ) { innerPadding ->
        if (reports.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No reports yet")
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(reports) { rpt ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .size(60.dp)
                                    .background(Color.LightGray, RoundedCornerShape(8.dp))
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(rpt.description, style = MaterialTheme.typography.bodyLarge)
                                Spacer(Modifier.height(4.dp))
                                Text(rpt.name, style = MaterialTheme.typography.bodyMedium)
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    "${rpt.phone}  •  ${if (rpt.isLost) "Lost" else "Found"}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}