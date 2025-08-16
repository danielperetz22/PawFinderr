package org.example.project.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.project.R
import org.example.project.ui.components.LoadingAnimation
import org.example.project.ui.home.AndroidUserViewModel


private val balooBhaijaan2Family = FontFamily(
    Font(R.font.baloobhaijaan2_regular,   FontWeight.Normal),
    Font(R.font.baloobhaijaan2_medium,    FontWeight.Medium),
    Font(R.font.baloobhaijaan2_semibold,  FontWeight.SemiBold),
    Font(R.font.baloobhaijaan2_bold,      FontWeight.Bold),
    Font(R.font.baloobhaijaan2_extrabold, FontWeight.ExtraBold)
)


@Composable
fun ProfileScreen(onSignOut: () -> Unit = {},
                  isLoading: Boolean,
                  errorMessage : String?,
                  ) {
    val vm: AndroidUserViewModel = viewModel()
    val email by vm.currentEmail.collectAsState()
    val signedIn by vm.currentUid.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF0F0F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (signedIn != null) {
                OutlinedTextField(
                    value = email ?: "",
                    onValueChange = {},
                    label = { Text("email") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text("not connected")
            }
            if (isEditing) {
                OutlinedTextField(
                    value               = newPassword,
                    onValueChange       = { newPassword = it },
                    label               = { Text("Enter new password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier            = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value               = confirmPassword,
                    onValueChange       = { confirmPassword = it },
                    label               = { Text("Confirm new password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier            = Modifier.fillMaxWidth()
                )
                if (errorMessage != null) {
                    Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                }
                if (localError != null) {
                    Text(localError!!, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                } else if (errorMessage != null) {
                    Text(errorMessage, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if(!isEditing) {
                Button(
                    onClick = {
                        onSignOut()
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEB0B2)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "logout",
                        color = Color.White,
                        fontFamily = balooBhaijaan2Family,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                SmallFloatingActionButton(
                    onClick = {
                        if (!isLoading) {
                            isEditing = true
                        }
                    },
                    modifier = Modifier
                        .alpha(if (isLoading) 0.4f else 1f),
                    containerColor = Color(0xFFFEB0B2),
                    contentColor = Color.White,

                    ) {

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile"
                    )
                }
            }else{
                Button(
                    onClick = { isEditing = false },
                    enabled = !isLoading,
                    colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFFF69092)),
                    shape   = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Cancel",
                        color = Color.White,
                        fontFamily = balooBhaijaan2Family,
                        fontWeight = FontWeight.ExtraBold)
                }
                Button(
                    onClick = {
                              if(newPassword.isBlank()||newPassword!=confirmPassword){
                                  localError = "Passwords do not match"
                              }
                              else{
                                  vm.changePassword(newPassword)
                                  localError = null
                                  isEditing = false
                              }},
                    enabled = !isLoading,
                    colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEB0B2)),
                    shape   = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Save changes",
                        color = Color.White,
                        fontFamily = balooBhaijaan2Family,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }


        if (isLoading) {
            Box(
                Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            LoadingAnimation(
                isLoading = isLoading,
                modifier  = Modifier.matchParentSize()
            )
        }

        LaunchedEffect(isLoading, errorMessage) {
            if (!isLoading && errorMessage == null && isEditing) {
                isEditing = false
                newPassword = ""
                confirmPassword = ""
            }
        }
    }
}






