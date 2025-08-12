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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.rememberAsyncImagePainter
import org.example.project.CloudinaryUploader
import org.example.project.data.report.ReportViewModel

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
    onAddLocation: () -> Unit = {},
    onPublish: (
        description: String,
        name: String,
        phone: String,
        isLost: Boolean,
        imageUrl: String,
        lat: Double?,
        lng: Double?
    ) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }
    var uploadedUrl by remember { mutableStateOf<String?>(null) }
    val reportVm = remember { ReportViewModel() }
    val uiState by reportVm.uiState.collectAsState()


    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            onImagePicked(it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            cameraUri?.let {
                selectedImageUri = it
                onImagePicked(it)
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

    // --- Wrap in a Box to center the Column both vertically & horizontally ---
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
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Lost / Found toggle ---
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
                        contentColor   = Color.White
                    )
                ) {
                    Text("Lost",
                        fontFamily = balooBhaijaan2Family,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,)
                }

                Button(
                    onClick = { isLost = false },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isLost) Color(0xFFF69092) else Color(0xFFFEB0B2),
                        contentColor   = Color.White
                    )
                ) {
                    Text("Found",
                        fontFamily = balooBhaijaan2Family,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,)
                }
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF0F0F0))
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .clickable { showDialog = true },
                contentAlignment = Alignment.BottomEnd
            ) {
                // if we have a URI, show it via Coil…
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

                // …and always overlay the "+" button in the corner
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(28.dp)
                        .background(Color(0xFFF69092), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", color = Color.White, fontSize = 18.sp)
                }
            }

            // AlertDialog to choose camera vs gallery
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Select an image source") },
                    text = { Text("New photo or selection from the gallery?") },
                    confirmButton = {
                        TextButton(onClick = {
                            galleryLauncher.launch("image/*")
                            showDialog = false
                        }) {
                            Text("gallery")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            cameraUri = createImageUri()
                            cameraLauncher.launch(cameraUri!!)
                            showDialog = false
                        }) {
                            Text("photo")
                        }
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            // --- Description field ---
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                placeholder = { Text("Enter a description about the dog") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                singleLine = false
            )

            Spacer(Modifier.height(8.dp))

            // --- Name field ---
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                placeholder = { Text("Add your name here") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // --- Phone field ---
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                placeholder = { Text("+972.....") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                )
            )

            Spacer(Modifier.height(12.dp))

            // --- Add Location button with emoji ---
            OutlinedButton(
                onClick = { showPicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape  = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White.copy(alpha = 0.3f),
                    contentColor   = Color(0xFFFFC0C0) // Primary pink
                )
            ) {
                Text("📍  Add location",
                    fontFamily = balooBhaijaan2Family,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
            if (pickedLocation != null) {
                Text("📍 Location set (${pickedLocation.first}, ${pickedLocation.second})")
            }
            currentPicked?.let { (lat, lng) ->
                Text("📍 Location set ($lat, $lng)")
            }

            if (showPicker) {
                MapPickerDialog(
                    onDismiss = { showPicker = false },
                    onPicked  = { lat, lng -> currentPicked = lat to lng }
                )
            }
            
            Spacer(Modifier.height(12.dp))

            // --- Publish Report ---
            Button(
                onClick = { selectedImageUri?.let { uri ->
                    uploading = true
                    CloudinaryUploader.upload(context, uri) { url ->
                        uploading = false
                        url?.let { imageUrl ->
                            // pass all your current inputs plus the Cloudinary URL:
                            onPublish(
                                description,
                                name,
                                phone,
                                isLost,
                                imageUrl,
                                currentPicked?.first,
                                currentPicked?.second
                            )
                        }
                    }
                }},
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
                        fontFamily = balooBhaijaan2Family,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

