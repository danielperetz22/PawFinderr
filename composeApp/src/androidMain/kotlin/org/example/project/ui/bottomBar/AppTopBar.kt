package org.example.project.ui.bottomBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.example.project.R

private val balooBhaijaan2Family = FontFamily(
    Font(R.font.baloobhaijaan2_semibold,  FontWeight.SemiBold),
)

fun Modifier.bottomBorder(width: Dp, color: Color): Modifier = this.then(
    Modifier.drawBehind {
        val stroke = width.toPx()
        drawLine(
            color = color,
            strokeWidth = stroke,
            start = Offset(0f, size.height - stroke / 2),
            end = Offset(size.width, size.height - stroke / 2)
        )
    }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title, fontFamily = balooBhaijaan2Family,
            fontWeight = FontWeight.SemiBold) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White.copy(alpha = 0.3f)
        ),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .bottomBorder(width = 2.dp, color = Color(0xFF90D1D8))

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AppTopBarPreview() {
    AppTopBar(
        title = "Feed",
        onBackClick = { /* אין פעולה ב־Preview */ }
    )
}
