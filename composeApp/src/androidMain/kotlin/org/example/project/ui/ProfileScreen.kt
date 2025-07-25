package org.example.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.project.R
import org.example.project.data.firebase.RemoteFirebaseRepository
import org.example.project.ui.home.AndroidUserViewModel
import org.example.project.user.UserViewModel


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
//    val vm = remember { UserViewModel(repo = RemoteFirebaseRepository()) }
    val vm: AndroidUserViewModel = viewModel()
    val email by vm.currentEmail.collectAsState()
    val signedIn by vm.currentUid.collectAsState()


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
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(bottom = 110.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    onSignOut() },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEB0B2)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("logout",
                    color = Color.White,
                    fontFamily = balooBhaijaan2Family,
                    fontWeight = FontWeight.ExtraBold,
                )
            }

            SmallFloatingActionButton(
                onClick = { /* פעולה לערוך פרופיל */ },
                containerColor = Color(0xFFFEB0B2),
                contentColor   = Color.White
                ) {

                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile"
                )

            }
        }
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}






