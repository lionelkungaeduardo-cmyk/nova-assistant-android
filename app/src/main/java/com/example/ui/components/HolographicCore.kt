package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ai.AIState
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun HolographicCore(
    aiState: AIState,
    rmsLevel: Float = 0f,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    isPowerSaving: Boolean = false,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "CoreInfinite")

    // Rotation animations
    val rotationAngle1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isPowerSaving) 12000 else 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rot1"
    )

    val rotationAngle2 by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isPowerSaving) 16000 else 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rot2"
    )

    // Pulse animation based on state
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (aiState) {
                    AIState.LISTENING -> 400
                    AIState.THINKING -> 600
                    AIState.SPEAKING -> 500
                    AIState.EXECUTING -> 350
                    else -> 2000
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    // Dynamic state colors
    val corePrimaryColor = when (aiState) {
        AIState.IDLE -> NovaCyan
        AIState.LISTENING -> NovaLaserGreen
        AIState.THINKING -> NovaViolet
        AIState.SPEAKING -> NovaBlue
        AIState.EXECUTING -> NovaCyberAmber
        AIState.OFFLINE -> NovaTextMuted
        AIState.ERROR -> NovaNeonPink
    }

    val coreSecondaryColor = when (aiState) {
        AIState.IDLE -> NovaViolet
        AIState.LISTENING -> NovaCyan
        AIState.THINKING -> NovaCyan
        AIState.SPEAKING -> NovaLaserGreen
        AIState.EXECUTING -> NovaNeonPink
        AIState.OFFLINE -> NovaVoidBlack
        AIState.ERROR -> NovaCyberAmber
    }

    // Static seeded particle offsets
    val particles = remember {
        List(if (isPowerSaving) 10 else 24) {
            val angle = Random.nextFloat() * 360f
            val distance = Random.nextFloat() * 0.8f + 0.2f
            val particleSize = Random.nextFloat() * 3f + 1.5f
            val speed = Random.nextFloat() * 0.5f + 0.5f
            Triple(angle, distance, Pair(particleSize, speed))
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = this.size.width
            val canvasHeight = this.size.height
            val center = Offset(canvasWidth / 2f, canvasHeight / 2f)
            val baseRadius = (canvasWidth / 2f) * 0.46f * pulseScale

            // 1. Ambient Frosted Blue-Cyan Glow Backdrop
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        corePrimaryColor.copy(alpha = 0.28f),
                        coreSecondaryColor.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.9f
                ),
                radius = baseRadius * 1.9f,
                center = center
            )

            // 2. Subtle Outer Frosted Glass Rings
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = baseRadius * 1.45f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                color = corePrimaryColor.copy(alpha = 0.12f),
                radius = baseRadius * 1.28f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // 3. Audio reaction wave if listening/speaking
            if (aiState == AIState.LISTENING || aiState == AIState.SPEAKING) {
                val reactiveRadius = baseRadius * (1.25f + rmsLevel * 0.5f)
                drawCircle(
                    color = corePrimaryColor.copy(alpha = 0.45f),
                    radius = reactiveRadius,
                    center = center,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                    )
                )
            }

            // 4. Gradient Glowing Perimeter Ring (Cyan -> Blue -> Indigo)
            rotate(degrees = rotationAngle1, pivot = center) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(
                            corePrimaryColor,
                            NovaBlue,
                            coreSecondaryColor,
                            corePrimaryColor.copy(alpha = 0.2f),
                            corePrimaryColor
                        ),
                        center = center
                    ),
                    radius = baseRadius * 1.12f,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // 5. Opposite Orbital Glass Ring with Dash & Dot
            rotate(degrees = rotationAngle2, pivot = center) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(
                            coreSecondaryColor.copy(alpha = 0.8f),
                            Color.Transparent,
                            corePrimaryColor.copy(alpha = 0.8f),
                            Color.White.copy(alpha = 0.3f),
                            coreSecondaryColor.copy(alpha = 0.8f)
                        ),
                        center = center
                    ),
                    radius = baseRadius * 0.98f,
                    center = center,
                    style = Stroke(
                        width = 1.2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 8f), 0f)
                    )
                )
            }

            // 6. Floating Orbital Glow Micro-Particles
            if (!isPowerSaving) {
                particles.forEach { (initialAngle, distRatio, sizeSpeed) ->
                    val (pSize, speed) = sizeSpeed
                    val currentAngle = (initialAngle + rotationAngle1 * speed) * (Math.PI / 180f)
                    val pDist = baseRadius * (distRatio + (if (aiState == AIState.LISTENING) rmsLevel * 0.25f else 0f))
                    val px = center.x + (pDist * cos(currentAngle)).toFloat()
                    val py = center.y + (pDist * sin(currentAngle)).toFloat()

                    drawCircle(
                        color = corePrimaryColor.copy(alpha = 0.7f),
                        radius = pSize.dp.toPx(),
                        center = Offset(px, py)
                    )
                }
            }

            // 7. Frosted Glass Inner Sphere
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x22FFFFFF), // 13% white glass
                        Color(0x0A06B6D4), // soft cyan tint
                        Color(0xFF02040A)
                    ),
                    center = center,
                    radius = baseRadius * 0.75f
                ),
                radius = baseRadius * 0.75f,
                center = center
            )

            // Inner glass rim
            drawCircle(
                color = Color.White.copy(alpha = 0.18f),
                radius = baseRadius * 0.75f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // 8. Frosted Core Center Capsule & Glowing Nucleus
            drawCircle(
                color = Color(0x1AFFFFFF),
                radius = baseRadius * 0.38f,
                center = center
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.25f),
                radius = baseRadius * 0.38f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // Glowing Core Nucleus Dot
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        Color.White,
                        corePrimaryColor,
                        corePrimaryColor.copy(alpha = 0.4f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 0.18f
                ),
                radius = baseRadius * 0.18f,
                center = center
            )
        }
    }
}
