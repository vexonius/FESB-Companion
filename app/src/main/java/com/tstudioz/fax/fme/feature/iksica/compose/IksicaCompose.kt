package com.tstudioz.fax.fme.feature.iksica.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.LinearProgressIndicator
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
import com.tstudioz.fax.fme.feature.iksica.IksicaViewModel
import com.tstudioz.fax.fme.feature.iksica.LoginStatus
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentDataIksica
import com.tstudioz.fax.fme.feature.iksica.repository.Status
import kotlinx.coroutines.InternalCoroutinesApi
import java.math.RoundingMode
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin


@OptIn(InternalCoroutinesApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun IksicaCompose(iksicaViewModel: IksicaViewModel) {

    val status = iksicaViewModel.status.observeAsState().value
    val loginStatus = iksicaViewModel.loginStatus.observeAsState().value
    val receiptsStatus = iksicaViewModel.receiptsStatus.observeAsState().value
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val isRefreshing = iksicaViewModel.isRefreshing.observeAsState(false).value
    val pullRefreshState = rememberPullRefreshState(isRefreshing, {
        iksicaViewModel.getReceipts(true)
    })
    val showPopup = remember { mutableStateOf(false) }

    BottomSheetScaffold(sheetPeekHeight = 0.dp,
        modifier = Modifier
            .pullRefresh(pullRefreshState)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = iksicaViewModel.snackbarHostState) },
        sheetContent = {
            if (iksicaViewModel.showItem.observeAsState(initial = false).value) {
                BottomSheetIksica(iksicaViewModel.itemToShow) { iksicaViewModel.toggleShowItem(it) }
            }
        }) {
        if (loginStatus != LoginStatus.SUCCESS) {
            LinearProgressIndicator(Modifier.fillMaxWidth().zIndex(2f))
        }
        Box {
            PullRefreshIndicator(
                isRefreshing, pullRefreshState, Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(2f)
            )
            if ((status == Status.FETCHING) && !isRefreshing) {
                CircularIndicator()
            }
            val list = iksicaViewModel.receipts.observeAsState().value
            if (iksicaViewModel.iksicaBalance.observeAsState().value != null && list != null){
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    item {
                        ElevatedCardIksica(
                            iksicaViewModel.studentDataIksica.observeAsState().value?.nameSurname ?: "",
                            iksicaViewModel.studentDataIksica.observeAsState().value?.iksicaNumber ?: "",
                            iksicaViewModel.iksicaBalance.observeAsState().value?.balance.toString(),
                        ) { showPopup.value = true }
                    }
                    if (list.isNotEmpty()) {
                        items(list) {
                            IksicaItem(it) {
                                iksicaViewModel.getReceiptDetails(it)
                            }
                        }
                    }
                    if (receiptsStatus == Status.EMPTY) {
                        item {
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
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularIndicator()
                }
            }
        }
        PopupBox(
            showPopup = showPopup.value,
            onClickOutside = { showPopup.value = !showPopup.value }
        ) {
            CardIksicaPopupContent(
                studentDataIksica = iksicaViewModel.studentDataIksica.observeAsState().value ?: StudentDataIksica(
                    nameSurname = "",
                    rightsLevel = "",
                    dailySupport = 0.0,
                    oib = "",
                    jmbag = "",
                    iksicaNumber = "",
                    rightsFrom = "",
                    rightsTo = ""
                ),
                iksicaBalance = iksicaViewModel.iksicaBalance.observeAsState().value?.balance ?: 0.0
            )

        }
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
                            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(10.dp))
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


fun Modifier.angledGradientBackground(colors: List<Color>, degrees: Float, halfHalf: Boolean = false) =
    drawBehind {
        /**
        Have to compute length of gradient vector so that it lies within
        the visible rectangle.
        --------------------------------------------
        | length of gradient ^  /                  |
        |             --->  /  /                   |
        |                  /  / <- rotation angle  |
        |                 /  o --------------------|  y
        |                /  /                      |
        |               /  /                       |
        |              v  /                        |
        --------------------------------------------
        x

        diagonal angle = atan2(y, x)
        (it's hard to draw the diagonal)

        Simply rotating the diagonal around the centre of the rectangle
        will lead to points outside the rectangle area. Further, just
        truncating the coordinate to be at the nearest edge of the
        rectangle to the rotated point will distort the angle.
        Let α be the desired gradient angle (in radians) and γ be the
        angle of the diagonal of the rectangle.
        The correct for the length of the gradient is given by:
        x/|cos(α)|  if -γ <= α <= γ,   or   π - γ <= α <= π + γ
        y/|sin(α)|  if  γ <= α <= π - γ, or π + γ <= α <= 2π - γ
        where γ ∈ (0, π/2) is the angle that the diagonal makes with
        the base of the rectangle.

         **/

        var deg2 = degrees

        val (x, y) = size
        val gamma = atan2(y, x)

        if (halfHalf) {
            deg2 = atan2(x, y).times(180f / PI).toFloat()
        }

        if (gamma == 0f || gamma == (PI / 2).toFloat()) {
            // degenerate rectangle
            return@drawBehind
        }

        val degreesNormalised = (deg2 % 360).let { if (it < 0) it + 360 else it }

        val alpha = (degreesNormalised * PI / 180).toFloat()

        val gradientLength = when (alpha) {
            // ray from centre cuts the right edge of the rectangle
            in 0f..gamma, in (2 * PI - gamma)..2 * PI -> {
                x / cos(alpha)
            }
            // ray from centre cuts the top edge of the rectangle
            in gamma..(PI - gamma).toFloat() -> {
                y / sin(alpha)
            }
            // ray from centre cuts the left edge of the rectangle
            in (PI - gamma)..(PI + gamma) -> {
                x / -cos(alpha)
            }
            // ray from centre cuts the bottom edge of the rectangle
            in (PI + gamma)..(2 * PI - gamma) -> {
                y / -sin(alpha)
            }
            // default case (which shouldn't really happen)
            else -> hypot(x, y)
        }

        val centerOffsetX = cos(alpha) * gradientLength / 2
        val centerOffsetY = sin(alpha) * gradientLength / 2

        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                // negative here so that 0 degrees is left -> right and 90 degrees is top -> bottom
                start = Offset(center.x - centerOffsetX, center.y - centerOffsetY),
                end = Offset(center.x + centerOffsetX, center.y + centerOffsetY)
            ),
            size = size
        )
    }