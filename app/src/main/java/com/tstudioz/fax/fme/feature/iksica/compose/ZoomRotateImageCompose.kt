package com.tstudioz.fax.fme.feature.iksica.compose

import android.content.Context
import android.util.Log
import android.view.OrientationEventListener
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import com.tstudioz.fax.fme.util.GlideImage
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

    GlideImage(
        modifier = modifiableModifier
            .animateContentSize()
            .onGloballyPositioned {
                imageWidth.intValue = it.size.width
                imageHeight.intValue = it.size.height
            }
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
        model = imageUrl.toString()
    )

}

enum class Orientation { PORTRAIT, LANDSCAPE_LEFT, LANDSCAPE_RIGHT }

@Composable
fun Rotatable90Image(imageUrl: String?, contentDescription: String) {

    val ratio = 627 / 353f

    val scale = remember { mutableFloatStateOf(1f) }
    val rotationState = remember { mutableFloatStateOf(0f) }
    val aspectRatio = remember { mutableFloatStateOf(ratio) }
    val manualRotation = remember { mutableStateOf(false) }
    val orientation = remember { mutableStateOf(Orientation.PORTRAIT) }

    fun rotate(to: Orientation = orientation.value) {
        if (to == Orientation.PORTRAIT) {
            orientation.value = Orientation.PORTRAIT
            aspectRatio.floatValue = ratio
            rotationState.floatValue = 0f
            scale.floatValue = 1f
        } else {
            if (to == Orientation.LANDSCAPE_LEFT) {
                orientation.value = Orientation.LANDSCAPE_LEFT
                rotationState.floatValue = 90f
            } else {
                orientation.value = Orientation.LANDSCAPE_RIGHT
                rotationState.floatValue = -90f
            }
            aspectRatio.floatValue = 1 / ratio
            scale.floatValue = ratio
        }
    }
    if (!manualRotation.value) {
        DeviceOrientationListener(LocalContext.current, onOrientationChanged = { newOrientation ->
            rotate(to = newOrientation)
        })
    }

    GlideImage(model = imageUrl, contentDescription = contentDescription, modifier = Modifier
        .aspectRatio(aspectRatio.floatValue.coerceIn(1 / ratio..ratio))
        .animateContentSize()
        .graphicsLayer(
            scaleX = scale.floatValue,
            scaleY = scale.floatValue,
            rotationZ = rotationState.floatValue,
        )
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            manualRotation.value = !manualRotation.value
            rotate(
                if (manualRotation.value) Orientation.LANDSCAPE_LEFT
                else Orientation.PORTRAIT
            )
        })
}

@Composable
fun DeviceOrientationListener(applicationContext: Context, onOrientationChanged: (Orientation) -> Unit = {}) {

    DisposableEffect(Unit) {
        val orientationEventListener = object : OrientationEventListener(applicationContext) {
            override fun onOrientationChanged(orientation: Int) {
                if ((orientation >= 340 || orientation < 20)) {
                    onOrientationChanged(Orientation.PORTRAIT)
                } else if (orientation in 70..110) {
                    onOrientationChanged(Orientation.LANDSCAPE_RIGHT)
                } else if (orientation in 250..290) {
                    onOrientationChanged(Orientation.LANDSCAPE_LEFT)
                }
            }

            override fun enable() {
                super.enable()
                onOrientationChanged(Orientation.PORTRAIT)
            }
        }
        orientationEventListener.enable()

        onDispose {
            orientationEventListener.disable()
        }
    }
}