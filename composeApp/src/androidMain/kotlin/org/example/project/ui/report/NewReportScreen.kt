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
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.R
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext

private val balooBhaijaan2Family = FontFamily(
    Font(R.font.baloobhaijaan2_regular,   FontWeight.Normal),
    Font(R.font.baloobhaijaan2_medium,    FontWeight.Medium),
    Font(R.font.baloobhaijaan2_semibold,  FontWeight.SemiBold),
    Font(R.font.baloobhaijaan2_bold,      FontWeight.Bold),
    Font(R.font.baloobhaijaan2_extrabold, FontWeight.ExtraBold)
)

@Preview(showBackground = true)
@Composable
fun NewReportScreen(
    onImagePicked: (Uri) -> Unit = {},
    onAddLocation: () -> Unit = {},
    onPublish: () -> Unit = {}
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImagePicked(it) }
    }


    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            cameraUri
                ?.let { onImagePicked(it) }       // ← אין פה smart cast, ו־it כבר non‑null
        }
    }


    fun createImageUri(): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return context.contentResolver
            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw IllegalStateException("Cannot create image uri")
    }
    var isLost by remember { mutableStateOf(true) }
    var description by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // --- Wrap in a Box to center the Column both vertically & horizontally ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center                                            // ← CENTER!
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)                                                     // ← only 90% wide
                .wrapContentHeight(),                                                   // ← height wraps content
            horizontalAlignment = Alignment.CenterHorizontally                          // ← children centered
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

            // --- Photo placeholder with "+" ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .clickable { showDialog = true },
                contentAlignment = Alignment.BottomEnd
            ) {
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

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("בחר מקור תמונה") },
                    text = { Text("צילום חדש או בחירה מהגלריה?") },
                    confirmButton = {
                        TextButton(onClick = {
                            galleryLauncher.launch("image/*")
                            showDialog = false
                        }) { Text("גלריה") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            cameraUri = createImageUri()
                            cameraLauncher.launch(cameraUri!!)
                            showDialog = false
                        }) { Text("מצלמה") }
                    }
                )
            }


            Spacer(Modifier.height(12.dp))

            // --- Photo placeholder with "+" ---

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
                value = name,                                       // ← CHANGED
                onValueChange = { name = it },                      // ← CHANGED
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
                keyboardOptions = KeyboardOptions(                // ← CHANGED
                    keyboardType = KeyboardType.Phone
                )
            )

            Spacer(Modifier.height(12.dp))

            // --- Add Location button with emoji ---
            OutlinedButton(
                onClick = onAddLocation,
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
            Spacer(Modifier.height(12.dp))

            // --- Publish Report ---
            Button(
                onClick = onPublish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC0C0),
                    contentColor = Color.White
                )
            ) {
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

