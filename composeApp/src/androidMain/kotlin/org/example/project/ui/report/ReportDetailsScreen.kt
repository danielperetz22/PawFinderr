package org.example.project.ui.report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.example.project.data.report.ReportModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailsScreen(
    report: ReportModel,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("report details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image
                AsyncImage(
                    model = report.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                // Lost / Found title
                Text(
                    text = if (report.isLost) "lost!" else "found!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (report.isLost) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )

                // Description block
                LabeledLine(label = "description :", value = report.description)

                // Contact
                LabeledLine(label = "contact me :", value = report.phone.ifBlank { "—" })
                if (report.name.isNotBlank()) {
                    Text(report.name, style = MaterialTheme.typography.bodyMedium)
                }

                // Location
                if (!report.location.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📍 ", style = MaterialTheme.typography.bodyLarge)
                        Text(report.location!!, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // (Optional) Map placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    // drop in a static map image or leave empty for now
                }

                Spacer(Modifier.height(80.dp)) // extra space so button doesn't overlap
            }

            Button(
                onClick = { /* TODO: edit action */ },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text("Edit")
            }
        }
    }
}

@Composable
private fun LabeledLine(label: String, value: String) {
    Row {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(6.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
