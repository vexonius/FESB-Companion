package com.tstudioz.fax.fme.compose

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.database.models.ReceiptItem
import com.tstudioz.fax.fme.viewmodel.IksicaViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.java.KoinJavaComponent.inject
import java.time.LocalDate

@OptIn(InternalCoroutinesApi::class, ExperimentalMaterial3Api::class)
@Composable
fun IksicaCompose() {

    val iksicaViewModel: IksicaViewModel by inject(IksicaViewModel::class.java)

    val scaffoldState = rememberBottomSheetScaffoldState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BottomSheetScaffold(sheetPeekHeight = 0.dp,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        scaffoldState = scaffoldState,
        sheetContent = {
            if (iksicaViewModel.showItem.observeAsState(initial = false).value) {
                val receipt = iksicaViewModel.itemToShow.observeAsState().value
                val sheetState = rememberModalBottomSheetState()
                ModalBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = { iksicaViewModel.toggleShowItem(false) },
                ) {
                    IksicaReceiptDetailed(receipt)
                }
            }
        }) {
        val list = iksicaViewModel.receipts.observeAsState().value
        if (!list.isNullOrEmpty()) {
            LazyColumn {
                items(list) {
                    IksicaItem(it) {
                        iksicaViewModel.getRacun(it)
                    }
                }
            }
        } else {
            IksicaLoading(iksicaViewModel.loadingTxt.observeAsState().value ?: "Loading...")
        }
    }
}

@Preview
@Composable
fun ElevatedCardIksica() {
    ElevatedCard(Modifier.size(width = 180.dp, height = 100.dp)) {
        Box(Modifier.fillMaxSize()) {
            Text("Card content", Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun IksicaItem(receipt: Receipt, onClick: () -> Unit) {
    Column {
        ListItem(modifier = Modifier.clickable(onClick = onClick),
            headlineContent = { Text(receipt.restoran, overflow = TextOverflow.Ellipsis) },
            supportingContent = {
                Text(receipt.datumString + " " + receipt.vrijeme + " ")
            },
            overlineContent = { Text(receipt.autorizacija) },
            trailingContent = { Text(receipt.iznosRacuna + "€") })
        HorizontalDivider()
    }
}

@Composable
fun IksicaLoading(loadingTxt: String = "Loading...") {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = loadingTxt)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun IksicaReceiptDetailed(
    receipt: Receipt? = Receipt(
        restoran = "Restoran",
        datumString = "Datum",
        vrijeme = "Vrijeme",
        detaljiRacuna = listOf(
            ReceiptItem(
                nazivArtikla = "Naziv",
                kolicina = "1",
                cijenaUkupno = "0.55",
                iznosSubvencije = "0.27",
                cijenaJednogArtikla = "0.58"
            )
        ),
        iznosRacuna = "0.55",
        iznosSubvencije = "0.27",
        autorizacija = "Autorizacija",
        urlSastavnica = "https://www.google.com",
        datum = LocalDate.now()
    )
) {
    LazyColumn(modifier = Modifier.padding(10.dp)) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(10.dp, 5.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.location_pin_svgrepo_com),
                    contentDescription = "Lokacija",
                    Modifier.height(20.dp)
                )
                Text(text = receipt?.restoran ?: "")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(10.dp, 5.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.date_time_svgrepo_com),
                    contentDescription = "Vrijeme",
                    Modifier.height(20.dp)
                )
                Text(text = (receipt?.datumString ?: "") + ", " + (receipt?.vrijeme ?: ""))
            }
        }
        items(receipt?.detaljiRacuna ?: emptyList()) {
            IksicaItemDetailed(it)
        }
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(text = "Ukupno plaćeno: ")
                Text(text = receipt?.iznosRacuna ?: "")
            }
            Divider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(text = "Ukupno subvencionirano: ")
                Text(text = receipt?.iznosSubvencije ?: "")
            }
        }
    }
}

@Preview
@Composable
fun IksicaItemDetailed(
    item: ReceiptItem = ReceiptItem(
        nazivArtikla = "Naziv",
        kolicina = "1",
        cijenaUkupno = "0.55",
        iznosSubvencije = "0.27",
        cijenaJednogArtikla = "0.58"
    )
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(Modifier.weight(0.85f)) {
            Text(text = item.kolicina + "x")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = item.nazivArtikla)
        }
        Column(Modifier.padding(start = 15.dp)) {
            Text(text = "- " + item.cijenaUkupno + " €")
            Text(text = "- " + item.iznosSubvencije + " €", color = Color.Red)
        }
    }
    Divider()
}

@Preview
@Composable
fun Test() {
    IksicaItemDetailed(
        item = ReceiptItem(
            nazivArtikla = "Naziva a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a",
            kolicina = "1",
            cijenaUkupno = "0.55",
            iznosSubvencije = "0.27",
            cijenaJednogArtikla = "0.58"
        )
    )
}