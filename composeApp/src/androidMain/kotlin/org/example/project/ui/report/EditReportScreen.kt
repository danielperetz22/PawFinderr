package org.example.project.ui.report

import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.R
import org.example.project.data.report.ReportModel
import java.util.Locale

private val balooBhaijaan2Family = FontFamily(
    Font(R.font.baloobhaijaan2_regular,   FontWeight.Normal),
    Font(R.font.baloobhaijaan2_medium,    FontWeight.Medium),
    Font(R.font.baloobhaijaan2_semibold,  FontWeight.SemiBold),
    Font(R.font.baloobhaijaan2_bold,      FontWeight.Bold),
    Font(R.font.baloobhaijaan2_extrabold, FontWeight.ExtraBold)
)

private val BgGray      = Color(0xFFF0F0F0)
private val LostColor   = Color(0xFFF69092)
private val PrimaryPink = Color(0xFFFEB0B2)
private val LabelGray   = Color(0xFF8D8D8D)
private val CardStroke  = Color(0xFFD6D6D6)

@Composable
fun EditReportScreen(
    report: ReportModel,
    onSave: (description: String, name: String, phone: String, isLost: Boolean, lat: Double, lng: Double) -> Unit
) {
    // Text fields
    var description by remember { mutableStateOf(report.description) }
    var name        by remember { mutableStateOf(report.name) }
    var phone       by remember { mutableStateOf(report.phone) }
    var isLost      by remember { mutableStateOf(report.isLost) }

    // Location (pre-filled from report; stays as-is unless user changes it)
    var draftLat    by remember { mutableStateOf(report.lat) }
    var draftLng    by remember { mutableStateOf(report.lng) }
    var showPicker  by remember { mutableStateOf(false) }
    var locationErr by remember { mutableStateOf<String?>(null) }

    val scroll = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGray)
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { isLost = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLost) LostColor else PrimaryPink.copy(alpha = 0.5f),
                        contentColor   = Color.White
                    )
                ) { Text("Lost", fontWeight = FontWeight.Bold) }

                Button(
                    onClick = { isLost = false },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isLost) MaterialTheme.colorScheme.primary else PrimaryPink,
                        contentColor   = Color.White
                    )
                ) { Text("Found", fontWeight = FontWeight.Bold) }
            }

            AsyncImage(
                model = report.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            LabeledEditor("description :", description) { description = it }
            LabeledEditor("contact me :",  phone)       { phone = it }
            LabeledEditor("name :",        name)        { name = it }

            // Location (current value shown; user can change via dialog)
            Text("location", color = LabelGray, style = MaterialTheme.typography.bodyMedium)
            LocationEditor(
                lat = draftLat,
                lng = draftLng,
                onChangeClick = {
                    showPicker = true
                    locationErr = null
                }
            )

            locationErr?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }

            Spacer(Modifier.height(96.dp))
        }

        // Save button is INSIDE the Box so .align works
        Button(
            onClick = {
                val lat = draftLat
                val lng = draftLng
                if (lat == null || lng == null) {
                    locationErr = "Please pick a location before saving."
                    return@Button
                }
                onSave(description, name, phone, isLost, lat, lng)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryPink,
                contentColor   = Color.White
            )
        ) {
            Text(
                "Save changes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        // Open your existing MapPickerDialog when the user taps "Change location"
        if (showPicker) {
            MapPickerDialog(
                onDismiss = { showPicker = false },
                onPicked  = { lat, lng ->
                    draftLat = lat
                    draftLng = lng
                    showPicker = false
                }
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

@Composable
private fun LocationEditor(
    lat: Double?,
    lng: Double?,
    onChangeClick: () -> Unit
) {
    val context = LocalContext.current
    val spot = lat?.let { la -> lng?.let { lo -> LatLng(la, lo) } }

    // Resolve address from current (or unchanged) coordinates
    var address by remember(lat, lng) { mutableStateOf<String?>(null) }
    LaunchedEffect(lat, lng) {
        address = if (lat == null || lng == null) null
        else try {
            withContext(Dispatchers.IO) {
                val list = Geocoder(context, Locale.getDefault()).getFromLocation(lat, lng, 1)
                val a = list?.firstOrNull()
                a?.getAddressLine(0)
            }
        } catch (_: Exception) {
            null
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = when {
                address != null -> address!!
                spot != null    -> String.format(Locale.getDefault(), "%.5f, %.5f", lat, lng)
                else            -> "No location selected"
            },
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(12.dp))
        OutlinedButton(
            onClick = onChangeClick,
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(if (spot == null) "Pick location" else "Change location")
        }
    }

    // (Optional) if you want a thin divider card below:
    Spacer(Modifier.height(8.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(CardStroke)
    )
}
