// MyReportsScreen.kt
package org.example.project.ui.report

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.example.project.R
import org.example.project.data.report.ReportModel
import org.example.project.ui.components.LoadingAnimation

private val balooBhaijaan2Family = FontFamily(
    Font(R.font.baloobhaijaan2_regular,   FontWeight.Normal),
    Font(R.font.baloobhaijaan2_medium,    FontWeight.Medium),
    Font(R.font.baloobhaijaan2_semibold,  FontWeight.SemiBold),
    Font(R.font.baloobhaijaan2_bold,      FontWeight.Bold),
    Font(R.font.baloobhaijaan2_extrabold, FontWeight.ExtraBold)
)

@Composable
fun MyReportsScreen(
    reports: List<ReportModel>,
    onPublishClicked: () -> Unit,
    onItemClick: (ReportModel) -> Unit = {},
    isLoading: Boolean = false
) {
    val sortedReports = reports.sortedByDescending { it.id }


    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        if (sortedReports.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No reports yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 12.dp)
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
            .height(140.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (rpt.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = rpt.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // gray placeholder
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f))
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title.ifBlank { "Untitled" },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = balooBhaijaan2Family,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (rpt.description.isNotBlank() && rpt.description != title) {
                    Text(
                        text = rpt.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = balooBhaijaan2Family,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (rpt.isLost) "Lost" else "Found",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = balooBhaijaan2Family,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .background(
                                color = Color(0xFFFEB0B2),
                                shape = RoundedCornerShape(50)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )

                    if (rpt.phone.isNotBlank()) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = rpt.phone,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = balooBhaijaan2Family,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                    }
                }
            }
        }
    }
}