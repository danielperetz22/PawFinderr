package org.example.project.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.example.project.data.report.ReportModel

// Reuse the same colors you used on ReportDetailsScreen.
// If these already exist in that file, remove these duplicates and import instead.
private val BgGray        = Color(0xFFF3F3F3)
private val LostColor     = Color(0xFFFF6E61)
private val PrimaryPink   = Color(0xFFFFC0C0)
private val LabelGray     = Color(0xFF8D8D8D)
private val CardStroke    = Color(0xFFD6D6D6)

@Composable
fun EditReportScreen(
    report: ReportModel,
    onSave: (description: String, name: String, phone: String, isLost: Boolean) -> Unit
) {
    var description by remember { mutableStateOf(report.description) }
    var name        by remember { mutableStateOf(report.name) }
    var phone       by remember { mutableStateOf(report.phone) }
    var isLost      by remember { mutableStateOf(report.isLost) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGray)
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
                text = if (isLost) "lost!" else "found!",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isLost) LostColor else MaterialTheme.colorScheme.primary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { isLost = true },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLost) LostColor else PrimaryPink.copy(alpha = 0.5f),
                        contentColor = Color.White
                    )
                ) { Text("Lost", fontWeight = FontWeight.Bold) }

                Button(
                    onClick = { isLost = false },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isLost) MaterialTheme.colorScheme.primary else PrimaryPink.copy(alpha = 0.5f),
                        contentColor = Color.White
                    )
                ) { Text("Found", fontWeight = FontWeight.Bold) }
            }

            LabeledEditor(
                label = "description :",
                value = description,
                onValueChange = { description = it }
            )

            LabeledEditor(
                label = "contact me :",
                value = phone,
                onValueChange = { phone = it }
            )

            LabeledEditor(
                label = "name :",
                value = name,
                onValueChange = { name = it }
            )

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

            Spacer(Modifier.height(108.dp))
        }

        // Save only
        Button(
            onClick = { onSave(description, name, phone, isLost) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryPink,
                contentColor = Color.White
            )
        ) {
            Text(
                "Save changes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}


@Composable
private fun LabeledEditor(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Column {
        Text(label, color = LabelGray, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
            textStyle = MaterialTheme.typography.titleMedium,
            singleLine = false,
            shape = RoundedCornerShape(12.dp)
        )
    }
}