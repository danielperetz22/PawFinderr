package org.example.project.ui.report

import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
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

private val BgGray      = Color(0xFFF3F3F3)
private val LostColor   = Color(0xFF90D1D8)
private val PrimaryPink = Color(0xFFFFC0C0)
private val LabelGray   = Color(0xFF8D8D8D)
private val CardStroke  = Color(0xFFD6D6D6)

@Composable
fun ReportDetailsScreen(
    report: ReportModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGray)
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        val scroll = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = report.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Text(
                text = if (report.isLost) "lost!" else "found!",
                fontFamily = balooBhaijaan2Family,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                color = LostColor
            )

            InlineLabel("description:", report.description)
            InlineLabel("contact me:", report.phone.ifBlank { "—" })
            if (report.name.isNotBlank()) {
                Text(report.name, fontFamily = balooBhaijaan2Family, fontWeight = FontWeight.Medium)
            }


            val lat = report.lat
            val lng = report.lng
            val context = LocalContext.current

            if (lat != null && lng != null) {
                Text(
                    if (report.isLost) "last seen" else "found here",
                    fontFamily = balooBhaijaan2Family,
                    fontWeight = FontWeight.Medium
                )

                AddressBlock(lat = lat, lng = lng, modifier = Modifier.padding(top = 4.dp))

                report.location?.takeIf { it.isNotBlank() }?.let { note ->
                    Spacer(Modifier.height(6.dp))
                    InlineLabel(label = "location note :", value = note)
                }

                val spot = LatLng(lat, lng)
                val cameraState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(spot, 16f)
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    GoogleMap(
                        cameraPositionState = cameraState,
                        properties = MapProperties(isMyLocationEnabled = false),
                        uiSettings = MapUiSettings(
                            myLocationButtonEnabled = false,
                            zoomControlsEnabled = true
                        )
                    ) {
                        Marker(
                            state = MarkerState(position = spot),
                            title = report.name.ifBlank { "Location" }
                        )
                    }
                }

                TextButton(
                    onClick = {
                        val name = Uri.encode(report.name.ifBlank { "Location" })
                        val gmm = Uri.parse("geo:$lat,$lng?q=$lat,$lng($name)")
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, gmm)
                                    .setPackage("com.google.android.apps.maps")
                            )
                        }.onFailure {
                            context.startActivity(Intent(Intent.ACTION_VIEW, gmm))
                        }
                    }
                ) {
                    Text("Open in Google Maps")
                }
            } else {
                // Placeholder when no coordinates are saved
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, CardStroke, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No location provided", color = LabelGray, style = MaterialTheme.typography.titleMedium)
                }
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

@Composable
fun InlineLabel(label: String, value: String, modifier: Modifier = Modifier) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontFamily = balooBhaijaan2Family, fontWeight = FontWeight.Bold)) {
                append(label)
                if (!label.endsWith(" ")) append(" ")
            }
            withStyle(SpanStyle(fontFamily = balooBhaijaan2Family, fontWeight = FontWeight.Medium)) {
                append(value)
            }
        },
        style = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = balooBhaijaan2Family,
            fontSize = 16.sp
        ),
        modifier = modifier
    )
}


@Composable
private fun AddressBlock(
    lat: Double,
    lng: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var text by remember(lat, lng) { mutableStateOf<String?>(null) }

    LaunchedEffect(lat, lng) {
        text = try {
            withContext(Dispatchers.IO) {
                val geocoder = Geocoder(context, Locale.getDefault())
                val list = geocoder.getFromLocation(lat, lng, 1)
                val addr = list?.firstOrNull()
                when {
                    addr == null -> null
                    !addr.featureName.isNullOrBlank() && !addr.thoroughfare.isNullOrBlank() ->
                        "${addr.featureName} ${addr.thoroughfare}, ${addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: addr.countryName.orEmpty()}"
                    !addr.thoroughfare.isNullOrBlank() ->
                        "${addr.thoroughfare}, ${addr.locality ?: addr.adminArea ?: addr.countryName.orEmpty()}"
                    else ->
                        addr.getAddressLine(0)
                }
            }
        } catch (_: Exception) {
            null
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(Icons.Filled.Place, contentDescription = null, tint = PrimaryPink)
        Spacer(Modifier.width(8.dp))
        Text(
            text = text ?: String.format(Locale.getDefault(), "%.5f, %.5f", lat, lng),
            fontFamily = balooBhaijaan2Family,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
    }
}
