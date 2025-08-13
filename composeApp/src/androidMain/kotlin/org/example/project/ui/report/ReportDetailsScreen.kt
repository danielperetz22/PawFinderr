package org.example.project.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.example.project.data.report.ReportModel

// app colors to match your other screens
private val BgGray        = Color(0xFFF3F3F3)
private val LostColor     = Color(0xFFFF6E61)
private val PrimaryPink   = Color(0xFFFFC0C0)
private val LabelGray     = Color(0xFF8D8D8D)
private val CardStroke    = Color(0xFFD6D6D6)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailsScreen(
    report: ReportModel,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
            modifier = Modifier
                .fillMaxSize()
                .background(BgGray)
                .padding(inner)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = report.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(22.dp)),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = if (report.isLost) "lost!" else "found!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (report.isLost) LostColor else MaterialTheme.colorScheme.primary
                )

                LabeledLine(label = "description :", value = report.description)

                LabeledLine(label = "contact me :", value = report.phone.ifBlank { "—" })
                if (report.name.isNotBlank()) {
                    Text(report.name, style = MaterialTheme.typography.bodyLarge)
                }

                report.location?.takeIf { it.isNotBlank() }?.let { loc ->
                    LabeledLine(label = "location :", value = loc)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, CardStroke, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("map", color = LabelGray, style = MaterialTheme.typography.titleMedium)
                }

                Spacer(Modifier.height(96.dp))
            }

            // Bottom action buttons: Edit + Delete
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPink,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Edit",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun LabeledLine(label: String, value: String) {
    Column {
        Text(
            label,
            color = LabelGray,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}