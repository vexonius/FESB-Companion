package com.tstudioz.fax.fme.feature.iksica.compose

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentData
import com.tstudioz.fax.fme.feature.iksica.view.IksicaReceiptState
import com.tstudioz.fax.fme.feature.iksica.view.IksicaViewModel
import com.tstudioz.fax.fme.feature.iksica.view.IksicaViewState
import kotlinx.coroutines.InternalCoroutinesApi
import java.math.RoundingMode
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@OptIn(
    InternalCoroutinesApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
fun IksicaCompose(iksicaViewModel: IksicaViewModel) {

    val scaffoldState = rememberBottomSheetScaffoldState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val receiptSelected = iksicaViewModel.receiptSelected.observeAsState().value
    val studentData = iksicaViewModel.studentData.observeAsState().value

    val viewState = iksicaViewModel.viewState.observeAsState().value ?: IksicaViewState.Loading
    val isRefreshing = viewState is IksicaViewState.Fetching || viewState is IksicaViewState.Loading

    val showPopup = remember { mutableStateOf(false) }

    val shownCamera = remember { mutableStateOf("Kampus") }
    val image = iksicaViewModel.image.observeAsState().value

    val pullRefreshState = rememberPullRefreshState(isRefreshing, {
        iksicaViewModel.getReceipts()
    })

    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                iksicaViewModel.getReceipts()
            }
            else -> {}
        }
    }

    BottomSheetScaffold(
        sheetPeekHeight = 0.dp,
        modifier = Modifier
            .pullRefresh(pullRefreshState)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = iksicaViewModel.snackbarHostState) },
        sheetContent = {
            if (receiptSelected is IksicaReceiptState.Success) {
                BottomSheetIksica(receiptSelected.data) { iksicaViewModel.hideReceiptDetails() }
            }
        }) {
        Box {
            PullRefreshIndicator(
                isRefreshing,
                pullRefreshState,
                Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(2f)
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Row(
                        modifier = Modifier
                            .padding(10.dp, 10.dp, 10.dp, 0.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            iksicaViewModel.getImage("b8_27_eb_aa_ed_1c/")
                            iksicaViewModel.hideImage()
                            shownCamera.value = "Kampus"
                        }) {
                            Text("Kampus")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(onClick = {
                            iksicaViewModel.getImage("b8_27_eb_d1_4b_4a/")
                            iksicaViewModel.hideImage()
                            shownCamera.value = "FESB"
                        }) {
                            Text("FESB")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(onClick = {
                            iksicaViewModel.getImage("b8_27_eb_ac_55_f5/")
                            iksicaViewModel.hideImage()
                            shownCamera.value = "STOP"
                        }) {
                            Text("STOP")
                        }
                    }
                }
                when (viewState) {
                    is IksicaViewState.Initial, is IksicaViewState.Empty, is IksicaViewState.FetchingError -> {
                        item {
                            EmptyIksicaView(stringResource(id = R.string.iksica_no_data))
                        }
                    }

                    is IksicaViewState.Success, is IksicaViewState.Fetching -> {
                        val model: StudentData = (viewState as? IksicaViewState.Success)?.data
                            ?: (viewState as? IksicaViewState.Fetching)?.data ?: return@LazyColumn

                        item {
                            ElevatedCardIksica(model.nameSurname, model.cardNumber, model.balance) {
                                showPopup.value = true
                            }
                        }

                        if (model.receipts.isEmpty()) {
                            item {
                                EmptyIksicaView(stringResource(id = R.string.iksica_no_receipts))
                            }
                        }

                        items(model.receipts) {
                            IksicaItem(it) {
                                iksicaViewModel.getReceiptDetails(it)
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
    if (image != null) {
        ZoomablePopup(iksicaViewModel, image, "Menza", shownCamera)
    }
    PopupBox(
        showPopup = showPopup.value,
        onClickOutside = { showPopup.value = !showPopup.value }
    ) {
        if (studentData != null) {
            CardIksicaPopupContent(studentData)
        }
    }
}

@Composable
fun EmptyIksicaView(text: String) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun PopupBox(
    showPopup: Boolean,
    onClickOutside: () -> Unit,
    content: @Composable() () -> Unit
) {
    if (showPopup) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                .zIndex(10F)
                .clickable {}, // da ne bi klinkilo kroz popup background na stvari ispod
            contentAlignment = Alignment.Center
        ) {
            // popup
            Popup(
                alignment = Alignment.Center,
                properties = PopupProperties(
                    excludeFromSystemGesture = true,
                ),
                // to dismiss on click outside
                onDismissRequest = { onClickOutside() },
            ) {
                Box(
                    modifier = Modifier
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp))
                ) {
                    Box(
                        Modifier
                            .wrapContentSize(align = Alignment.Center)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clip(RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
fun IksicaItem(receipt: Receipt, onClick: () -> Unit) {
    Column {
        HorizontalDivider()
        ListItem(
            modifier = Modifier.clickable(onClick = onClick),
            headlineContent = { Text(receipt.restaurant, overflow = TextOverflow.Ellipsis) },
            supportingContent = {
                Text(receipt.dateString + " " + receipt.time + " ")
            },
            overlineContent = { Text(receipt.authorised) },
            trailingContent = {
                Text(
                    text = receipt.paidAmount.toBigDecimal()
                        .setScale(2, RoundingMode.HALF_EVEN).toString() + " €",
                    fontSize = 15.sp,
                )
            })
    }
}

@OptIn(InternalCoroutinesApi::class)
@Composable
fun ZoomablePopup(
    iksicaViewModel: IksicaViewModel,
    image: Bitmap,
    contentDescription: String,
    shownCamera: MutableState<String>
) {
    val bitmap = image.asImageBitmap()
    val scale = remember { mutableFloatStateOf(1f) }
    val rotationState = remember { mutableFloatStateOf(0f) }
    val offsetX = remember { mutableFloatStateOf(0f) }
    val offsetY = remember { mutableFloatStateOf(0f) }
    val imageWidth = bitmap.width
    val imageHeight = bitmap.height
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
            .zIndex(10F)
            .clickable {}, // da ne bi klinkilo kroz popup background na stvari ispod

    ) {

        Popup(
            alignment = Alignment.Center, onDismissRequest = {
                iksicaViewModel.hideImage()
            }) {

            Box(
                Modifier
                    .wrapContentSize()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceDim, RectangleShape)
                    .paint(painterResource(id = R.drawable.tile_background__1_), contentScale = ContentScale.Crop, alpha = 0.5f),
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(15.dp, 15.dp, 15.dp, 0.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = shownCamera.value,
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            modifier = Modifier
                                .size(25.dp)
                                .clickable {
                                    iksicaViewModel.hideImage()
                                }, tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Image(
                        modifier = Modifier
                            .clip(RectangleShape)
                            .aspectRatio(1f)
                            .graphicsLayer(
                                scaleX = maxOf(.5f, minOf(3f, scale.floatValue)),
                                scaleY = maxOf(.5f, minOf(3f, scale.floatValue)),
                                rotationZ = rotationState.floatValue,
                                translationX = offsetX.floatValue,
                                translationY = offsetY.floatValue
                            )
                            .pointerInput(Unit) {
                                detectTransformGestures { centroid, pan, zoom, rotation ->
                                    if (rotationState.floatValue + rotation in -180f..180f)
                                        rotationState.floatValue += rotation
                                    val rotationAngle = rotationState.floatValue * PI / 180
                                    val pany =
                                        (pan.x * sin(rotationAngle) + pan.y * cos(rotationAngle)) * scale.floatValue
                                    val panx =
                                        (pan.x * cos(rotationAngle) - pan.y * sin(rotationAngle)) * scale.floatValue
                                    val imageWIDTH =
                                        (abs(cos(rotationState.floatValue * PI / 180) * imageWidth) + abs(
                                            sin(
                                                rotationState.floatValue * PI / 180
                                            ) * imageHeight
                                        )).toFloat()
                                    val imageHEIGHT =
                                        (abs(sin(rotationState.floatValue * PI / 180) * imageWidth) + abs(
                                            cos(
                                                rotationState.floatValue * PI / 180
                                            ) * imageHeight
                                        )).toFloat()
                                    if (scale.floatValue * zoom in 1f..3f) scale.floatValue *= zoom
                                    if (scale.floatValue > 1) {
                                        offsetX.floatValue =
                                            (offsetX.floatValue + panx.toFloat()).coerceIn(-(imageWIDTH * (scale.floatValue - 1) / 2)..(imageWIDTH * (scale.floatValue - 1) / 2))
                                        offsetY.floatValue =
                                            (offsetY.floatValue + pany.toFloat()).coerceIn(-(imageHEIGHT * (scale.floatValue - 1) / 2)..(imageHEIGHT * (scale.floatValue - 1) / 2))
                                    } else {
                                        offsetX.floatValue = 0f
                                        offsetY.floatValue = 0f
                                    }
                                }
                            }
                            .clickable {
                                scale.floatValue = 1f
                                rotationState.floatValue = 0f
                                offsetX.floatValue = 0f
                                offsetY.floatValue = 0f
                            },
                        contentDescription = contentDescription,
                        bitmap = bitmap
                    )
                }
            }

        }
    }

}

fun Modifier.angledGradientBackground(
    colors: List<Color>,
    degrees: Float,
    halfHalf: Boolean = false
) =
    drawBehind {
        var deg2 = degrees

        val (x, y) = size
        val gamma = atan2(y, x)

        if (halfHalf) {
            deg2 = atan2(x, y).times(180f / PI).toFloat()
        }

        if (gamma == 0f || gamma == (PI / 2).toFloat()) {
            return@drawBehind
        }

        val degreesNormalised = (deg2 % 360).let { if (it < 0) it + 360 else it }

        val alpha = (degreesNormalised * PI / 180).toFloat()

        val gradientLength = when (alpha) {
            in 0f..gamma, in (2 * PI - gamma)..2 * PI -> {
                x / cos(alpha)
            }

            in gamma..(PI - gamma).toFloat() -> {
                y / sin(alpha)
            }

            in (PI - gamma)..(PI + gamma) -> {
                x / -cos(alpha)
            }

            in (PI + gamma)..(2 * PI - gamma) -> {
                y / -sin(alpha)
            }

            else -> hypot(x, y)
        }

        val centerOffsetX = cos(alpha) * gradientLength / 2
        val centerOffsetY = sin(alpha) * gradientLength / 2

        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(center.x - centerOffsetX, center.y - centerOffsetY),
                end = Offset(center.x + centerOffsetX, center.y + centerOffsetY)
            ),
            size = size
        )
    }