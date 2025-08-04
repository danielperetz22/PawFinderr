package org.example.project.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.airbnb.lottie.compose.*
import org.example.project.R

@Composable
fun LoadingAnimation(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
) {
    val compositionRes = R.raw.long_dog

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            visible = true
        } else {
            visible = false
        }
    }

    if (visible) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(compositionRes)
        )
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations  = LottieConstants.IterateForever
        )

        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            LottieAnimation(
                composition = composition,
                progress    = { progress },
                modifier    = Modifier.size(size)
            )
        }
    }
}
