package com.tstudioz.fax.fme.feature.iksica.compose

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import okhttp3.HttpUrl
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RotatableZoomableImage(imageUrl: HttpUrl, contentDescription: String) {
    val scale = remember { mutableFloatStateOf(1f) }
    val rotationState = remember { mutableFloatStateOf(0f) }
    val offsetX = remember { mutableFloatStateOf(0f) }
    val offsetY = remember { mutableFloatStateOf(0f) }
    val aspectRatio = remember { mutableFloatStateOf(16 / 9f) }
    val imageWidth = remember { mutableIntStateOf(0) }
    val imageHeight = remember { mutableIntStateOf(0) }

    val modifiableModifier = Modifier.aspectRatio(aspectRatio.floatValue.coerceIn(9 / 16f..16 / 9f))

    AsyncImage(
        modifier = modifiableModifier
            .animateContentSize()
            .graphicsLayer(
                scaleX = maxOf(.5f, minOf(3f, scale.floatValue)),
                scaleY = maxOf(.5f, minOf(3f, scale.floatValue)),
                rotationZ = rotationState.floatValue,
                translationX = offsetX.floatValue,
                translationY = offsetY.floatValue
            )
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    if (rotationState.floatValue + rotation in -180f..180f) rotationState.floatValue += rotation
                    if (scale.floatValue * zoom in 1f..3f) scale.floatValue *= zoom

                    val cosRotAngle = cos(rotationState.floatValue * PI / 180)
                    val sinRotAngle = sin(rotationState.floatValue * PI / 180)

                    val scaledPanX = (pan.x * cosRotAngle - pan.y * sinRotAngle).toFloat() * scale.floatValue
                    val scaledPanY = (pan.x * sinRotAngle + pan.y * cosRotAngle).toFloat() * scale.floatValue

                    val rotatedWidth =
                        (abs(cosRotAngle * imageWidth.intValue) + abs(sinRotAngle * imageHeight.intValue)).toFloat()
                    val rotatedHeight =
                        (abs(sinRotAngle * imageWidth.intValue) + abs(cosRotAngle * imageHeight.intValue)).toFloat()

                    val extraWidth = 0.12f

                    val scaledWidth = rotatedWidth * (scale.floatValue - 1 + extraWidth) / 2
                    val scaledHeight = rotatedHeight * (scale.floatValue - 1 + extraWidth) / 2

                    aspectRatio.floatValue = scaledWidth / scaledHeight / scale.floatValue

                    if (scale.floatValue > 1) {
                        offsetX.floatValue = (offsetX.floatValue + scaledPanX).coerceIn(-scaledWidth..scaledWidth)
                        offsetY.floatValue = (offsetY.floatValue + scaledPanY).coerceIn(-scaledHeight..scaledHeight)
                    } else {
                        offsetX.floatValue = 0f
                        offsetY.floatValue = 0f
                    }
                }
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                scale.floatValue = 1f
                offsetX.floatValue = 0f
                offsetY.floatValue = 0f
                rotationState.floatValue = 0f
                aspectRatio.floatValue = 16 / 9f
            },
        contentDescription = contentDescription,
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .listener(
                onSuccess = { _, result ->
                    (result.drawable as? BitmapDrawable)?.bitmap?.let {
                        imageWidth.intValue = it.width
                        imageHeight.intValue = it.height
                    }
                },
                onError = { _, _ ->
                    imageWidth.intValue = 0
                    imageHeight.intValue = 0
                }
            )
            .build()
    )

}

@Composable
fun Rotatable90Image(imageUrl: HttpUrl, contentDescription: String) {
    val scale = remember { mutableFloatStateOf(1f) }
    val rotationState = remember { mutableFloatStateOf(0f) }
    val aspectRatio = remember { mutableFloatStateOf(16 / 9f) }

    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = Modifier
            .aspectRatio(aspectRatio.floatValue.coerceIn(9 / 16f..16 / 9f))
            .animateContentSize()
            .graphicsLayer(
                scaleX = maxOf(.5f, minOf(3f, scale.floatValue)),
                scaleY = maxOf(.5f, minOf(3f, scale.floatValue)),
                rotationZ = rotationState.floatValue,
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (aspectRatio.floatValue != 16 / 9f)
                    aspectRatio.floatValue = 16 / 9f
                else
                    aspectRatio.floatValue = 9 / 16f
                if (rotationState.floatValue == 0f)
                    rotationState.floatValue = 90f
                else
                    rotationState.floatValue = 0f
                if (scale.floatValue == 1f)
                    scale.floatValue = 1.77f
                else
                    scale.floatValue = 1f
            }
    )
}