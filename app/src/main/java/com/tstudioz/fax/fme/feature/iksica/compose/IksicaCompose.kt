package com.tstudioz.fax.fme.feature.iksica.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.BalloonIndicatorType
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.iksica.menzaLocations
import com.tstudioz.fax.fme.feature.home.view.noRippleClickable
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentData
import com.tstudioz.fax.fme.feature.iksica.view.IksicaReceiptState
import com.tstudioz.fax.fme.feature.iksica.view.IksicaViewModel
import com.tstudioz.fax.fme.feature.iksica.view.IksicaViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(
    InternalCoroutinesApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
fun IksicaCompose(iksicaViewModel: IksicaViewModel) {

    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val listState = rememberLazyListState()
    val nestedSheetState = rememberNestedSheetState(composableHeight = 999, sheetOffset = 999)

    val receiptSelected = iksicaViewModel.receiptSelected.observeAsState().value
    val studentData = iksicaViewModel.studentData.observeAsState().value
    val viewState = iksicaViewModel.viewState.observeAsState().value ?: IksicaViewState.Loading

    val isRefreshing = viewState is IksicaViewState.Fetching || viewState is IksicaViewState.Loading
    val showPopup = remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(isRefreshing, { iksicaViewModel.getReceipts() })
    val imageUrl = iksicaViewModel.images.observeAsState().value
    val menzas = iksicaViewModel.menza.observeAsState().value

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) iksicaViewModel.getReceipts()

    }

    if (iksicaViewModel.menzaOpened.observeAsState().value == true) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val pageCount = menzaLocations.size
            val state = rememberPagerState(
                initialPage = (pageCount.div(2)),
                pageCount = { pageCount }
            )
            DisposableEffect(lifecycleState) {
                onDispose {
                    iksicaViewModel.closeMenza()
                    Log.d("IksicaCompose", "Menza image getting stopped")
                }
            }
            LaunchedEffect(state.settledPage) {
                iksicaViewModel.updateMenzaUrl(menzaLocations[state.settledPage])
            }
            Column {
                Row(
                    horizontalArrangement = Arrangement.Center, modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(R.color.chinese_black))
                        .padding(0.dp,24.dp,0.dp,24.dp)
                ) {
                    DotsIndicator(
                        dotCount = pageCount,
                        type = BalloonIndicatorType(
                            dotsGraphic = DotGraphic(
                                color = Color.White,
                                size = 6.dp
                            ),
                            balloonSizeFactor = 1.7f
                        ),
                        dotSpacing = 20.dp,
                        pagerState = state
                    )
                }
                HorizontalPager(state) {
                    val meni = menzas?.get(it)
                    ImageMeniView(iksicaViewModel, imageUrl, meni)
                }
            }
        }
        return
    }

    DisposableEffect(lifecycleState) {
        onDispose {
            if (lifecycleState == Lifecycle.State.CREATED) {
                coroutineScope.launch {
                    listState.scrollToItem(0)
                }
            }
        }
    }
    BottomSheetScaffold(
        sheetPeekHeight = 0.dp,
        modifier = Modifier
            .pullRefresh(pullRefreshState)
            .nestedScroll(TopAppBarDefaults.pinnedScrollBehavior().nestedScrollConnection),
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = iksicaViewModel.snackbarHostState) },
        sheetContent = {
            if (receiptSelected is IksicaReceiptState.Success)
                BottomSheetIksica(receiptSelected.data) { iksicaViewModel.hideReceiptDetails() }
        }) {
        Box(Modifier.fillMaxWidth()) {
            PullRefreshIndicator(
                isRefreshing, pullRefreshState, Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(5f)
            )

            when (viewState) {
                is IksicaViewState.Initial, is IksicaViewState.Empty -> EmptyIksicaView()
                is IksicaViewState.Success -> {
                    PopulatedIksicaView(
                        viewState.data,
                        listState,
                        iksicaViewModel,
                        nestedSheetState,
                        onCardClick = { showPopup.value = true },
                        onItemClick = { iksicaViewModel.getReceiptDetails(it) }
                    )
                }

                is IksicaViewState.Fetching -> {
                    PopulatedIksicaView(
                        viewState.data,
                        listState,
                        iksicaViewModel,
                        nestedSheetState,
                        onCardClick = { showPopup.value = true },
                        onItemClick = { iksicaViewModel.getReceiptDetails(it) }
                    )
                }

                else -> {}
            }
        }
    }

    PopupBox(showPopup = showPopup.value, onClickOutside = { showPopup.value = !showPopup.value }) {
        if (studentData != null) {
            CardIksicaPopupContent(studentData)
        }
    }
}

@OptIn(InternalCoroutinesApi::class)
@Composable
fun EmptyIksicaView() {
    Column {
        TopBarIksica()
        Box(Modifier.fillMaxWidth()) {
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

@OptIn(InternalCoroutinesApi::class)
@Composable
fun PopulatedIksicaView(
    model: StudentData,
    listState: LazyListState,
    iksicaViewModel: IksicaViewModel,
    nestedSheetState: NestedSheetState,
    onCardClick: () -> Unit,
    onItemClick: (Receipt) -> Unit
) {
    val sheetOffset = nestedSheetState.sheetOffset
    val sheetTopPadding = nestedSheetState.sheetTopPadding
    val composableHeight = nestedSheetState.composableHeight

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (listState.firstVisibleItemIndex == 0) {
                    sheetOffset.intValue =
                        (sheetOffset.intValue + delta).coerceIn(sheetTopPadding, composableHeight.intValue.toFloat())
                            .toInt()
                }
                return Offset(
                    0f,
                    if (composableHeight.intValue > sheetOffset.intValue && sheetOffset.intValue > sheetTopPadding) delta else 0f
                )
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ) =
                Offset(0f, if (sheetOffset.intValue == 0) available.y else 0f)
        }
    }

    Box(modifier = Modifier.nestedScroll(nestedScrollConnection)) {
        Column(Modifier.onGloballyPositioned {
            if (!nestedSheetState.set) {
                nestedSheetState.set = true
                composableHeight.intValue = it.size.height
                sheetOffset.intValue = it.size.height
            }
        }) {
            TopBarIksica(Icons.Default.Info, iksicaViewModel)

            ElevatedCardIksica(model.nameSurname, model.cardNumber, model.balance) { onCardClick() }
        }
        Column(
            modifier = Modifier
                .offset { IntOffset(0, sheetOffset.intValue) }
                .clip(RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp))
                .background(colorResource(R.color.chinese_black))
                .noRippleClickable {}
        ) {
            if (model.receipts.isEmpty()) {
                EmptyIksicaView(stringResource(id = R.string.iksica_no_receipts))
            } else {
                TransakcijeText()
                LazyColumn(Modifier.fillMaxSize(), state = listState) {
                    items(model.receipts) {
                        IksicaItem(it) { onItemClick(it) }
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
    iksicaViewModel: IksicaViewModel? = null,
) {
    Row(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth(),
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
                modifier = Modifier
                    .clickable {
                        with(iksicaViewModel) {
                            this?.openMenza()
                        }
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
            text = text,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}