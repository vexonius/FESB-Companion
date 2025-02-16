package com.tstudioz.fax.fme.feature.iksica.compose

import android.graphics.drawable.Icon
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.iksica.view.IksicaReceiptState
import com.tstudioz.fax.fme.feature.iksica.view.IksicaViewModel
import com.tstudioz.fax.fme.feature.iksica.view.IksicaViewState
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(InternalCoroutinesApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun IksicaCompose(iksicaViewModel: IksicaViewModel) {

    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val listState = rememberLazyListState()

    val receiptSelected = iksicaViewModel.receiptSelected.observeAsState().value
    val studentData = iksicaViewModel.studentData.observeAsState().value
    val viewState = iksicaViewModel.viewState.observeAsState().value ?: IksicaViewState.Loading

    val isRefreshing = viewState is IksicaViewState.Fetching || viewState is IksicaViewState.Loading
    val showPopup = remember { mutableStateOf(false) }

    val imageUrl = iksicaViewModel.imageUrl.observeAsState().value
    val imageName = iksicaViewModel.imageName.observeAsState().value

    val pullRefreshState = rememberPullRefreshState(isRefreshing, {
        iksicaViewModel.getReceipts()
    })

    LaunchedEffect(lifecycleState) { if (lifecycleState == Lifecycle.State.RESUMED) iksicaViewModel.getReceipts() }
    DisposableEffect(Unit) {
        onDispose {
            iksicaViewModel.closeImageMenza()
        }
    }

    if (imageName != null) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .background(colorResource(R.color.chinese_black))
                .zIndex(10F),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            /*Icon(
                Icons.AutoMirrored.Default.ArrowBack, "",
                Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { iksicaViewModel.closeImageMenza() }
                    .padding(7.dp)
                    .size(26.dp)
            )*/
            Card(
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .padding(24.dp, 53.dp, 24.dp, 24.dp)
            ) {
                imageUrl?.let { Rotatable90Image(imageUrl = it, contentDescription = "Menza") }
                //imageUrl?.let { RotatableZoomableImage(imageUrl = it, "Menza") }
            }
            MeniComposeIksica(iksicaViewModel.menza.observeAsState().value)
            BackHandler {
                iksicaViewModel.closeImageMenza()
            }
        }
    } else {

        BottomSheetScaffold(
            sheetPeekHeight = 0.dp,
            modifier = Modifier
                .padding(WindowInsets.navigationBars.asPaddingValues())
                .pullRefresh(pullRefreshState)
                .nestedScroll(TopAppBarDefaults.pinnedScrollBehavior().nestedScrollConnection),
            scaffoldState = scaffoldState,
            snackbarHost = { SnackbarHost(hostState = iksicaViewModel.snackbarHostState) },
            sheetContent = {
                if (receiptSelected is IksicaReceiptState.Success)
                    BottomSheetIksica(receiptSelected.data) { iksicaViewModel.hideReceiptDetails() }
            }) {
            when (viewState) {
                is IksicaViewState.Initial, is IksicaViewState.Empty -> {
                    EmptyIksicaView(isRefreshing, pullRefreshState)
                }

                is IksicaViewState.Success, is IksicaViewState.Fetching -> {
                    PopulatedIksicaView(
                        iksicaViewModel,
                        viewState,
                        showPopup,
                        listState,
                        isRefreshing,
                        pullRefreshState
                    )
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
}

@OptIn(ExperimentalMaterialApi::class, InternalCoroutinesApi::class)
@Composable
fun EmptyIksicaView(isRefreshing: Boolean, pullRefreshState: PullRefreshState) {
    Column {
        TopBarIksica()
        Box(Modifier.fillMaxWidth()) {
            PullRefreshIndicator(
                isRefreshing, pullRefreshState, Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(5f)
            )
            LazyColumn(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    EmptyIksicaView(stringResource(id = R.string.iksica_no_data))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, InternalCoroutinesApi::class)
@Composable
fun PopulatedIksicaView(
    iksicaViewModel: IksicaViewModel,
    viewState: IksicaViewState,
    showPopup: MutableState<Boolean>,
    listState: LazyListState,
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState
) {

    /*val locations = listOf(
        "kampus" to "Kampus",
        "fesb_vrh" to "FESB",
        "fesb_stop" to "STOP",
        "efst" to "Efst",
        "fgag" to "Fgag",
        "hostel" to "Hostel",
        "medicina" to "Medicina",
        "indeks" to "Indeks"
    )*/
    val sheetTopPadding = with(LocalDensity.current) { 0.dp.toPx() }
    var composableHeight by remember { mutableIntStateOf(0) }
    var sheetOffset by remember { mutableIntStateOf(0) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (listState.firstVisibleItemIndex == 0) {
                    sheetOffset =
                        (sheetOffset + delta).coerceIn(sheetTopPadding, composableHeight.toFloat()).toInt()
                }
                return Offset(
                    0f,
                    if (composableHeight > sheetOffset && sheetOffset > sheetTopPadding) delta else 0f
                )
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource) =
                Offset(0f, if (sheetOffset == 0) available.y else 0f)
        }
    }

    val model = when (viewState) {
        is IksicaViewState.Success -> viewState.data
        is IksicaViewState.Fetching -> viewState.data
        else -> return
    }
    Box(modifier = Modifier.nestedScroll(nestedScrollConnection)) {
        Column(Modifier.onGloballyPositioned {
            if (composableHeight == 0) {
                composableHeight = it.size.height
                sheetOffset = it.size.height
            }
        }) {
            TopBarIksica(Icons.Default.Call, iksicaViewModel)

            /*Row(
                modifier = Modifier
                    .padding(10.dp, 10.dp, 10.dp, 0.dp)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center,
            ) {
                locations.forEach { (ime, lokacija) ->

                    Box(modifier = Modifier
                        .padding(5.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF313231))
                        .clickable {
                            iksicaViewModel.runImageMenza(ime, lokacija)
                        }
                        .padding(10.dp))
                    {
                        Text(text = lokacija)
                    }
                }
            }
*/

            Box(Modifier.fillMaxWidth()) {
                PullRefreshIndicator(
                    isRefreshing, pullRefreshState, Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(5f)
                )
                ElevatedCardIksica(model.nameSurname, model.cardNumber, model.balance) { showPopup.value = true }
            }
        }
        Column(modifier = Modifier
            .offset { IntOffset(0, sheetOffset) }
            .clip(RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp))
            .background(colorResource(R.color.chinese_black))
        ) {
            if (model.receipts.isEmpty()) {
                EmptyIksicaView(stringResource(id = R.string.iksica_no_receipts))
            } else {
                TransakcijeText()
                LazyColumn(state = listState) {
                    items(model.receipts) {
                        IksicaItem(it) { iksicaViewModel.getReceiptDetails(it) }
                    }
                }
            }
        }
    }
}

@OptIn(InternalCoroutinesApi::class)
@Composable
fun TopBarIksica(
    icon: ImageVector? = null,
    iksicaViewModel: IksicaViewModel?=null,
) {
    val locations = listOf(
        "kampus" to "Kampus",
        "fesb_vrh" to "FESB",
        "fesb_stop" to "STOP",
        "efst" to "Efst",
        "fgag" to "Fgag",
        "hostel" to "Hostel",
        "medicina" to "Medicina",
        "indeks" to "Indeks"
    )
    Row(
        modifier = Modifier.background(Color.Transparent).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.tab_iksica),
            fontSize = 30.sp,
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 16.dp)
        )
        icon?.let {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.clickable {
                    iksicaViewModel?.runImageMenza(locations.first().first, locations.first().second)
                }
                    .padding(16.dp, 16.dp, 16.dp, 16.dp)
                    .size(30.dp), tint = Color.White
            )
        }
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
            .padding(20.dp, 30.dp, 16.dp, 24.dp)
    )
}

@Composable
fun EmptyIksicaView(text: String) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp, 100.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = text, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

/*
@Composable
fun IksicaItem(receipt: Receipt, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(16.dp, 5.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(receipt.restaurant.trim(), overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(0.80f))
            Text(
                text = "-" + receipt.subsidizedAmount.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
                    .toString() + "€",
                fontSize = 15.sp,
                modifier = Modifier.weight(0.20f),
                textAlign = TextAlign.End,
            )
        }
        Row {
            val today = LocalDate.now()
            val daysAgo = ChronoUnit.DAYS.between(receipt.date, today)

            val relativeText = when {
                daysAgo == 0L -> "Danas"
                daysAgo == 1L -> "Jučer"
                daysAgo > 1L && daysAgo % 10 == 1L && daysAgo % 100 != 11L -> "Prije $daysAgo dan"
                daysAgo > 1L -> "Prije $daysAgo dana"
                else -> "U budućnosti"
            }
            Text(relativeText + " " + receipt.time + " ", color = Color(0xFFCCCCCC))
        }
    }
    HorizontalDivider(Modifier.padding(horizontal = 10.dp))
}*/
