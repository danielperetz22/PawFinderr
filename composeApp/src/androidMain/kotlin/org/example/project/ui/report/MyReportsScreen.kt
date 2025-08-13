package org.example.project.ui.report

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.example.project.data.report.ReportModel

@Composable
fun MyReportsScreen(
    reports: List<ReportModel>,
    onPublishClicked: () -> Unit,
    onItemClick: (ReportModel) -> Unit = {}
) {


    // Sort by newest first (replace "timestamp" with your actual field)
    val sortedReports = reports.sortedByDescending { it.id }
    // or .sortedByDescending { it.timestamp } if you have one

    Box(
        Modifier
            .fillMaxSize()
    ) {
        if (sortedReports.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No reports yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 36.dp,
                )
            ) {
                items(sortedReports, key = { it.id }) { rpt ->
                    ReportItem(rpt = rpt, onClick = { onItemClick(rpt) })
                }
            }
        }

        SmallFloatingActionButton(
            onClick = onPublishClicked,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            containerColor = Color(0xFF90D1D8),
            contentColor = Color.White,
        ) {
            Icon(Icons.Default.Add, contentDescription = "New report")
        }
    }
}


@Composable
private fun ReportItem(
    rpt: ReportModel,
    onClick: () -> Unit
) {
    val title = if (rpt.name.isNotBlank()) rpt.name else rpt.description


    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = rpt.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = title.ifBlank { "Untitled" },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (rpt.description.isNotBlank() && rpt.description != title) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = rpt.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AssistChip(
                        onClick = {},
                        label = { Text(if (rpt.isLost) "Lost" else "Found") }
                    )
                    if (rpt.phone.isNotBlank()) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = rpt.phone,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
