package com.tstudioz.fax.fme.feature.menza.view

import android.content.Context
import android.view.OrientationEventListener
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import com.tstudioz.fax.fme.util.GlideImage

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