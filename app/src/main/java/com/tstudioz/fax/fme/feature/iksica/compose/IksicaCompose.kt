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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.greyishWhite
import com.tstudioz.fax.fme.feature.iksica.daysAgoText
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentData
import com.tstudioz.fax.fme.feature.iksica.roundToTwo
import com.tstudioz.fax.fme.feature.iksica.view.IksicaReceiptState
import com.tstudioz.fax.fme.feature.iksica.view.IksicaViewModel
import com.tstudioz.fax.fme.feature.iksica.view.IksicaViewState
import kotlinx.coroutines.InternalCoroutinesApi
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(
    InternalCoroutinesApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class
)
@Composable
fun IksicaCompose(iksicaViewModel: IksicaViewModel) {

    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    val receiptSelected = iksicaViewModel.receiptSelected.observeAsState().value
    val studentData = iksicaViewModel.studentData.observeAsState().value

    val viewState = iksicaViewModel.viewState.observeAsState().value ?: IksicaViewState.Loading
    val isRefreshing = viewState is IksicaViewState.Fetching || viewState is IksicaViewState.Loading
    val showPopup = remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(isRefreshing, { iksicaViewModel.getReceipts() })

    val marginTop = with(LocalDensity.current) { 0.dp.toPx() }
    var composableHeight by remember { mutableIntStateOf(0) }
    var offset by remember { mutableIntStateOf(0) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (listState.firstVisibleItemIndex == 0) {
                    offset = (offset + delta).coerceIn(marginTop, composableHeight.toFloat()).toInt()
                }
                return Offset(
                    0f, if (offset < composableHeight && offset > marginTop) delta else 0f
                )
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource) = Offset.Zero
        }
    }

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                iksicaViewModel.getReceipts()
            }

            else -> {}
        }
    }

    BottomSheetScaffold(sheetPeekHeight = 0.dp,
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
        when (viewState) {
            is IksicaViewState.Initial, is IksicaViewState.Empty, is IksicaViewState.FetchingError -> {
                TopBarIksica()
                Box(Modifier.fillMaxWidth()) {
                    PullRefreshIndicator(
                        isRefreshing, pullRefreshState, Modifier
                            .align(Alignment.TopCenter)
                            .zIndex(5f)
                    )
                }
                EmptyIksicaView(stringResource(id = R.string.iksica_no_data))
            }

            is IksicaViewState.Success, is IksicaViewState.Fetching -> {
                val model: StudentData =
                    (viewState as? IksicaViewState.Success)?.data ?: (viewState as? IksicaViewState.Fetching)?.data
                    ?: return@BottomSheetScaffold
                Box(
                    modifier = Modifier.nestedScroll(nestedScrollConnection)
                ) {
                    Column(Modifier.onGloballyPositioned {
                        if (composableHeight == 0) {
                            composableHeight = it.size.height
                            offset = composableHeight
                        }
                    }) {
                        TopBarIksica()
                        Box(Modifier.fillMaxWidth()) {
                            PullRefreshIndicator(
                                isRefreshing, pullRefreshState, Modifier
                                    .align(Alignment.TopCenter)
                                    .zIndex(5f)
                            )
                            ElevatedCardIksica(model.nameSurname, model.cardNumber, model.balance) {
                                showPopup.value = true
                            }
                        }
                    }
                    Column(modifier = Modifier.offset { IntOffset(0, offset) }) {
                        if (model.receipts.isEmpty()) {
                            EmptyIksicaView(stringResource(id = R.string.iksica_no_receipts))
                        }

                        TransakcijeText()

                        LazyColumn(state = listState) {
                            items(model.receipts) {
                                IksicaItem(it) {
                                    iksicaViewModel.getReceiptDetails(it)
                                }
                            }
                        }
                    }
                }
            }

            else -> {}
        }
    }

    PopupBox(showPopup = showPopup.value, onClickOutside = { showPopup.value = !showPopup.value }) {
        if (studentData != null) {
            CardIksicaPopupContent(studentData)
        }
    }
}

@Composable
fun TopBarIksica() {
    Row(
        modifier = Modifier.background(Color.Transparent)
    ) {
        Text(
            text = stringResource(id = R.string.tab_iksica),
            fontSize = 30.sp,
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 16.dp)
        )
    }
}

@Composable
fun TransakcijeText() {
    Text(
        text = stringResource(id = R.string.transactions),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp))
            .background(colorResource(R.color.chinese_black))
            .padding(20.dp, 30.dp, 16.dp, 24.dp)
    )
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
            text = text, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}


@Composable
fun IksicaItem(receipt: Receipt, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .background(colorResource(R.color.chinese_black))
            .clickable(onClick = onClick)
            .padding(16.dp, 5.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                receipt.restaurant.trim(), overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(0.80f)
            )
            Text(
                text = stringResource(
                    id = R.string.minus_amount,
                    receipt.subsidizedAmount.roundToTwo()
                ) + stringResource(id = R.string.currency),
                fontSize = 15.sp,
                modifier = Modifier.weight(0.20f),
                textAlign = TextAlign.End,
            )
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Row {
                val today = LocalDate.now()
                val daysAgo = ChronoUnit.DAYS.between(receipt.date, today).daysAgoText(LocalContext.current)
                Text(daysAgo, color = greyishWhite)
                Spacer(modifier = Modifier.width(2.dp))
                Text(receipt.time, color = greyishWhite)
            }
            Text(
                text = stringResource(id = R.string.receipt_details),
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                color = colorResource(id = R.color.brandeis_blue)
            )
        }
    }
    HorizontalDivider(Modifier.padding(horizontal = 10.dp))
}