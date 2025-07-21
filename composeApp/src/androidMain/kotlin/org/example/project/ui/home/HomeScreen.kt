package org.example.project.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.R

private val balooBhaijaan2Family = FontFamily(
    Font(R.font.baloobhaijaan2_regular,   FontWeight.Normal),
    Font(R.font.baloobhaijaan2_medium,    FontWeight.Medium),
    Font(R.font.baloobhaijaan2_semibold,  FontWeight.SemiBold),
    Font(R.font.baloobhaijaan2_bold,      FontWeight.Bold),
    Font(R.font.baloobhaijaan2_extrabold, FontWeight.ExtraBold)
)

@Preview(showBackground = true)
@Composable

fun HomeScreen(
    onGetStarted: () -> Unit ={},
    onLogIn: () -> Unit={}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF0F0F0))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(124.dp))
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = "App Logo",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("welcome to PawFinder",
                fontFamily = balooBhaijaan2Family,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp)
            Text("help fins lost dogs quickly!",
                fontFamily = balooBhaijaan2Family,
                fontWeight  = FontWeight.Normal,
                fontSize    = 22.sp
            )
        }
        Spacer(modifier = Modifier.height(32.dp))


        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            listOf(
                "Quick Reports" to "file a report in seconds",
                "Live Map"      to "see every pin at a glance",
                "Direct Pickup" to "call the reporter"
            ).forEach { (title, subtitle) ->
                Row(
                    verticalAlignment   = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(
                                start = 32.dp
                            )
                            .size(30.dp)
                            .background(color = Color(0xFFDAEAE8), shape = CircleShape)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontFamily = balooBhaijaan2Family,
                            fontWeight = FontWeight.Normal,
                        )

                        Text(
                            text = subtitle,
                            fontSize = 14.sp,
                            fontFamily = balooBhaijaan2Family,
                            fontWeight = FontWeight.Normal,
                            )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC0C0), // pink
                    contentColor   = Color.White
                )
            ) {
                Text(
                    "get started",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = balooBhaijaan2Family

                )
            }
            OutlinedButton(
                onClick = onLogIn,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth()
                    .height(44.dp),
                shape  = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White.copy(alpha = 0.3f),
                    contentColor   = Color(0xFFFFC0C0) // Primary pink
                )
            ) {
                Text(
                    "log in",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = balooBhaijaan2Family,
                    )
            }
        }
    }
}
