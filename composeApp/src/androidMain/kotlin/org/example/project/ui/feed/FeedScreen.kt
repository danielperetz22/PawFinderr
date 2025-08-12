package org.example.project.ui.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapView() {
    val center = LatLng(32.0853, 34.7818)
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(center, 12f)
    }
    GoogleMap(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, bottom = 80.dp),
        cameraPositionState = cameraState
    ) {
        Marker(state = MarkerState(position = center), title = "Tel Aviv")
    }
}

@Composable
fun FeedScreen(onPublishClicked: () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MapView()
        SmallFloatingActionButton(
            onClick = onPublishClicked,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp, top = 16.dp),
            containerColor = Color(0xFF90D1D8),
            contentColor = Color.White,
        ) {
            Icon(Icons.Default.Add, contentDescription = "New report")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedScreenPreview() {
    MaterialTheme {
        FeedScreen()
    }
}
