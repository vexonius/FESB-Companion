package com.tstudioz.fax.fme.feature.iksica.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.tstudioz.fax.fme.compose.CircularIndicator
import com.tstudioz.fax.fme.feature.iksica.view.IksicaViewModel
import com.tstudioz.fax.fme.feature.iksica.view.IksicaViewState
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentData
import kotlinx.coroutines.InternalCoroutinesApi
import java.math.RoundingMode
import kotlin.math.PI
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

    val itemToShow = iksicaViewModel.itemToShow.observeAsState().value
    val isRefreshing = iksicaViewModel.isRefreshing.observeAsState(false).value
    val studentName = iksicaViewModel.studentData.observeAsState().value?.nameSurname ?: ""
    val cardNumber = iksicaViewModel.studentData.observeAsState().value?.iksicaNumber ?: ""
    val cardBalance = iksicaViewModel.iksicaBalance.observeAsState().value?.balance ?: 0.0
    val studentData = iksicaViewModel.studentData.observeAsState().value ?: StudentData.empty

    val list = iksicaViewModel.receipts.observeAsState().value ?: emptyList()
    val viewState = iksicaViewModel.viewState.observeAsState().value ?: IksicaViewState.Loading

    val showPopup = remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(isRefreshing, {
        iksicaViewModel.getReceipts()
    })

//    Insted of using multiple live data vars to observe state, with viewState model,
//    developer can design UI based on specific state that screen is in
//
//    when (viewState) {
//        is IksicaViewState.Initial -> {}
//        is IksicaViewState.Loading -> {}
//        is IksicaViewState.Success -> {}
//        is IksicaViewState.Error -> {}
//        is IksicaViewState.Empty -> {}
//    }

    BottomSheetScaffold(sheetPeekHeight = 0.dp,
        modifier = Modifier
            .pullRefresh(pullRefreshState)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = iksicaViewModel.snackbarHostState) },
        sheetContent = {
            itemToShow?.let {
                BottomSheetIksica(it) { iksicaViewModel.hideReceiptDetails() }
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (list.isNotEmpty()) {
                    item {
                        ElevatedCardIksica(studentName, cardNumber, cardBalance.toString()) {
                            showPopup.value = true
                        }
                    }
                    items(list) {
                        IksicaItem(it) {
                            iksicaViewModel.getReceiptDetails(it)
                        }
                    }
                } else {
                    item {
                        EmptyIksicaView()
                    }
                }
            }
        }
    }
    PopupBox(
        showPopup = showPopup.value,
        onClickOutside = { showPopup.value = !showPopup.value }
    ) {
        CardIksicaPopupContent(studentData = studentData, iksicaBalance = cardBalance)
    }

}

@Composable
fun EmptyIksicaView() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Nema računa u zadnjih 30 dana",
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
        ListItem(modifier = Modifier.clickable(onClick = onClick),
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