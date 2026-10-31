package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ZaraCyan
import com.example.ui.theme.ZaraPink
import com.example.ui.theme.ZaraPurple
import kotlin.math.sin

@Composable
fun WaveformVisualizer(
    isSpeaking: Boolean,
    amplitude: Float = 0.5f,
    modifier: Modifier = Modifier,
    barCount: Int = 28,
    maxHeight: Dp = 48.dp,
    barColorGradient: List<Color> = listOf(ZaraPurple, ZaraCyan, ZaraPink)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(maxHeight)
    ) {
        val totalWidth = size.width
        val canvasHeight = size.height
        val barSpacing = 4.dp.toPx()
        val barWidth = ((totalWidth - (barCount - 1) * barSpacing) / barCount).coerceAtLeast(3.dp.toPx())
        val centerY = canvasHeight / 2f

        val brush = Brush.horizontalGradient(barColorGradient)

        for (i in 0 until barCount) {
            val progress = i.toFloat() / barCount
            val wave = if (isSpeaking) {
                // Harmonic undulating wave
                val sin1 = sin(progress * 4f + phase)
                val sin2 = sin(progress * 8f - phase * 1.5f)
                val raw = (sin1 * 0.6f + sin2 * 0.4f + 1f) / 2f
                (0.2f + raw * 0.8f * (amplitude.coerceIn(0.2f, 1f)))
            } else {
                // Subtle resting idle vibration
                val sinVal = (sin(progress * 3f + phase) + 1f) / 2f
                0.15f + sinVal * 0.15f
            }

            val currentBarHeight = (canvasHeight * wave).coerceIn(4.dp.toPx(), canvasHeight)
            val left = i * (barWidth + barSpacing)
            val top = centerY - currentBarHeight / 2f

            drawRoundRect(
                brush = brush,
                topLeft = Offset(left, top),
                size = Size(barWidth, currentBarHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }
    }
}
