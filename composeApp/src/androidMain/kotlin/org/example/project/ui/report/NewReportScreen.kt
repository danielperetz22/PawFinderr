package org.example.project.ui.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.R
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.rememberAsyncImagePainter
import org.example.project.CloudinaryUploader

private val balooBhaijaan2Family = FontFamily(
    Font(R.font.baloobhaijaan2_regular,   FontWeight.Normal),
    Font(R.font.baloobhaijaan2_medium,    FontWeight.Medium),
    Font(R.font.baloobhaijaan2_semibold,  FontWeight.SemiBold),
    Font(R.font.baloobhaijaan2_bold,      FontWeight.Bold),
    Font(R.font.baloobhaijaan2_extrabold, FontWeight.ExtraBold)
)

@Composable
fun NewReportScreen(
    pickedLocation: Pair<Double, Double>?,
    onImagePicked: (Uri) -> Unit = {},
    onPublish: (
        description: String,
        name: String,
        phone: String,
        isLost: Boolean,
        imageUrl: String,
        lat: Double,
        lng: Double
    ) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }

    // NEW: error message
    var errorText by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            onImagePicked(it)
            errorText = null // clear error if user fixed it
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            cameraUri?.let {
                selectedImageUri = it
                onImagePicked(it)
                errorText = null
            }
        }
    }

    fun createImageUri(): Uri {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return context.contentResolver
            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: error("Couldn't create URI for camera image")
    }

    var isLost by remember { mutableStateOf(true) }
    var description by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var showPicker by remember { mutableStateOf(false) }
    var currentPicked by remember(pickedLocation) { mutableStateOf(pickedLocation) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Lost / Found toggle (unchanged) …
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { isLost = true },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLost) Color(0xFFF69092) else Color(0xFFFEB0B2),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Lost",
                        fontFamily = balooBhaijaan2Family,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }

                Button(
                    onClick = { isLost = false },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isLost) Color(0xFFF69092) else Color(0xFFFEB0B2),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Found",
                        fontFamily = balooBhaijaan2Family,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Photo picker (unchanged) …
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF0F0F0))
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.BottomEnd
            ) {
                selectedImageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                SmallFloatingActionButton(
                    onClick = { showDialog = true },
                    modifier = Modifier.padding(12.dp),
                    containerColor = Color(0xFF90D1D8),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New report")
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Select an image source") },
                    text = { Text("New photo or selection from the gallery?") },
                    confirmButton = {
                        TextButton(onClick = {
                            galleryLauncher.launch("image/*")
                            showDialog = false
                        }) { Text("gallery") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            cameraUri = createImageUri()
                            cameraLauncher.launch(cameraUri!!)
                            showDialog = false
                        }) { Text("photo") }
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    if (it.isNotBlank()) errorText = null
                },
                label = { Text("Description") },
                placeholder = { Text("Enter a description about the dog") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                singleLine = false
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (it.isNotBlank()) errorText = null
                },
                label = { Text("Name") },
                placeholder = { Text("Add your name here") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    if (it.isNotBlank()) errorText = null
                },
                label = { Text("Phone") },
                placeholder = { Text("+972.....") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { showPicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape  = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White.copy(alpha = 0.3f),
                    contentColor   = Color(0xFFFFC0C0)
                )
            ) { Text("📍  Add location") }

            currentPicked?.let { (lat, lng) ->
                Text("📍 Location set ($lat, $lng)")
            }

            if (showPicker) {
                MapPickerDialog(
                    onDismiss = { showPicker = false },
                    onPicked  = { lat, lng ->
                        currentPicked = lat to lng
                        errorText = null
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            // NEW: error label
            if (errorText != null) {
                Text(
                    errorText!!,
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
            }

            Button(
                onClick = {
                    // VALIDATION
                    val missing =
                        description.isBlank() ||
                                name.isBlank() ||
                                phone.isBlank() ||
                                selectedImageUri == null ||
                                currentPicked == null

                    if (missing) {
                        errorText = "Please fill all fields, add a photo, and set a location."
                        return@Button
                    }

                    val (lat, lng) = currentPicked!!
                    selectedImageUri?.let { uri ->
                        uploading = true
                        errorText = null
                        CloudinaryUploader.upload(context, uri) { url ->
                            uploading = false
                            url?.let { imageUrl ->
                                onPublish(
                                    description,
                                    name,
                                    phone,
                                    isLost,
                                    imageUrl,
                                    lat,
                                    lng
                                )
                            } ?: run {
                                errorText = "Image upload failed. Please try again."
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC0C0),
                    contentColor = Color.White
                )
            ) {
                if (uploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        "Publish Report",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}