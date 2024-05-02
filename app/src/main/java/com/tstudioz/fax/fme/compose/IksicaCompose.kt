package com.tstudioz.fax.fme.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.viewmodel.IksicaViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.java.KoinJavaComponent.inject

@OptIn(InternalCoroutinesApi::class, ExperimentalMaterial3Api::class)
@Composable
fun IksicaCompose() {

    val iksicaViewModel: IksicaViewModel by inject(IksicaViewModel::class.java)

    BottomSheetScaffold(sheetPeekHeight = 0.dp,
        sheetContent = {
            if (iksicaViewModel.showItem.observeAsState(initial = false).value) {
                val receipt = iksicaViewModel.itemToShow.observeAsState().value
                val sheetState = rememberModalBottomSheetState()
                ModalBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = { iksicaViewModel.toggleShowItem(false) }
                ) {
                Column {
                    Text(text = receipt?.restoran ?: "")
                    Text(text = receipt?.datumString ?: "")
                    Text(text = receipt?.vrijeme ?: "")
                    Text(text = receipt?.iznosRacuna ?: "")
                    Text(text = receipt?.iznosSubvencije ?: "")
                    Text(text = receipt?.autorizacija ?: "")
                    Text(text = receipt?.urlSastavnica ?: "")
                    receipt?.detaljiRacuna?.forEach {
                        Text(text = it.nazivArtikla)
                        Text(text = it.kolicina)
                        Text(text = it.cijenaUkupno)
                    }
                }}
            }
        }) {
        val list = iksicaViewModel.receipts.observeAsState().value
        LazyColumn {
            items(list ?: listOf()) {
                IksicaItem(it) {
                    iksicaViewModel.getRacun(it)
                }
            }
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
            /*leadingContent = {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "Localized description",
                )
            },*/
            trailingContent = { Text(receipt.iznosRacuna + "€") }
        )
        HorizontalDivider()
    }
}
