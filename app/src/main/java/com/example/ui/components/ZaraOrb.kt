package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.domain.model.VoiceAssistantState
import com.example.ui.theme.ZaraCyan
import com.example.ui.theme.ZaraElectricPurple
import com.example.ui.theme.ZaraPink
import com.example.ui.theme.ZaraPurple
import com.example.ui.theme.ZaraRed
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ZaraOrb(
    state: VoiceAssistantState,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "zara_orb_anim")

    // Breathing pulse for Idle
    val idlePulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_pulse"
    )

    // Continuous rotation for Processing
    val processingRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing)
        ),
        label = "processing_rot"
    )

    // Active wave expansion for Listening / Speaking
    val activeRipple by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "active_ripple"
    )

    // Amplitude smoothing for reactive speech
    val amplitudeAnim = remember { Animatable(0f) }
    LaunchedEffect(state) {
        val targetAmp = when (state) {
            is VoiceAssistantState.Listening -> state.amplitude.coerceIn(0.1f, 1f)
            is VoiceAssistantState.Speaking -> state.amplitude.coerceIn(0.2f, 1f)
            else -> 0f
        }
        amplitudeAnim.animateTo(
            targetValue = targetAmp,
            animationSpec = tween(150, easing = FastOutSlowInEasing)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .testTag("zara_orb")
            .clickable(onClick = onClick)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = (this.size.minDimension / 2f) * 0.72f

            when (state) {
                is VoiceAssistantState.Idle -> {
                    val radius = baseRadius * idlePulse

                    // Outer soft glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ZaraPurple.copy(alpha = 0.35f),
                                ZaraCyan.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = radius * 1.4f
                        ),
                        radius = radius * 1.4f,
                        center = center
                    )

                    // Core sphere
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFE0E7FF),
                                ZaraElectricPurple,
                                Color(0xFF130E2E)
                            ),
                            center = Offset(center.x - radius * 0.25f, center.y - radius * 0.25f),
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )

                    // Subtle delicate ring
                    drawCircle(
                        color = ZaraCyan.copy(alpha = 0.4f),
                        radius = radius * 1.08f,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                is VoiceAssistantState.Listening -> {
                    val amp = amplitudeAnim.value
                    val expandedRadius = baseRadius * (activeRipple + amp * 0.25f)

                    // Energetic cyan aura
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ZaraCyan.copy(alpha = 0.6f),
                                Color(0xFF0284C7).copy(alpha = 0.3f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = expandedRadius * 1.45f
                        ),
                        radius = expandedRadius * 1.45f,
                        center = center
                    )

                    // Double outer energetic rings
                    drawCircle(
                        color = ZaraCyan.copy(alpha = 0.7f),
                        radius = expandedRadius * 1.15f,
                        center = center,
                        style = Stroke(width = (2.5f + amp * 4f).dp.toPx())
                    )
                    drawCircle(
                        color = ZaraPurple.copy(alpha = 0.5f),
                        radius = expandedRadius * 1.3f,
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // Core bright cyan & white sphere
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                ZaraCyan,
                                Color(0xFF0369A1),
                                Color(0xFF082F49)
                            ),
                            center = Offset(center.x - baseRadius * 0.2f, center.y - baseRadius * 0.2f),
                            radius = baseRadius
                        ),
                        radius = baseRadius,
                        center = center
                    )
                }

                is VoiceAssistantState.Processing -> {
                    // Rotating gradient ring
                    rotate(processingRotation, pivot = center) {
                        drawCircle(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    ZaraCyan,
                                    ZaraPurple,
                                    ZaraPink,
                                    ZaraCyan
                                )
                            ),
                            radius = baseRadius * 1.18f,
                            center = center,
                            style = Stroke(width = 4.dp.toPx())
                        )
                    }

                    // Rotating inner accent
                    rotate(-processingRotation * 1.4f, pivot = center) {
                        drawCircle(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    ZaraPurple,
                                    Color.Transparent,
                                    ZaraCyan,
                                    Color.Transparent
                                )
                            ),
                            radius = baseRadius * 1.32f,
                            center = center,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    // Core sphere
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFF3E8FF),
                                ZaraPurple,
                                Color(0xFF3B0764),
                                Color(0xFF130E2E)
                            ),
                            center = Offset(center.x - baseRadius * 0.2f, center.y - baseRadius * 0.2f),
                            radius = baseRadius
                        ),
                        radius = baseRadius,
                        center = center
                    )
                }

                is VoiceAssistantState.Speaking -> {
                    val amp = amplitudeAnim.value
                    val speakRadius = baseRadius * (1f + amp * 0.2f)

                    // Multiple pulsating harmonic rings
                    for (i in 1..3) {
                        val ringRad = speakRadius + (i * 12.dp.toPx()) * activeRipple
                        drawCircle(
                            color = if (i % 2 == 0) ZaraPurple.copy(alpha = 0.5f / i) else ZaraPink.copy(alpha = 0.6f / i),
                            radius = ringRad,
                            center = center,
                            style = Stroke(width = (2f + amp * 3f).dp.toPx())
                        )
                    }

                    // Core glowing magenta-purple sphere
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                ZaraPink,
                                ZaraPurple,
                                Color(0xFF2E1065)
                            ),
                            center = Offset(center.x - baseRadius * 0.2f, center.y - baseRadius * 0.2f),
                            radius = baseRadius
                        ),
                        radius = baseRadius,
                        center = center
                    )
                }

                is VoiceAssistantState.Error -> {
                    // Red cautionary glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ZaraRed.copy(alpha = 0.4f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = baseRadius * 1.3f
                        ),
                        radius = baseRadius * 1.3f,
                        center = center
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFEE2E2),
                                ZaraRed,
                                Color(0xFF450A0A)
                            ),
                            center = Offset(center.x - baseRadius * 0.2f, center.y - baseRadius * 0.2f),
                            radius = baseRadius
                        ),
                        radius = baseRadius,
                        center = center
                    )

                    drawCircle(
                        color = ZaraRed.copy(alpha = 0.8f),
                        radius = baseRadius * 1.1f,
                        center = center,
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }
            }
        }
    }
}
