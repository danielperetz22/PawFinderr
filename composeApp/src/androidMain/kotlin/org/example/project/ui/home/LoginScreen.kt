package org.example.project.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.R
import org.example.project.ui.components.LoadingAnimation


private val balooBhaijaan2Family = FontFamily(
    Font(R.font.baloobhaijaan2_regular,   FontWeight.Normal),
    Font(R.font.baloobhaijaan2_medium,    FontWeight.Medium),
    Font(R.font.baloobhaijaan2_semibold,  FontWeight.SemiBold),
    Font(R.font.baloobhaijaan2_bold,      FontWeight.Bold),
    Font(R.font.baloobhaijaan2_extrabold, FontWeight.ExtraBold)
)

@Composable
fun LoginScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onLogin: (email: String, password: String) -> Unit ={_, _ ->} ,
    onNavigateToRegister: () -> Unit ={}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFFF0F0F0))
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(80.dp))
            Text(
                "welcome back!",
                color = Color(0xFFDA90D1D8),
                fontFamily = balooBhaijaan2Family,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
            )

            Spacer(Modifier.height(40.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("email") },
                    placeholder = { Text("enter email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("password") },
                    placeholder = { Text("enter password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Button(
                onClick = { onLogin(email, password) },
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
                    "log in",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = balooBhaijaan2Family
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Don’t have an account?",
                    fontFamily = balooBhaijaan2Family,
                    fontWeight = FontWeight.Normal,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Sign up",
                    fontFamily = balooBhaijaan2Family,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    modifier = Modifier
                        .clickable { onNavigateToRegister() }
                        .padding(0.dp)
                )
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
    }
}

