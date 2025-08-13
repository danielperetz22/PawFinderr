
package org.example.project.ui.report

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.project.R
import org.example.project.location.getLocation

private val balooBhaijaan2Family = FontFamily(
    Font(R.font.baloobhaijaan2_medium,FontWeight.ExtraBold),
    Font(R.font.baloobhaijaan2_semibold,FontWeight.SemiBold),
)
@Composable
fun MapPickerDialog(
    onDismiss: () -> Unit,
    onPicked: (lat: Double, lng: Double) -> Unit
) {
    val context = LocalContext.current

    // Ask location permission only when dialog opens
    var hasPermission by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { res ->
        hasPermission = res[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                res[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    var waitingForFirstFix by remember { mutableStateOf(false) }

    val camera = rememberCameraPositionState()
    val scope = rememberCoroutineScope()
    var picked by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            waitingForFirstFix = true
            val loc = runCatching { withContext(Dispatchers.IO) { getLocation() } }.getOrNull()
            loc?.let {
                val here = LatLng(it.latitude, it.longitude)
                picked = here                    // preselect current location (optional)
                camera.move(CameraUpdateFactory.newLatLngZoom(here, 16f))
            }
            waitingForFirstFix = false
        }
    }

    LaunchedEffect(Unit) {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        hasPermission = fine || coarse
        if (!hasPermission) {
            launcher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }



    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.large,
            color=Color(0xFFF0F0F0)) {
            Column(Modifier.padding(16.dp)) {
                Text("Pick location",  fontFamily = balooBhaijaan2Family,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,)
                Spacer(Modifier.height(12.dp))

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    cameraPositionState = camera,
                    properties = MapProperties(isMyLocationEnabled = hasPermission),
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = hasPermission,
                        zoomControlsEnabled = true
                    ),
                    onMapClick = { latLng ->
                        picked = latLng
                        scope.launch { camera.animate(CameraUpdateFactory.newLatLng(latLng)) }
                    }
                ) {
                    picked?.let { Marker(state = MarkerState(it), title = "Chosen location") }
                }

                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFFF69092)),
                        shape   = RoundedCornerShape(8.dp)
                    ) { Text("Cancel",
                        fontFamily = balooBhaijaan2Family,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        )
                    }

                    Button(
                        onClick = { picked?.let { onPicked(it.latitude, it.longitude); onDismiss() } },
                        enabled = picked != null,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEB0B2)),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Save",
                        fontFamily = balooBhaijaan2Family,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        )

                    }
                }
            }
        }
    }
}
