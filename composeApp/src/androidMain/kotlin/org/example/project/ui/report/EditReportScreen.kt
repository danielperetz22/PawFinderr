package org.example.project.ui.report

import android.content.Context
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.TextStyle
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
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import coil3.compose.AsyncImage
import com.cloudinary.android.MediaManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.example.project.R
import org.example.project.data.report.ReportModel
import java.util.Locale
import kotlin.coroutines.resumeWithException


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




@Composable
fun EditReportScreen(
    report: ReportModel,
    onSave: (
        description: String,
        name: String,
        phone: String,
        isLost: Boolean,
        lat: Double?,
        lng: Double?,
        imageUrl: String?
    ) -> Unit
) {
    // --- image state ---
    var localImageUri by remember { mutableStateOf<Uri?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) localImageUri = uri }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri != null) {
            localImageUri = cameraUri
        }
    }

    // --- text/flags ---
    var description by remember { mutableStateOf(report.description) }
    var name by remember { mutableStateOf(report.name) }
    var phone by remember { mutableStateOf(report.phone) }
    var isLost by remember { mutableStateOf(report.isLost) }

    // --- location ---
    var draftLat by remember { mutableStateOf(report.lat) }   // Double?
    var draftLng by remember { mutableStateOf(report.lng) }   // Double?
    var showPicker by remember { mutableStateOf(false) }
    var locationErr by remember { mutableStateOf<String?>(null) }

    val scroll = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGray)
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        // ===== Scrollable content =====
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { isLost = true },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLost) LostColor else PrimaryPink,
                        contentColor = Color.White
                    )
                ) { Text("Lost", fontFamily = balooBhaijaan2Family, fontWeight = FontWeight.Bold, fontSize = 16.sp) }

                Button(
                    onClick = { isLost = false },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isLost) LostColor else PrimaryPink,
                        contentColor = Color.White
                    )
                ) { Text("Found", fontFamily = balooBhaijaan2Family, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            }

            // --- image with pencil that opens dialog ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = localImageUri ?: report.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )

                SmallFloatingActionButton(
                    onClick = { showImageSourceDialog = true },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
                    containerColor = Color(0xFF90D1D8),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Change photo")
                }

                if (localImageUri != null) {
                    SmallFloatingActionButton(
                        onClick = { localImageUri = null },
                        modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
                        containerColor = Color(0xFF90D1D8),
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Revert to original")
                    }
                }
            }

            LabeledEditor("description :", description) { description = it }
            LabeledEditor("contact me :", phone) { phone = it }
            LabeledEditor("name :", name) { name = it }

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

            // space so sticky save doesn't cover content
            Spacer(Modifier.height(100.dp))
        }

        // ===== sticky bottom Save button =====
        Button(
            enabled = !isSaving,
            onClick = {
                val lat = draftLat
                val lng = draftLng

                scope.launch {
                    isSaving = true
                    try {
                        val finalUrl = localImageUri?.let { uploadToCloudinary(context, it) }
                        onSave(description, name, phone, isLost, lat!!, lng!!, finalUrl)
                    } catch (_: Throwable) {
                        // Keep existing image if upload failed
                        onSave(description, name, phone, isLost, lat!!, lng!!, null)
                    } finally {
                        isSaving = false
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryPink,
                contentColor = Color.White
            )
        ) {
            Text(
                if (isSaving) "Saving…" else "Save changes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        // Map picker sheet/dialog
        if (showPicker) {
            MapPickerDialog(
                onDismiss = { showPicker = false },
                onPicked = { lat, lng ->
                    draftLat = lat
                    draftLng = lng
                    showPicker = false
                }
            )
        }

        // Image source dialog (Camera / Gallery)
        if (showImageSourceDialog) {
            AlertDialog(
                onDismissRequest = { showImageSourceDialog = false },
                title = { Text("Select an image source") },
                text  = { Text("New photo or selection from the gallery?") },
                confirmButton = {
                    TextButton(onClick = {
                        galleryLauncher.launch("image/*")
                        showImageSourceDialog = false
                    }) { Text("Gallery") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        cameraUri = createImageUri(context)
                        cameraLauncher.launch(cameraUri!!)
                        showImageSourceDialog = false
                    }) { Text("Camera") }
                }
            )
        }
    }
}

// Helper for camera Uri
private fun createImageUri(context: Context): Uri {
    val cv = android.content.ContentValues().apply {
        put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "report_${System.currentTimeMillis()}.jpg")
        put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return context.contentResolver.insert(
        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        cv
    )!!
}

suspend fun uploadToCloudinary(ctx: Context, uri: Uri): String =
    withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { cont ->
            MediaManager.get().upload(uri)
                .option("resource_type", "image")
                .callback(object : com.cloudinary.android.callback.UploadCallback {
                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, total: Long) {}
                    override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                        val url = (resultData["secure_url"] ?: resultData["url"])?.toString()
                        if (url != null) cont.resume(url) {} else cont.resumeWithException(IllegalStateException("No URL"))
                    }
                    override fun onError(requestId: String?, error: com.cloudinary.android.callback.ErrorInfo?) {
                        cont.resumeWithException(RuntimeException(error?.description ?: "Upload failed"))
                    }
                    override fun onReschedule(requestId: String?, error: com.cloudinary.android.callback.ErrorInfo?) {
                        cont.resumeWithException(RuntimeException(error?.description ?: "Upload rescheduled"))
                    }
                })
                .dispatch(ctx)
        }
    }


@Composable
private fun LabeledEditor(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        label = {
            Text(
                // drop trailing " :" if present
                text = label.removeSuffix(" :"),
                color = LabelGray,
            )
        },
        singleLine = false,
        shape = RoundedCornerShape(8.dp),
    )
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
                address != null-> address!!
                spot != null-> String.format(Locale.getDefault(), "%.5f, %.5f", lat, lng)
                else-> "No location selected"
            },
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(12.dp))
        OutlinedButton(
            onClick = onChangeClick,
            shape  = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color.White),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White.copy(alpha = 0.3f),
                contentColor   = PrimaryPink
            )
        ) {
            Text(
                if (lat == null || lng == null) "Pick location" else "Change location",
                fontFamily = balooBhaijaan2Family,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp
            )
        }

    }
}
