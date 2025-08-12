package org.example.project.ui.report

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import org.example.project.IOScope.scope

@Composable
fun MapPickerScreen(
    onCancel: () -> Unit,
    onPick: (lat: Double, lng: Double) -> Unit
) {
    val context = LocalContext.current

    // permission
    var hasPermission by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { res ->
        hasPermission = res[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                res[Manifest.permission.ACCESS_COARSE_LOCATION] == true
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

    val camera = rememberCameraPositionState()
    val scope = rememberCoroutineScope()
    var picked by remember { mutableStateOf<LatLng?>(null) }

    // Let user tap to choose
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = camera,
        properties = MapProperties(isMyLocationEnabled = hasPermission),
        uiSettings = MapUiSettings(myLocationButtonEnabled = hasPermission, zoomControlsEnabled = true),
        onMapClick = { latLng ->
            picked = latLng
            scope.launch {
                camera.animate(CameraUpdateFactory.newLatLng(latLng))
            }
        }
    ) {
        picked?.let { Marker(state = MarkerState(it), title = "Chosen location") }
    }

    // confirm / cancel
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f)
        ) { Text("Cancel") }

        Button(
            onClick = { picked?.let { onPick(it.latitude, it.longitude) } },
            enabled = picked != null,
            modifier = Modifier.weight(1f)
        ) { Text("Use this place") }
    }
}
