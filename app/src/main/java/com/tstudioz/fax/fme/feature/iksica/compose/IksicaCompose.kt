package com.tstudioz.fax.fme.feature.iksica.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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

    val imageUrl = iksicaViewModel.imageUrl.observeAsState().value
    val imageName = iksicaViewModel.imageName.observeAsState().value

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
    DisposableEffect(Unit) {
        onDispose {
            iksicaViewModel.closeImageMenza()
        }
    }

    if (imageName != null) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .background(Color(0xFF1E1E1E))
                .zIndex(10F),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //imageUrl?.let { RotatableZoomableImage(imageUrl = it, "Menza") }
            imageUrl?.let { Rotatable90Image(imageUrl = it, contentDescription = "Menza") }
            MeniComposeIksica(iksicaViewModel.menza.observeAsState().value)
            BackHandler {
                iksicaViewModel.closeImageMenza()
            }
        }
    } else {
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Row(
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

                            item {
                                if (model.receipts.isEmpty()) {
                                    EmptyIksicaView(stringResource(id = R.string.iksica_no_receipts))
                                }
                            }

                            item {
                                Text(
                                    text = "Transakcije",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(16.dp, 12.dp)
                                )
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
}