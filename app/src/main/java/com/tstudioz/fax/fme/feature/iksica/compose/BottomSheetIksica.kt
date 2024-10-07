package com.tstudioz.fax.fme.feature.iksica.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.ReceiptItem
import java.math.RoundingMode
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetIksica(
    itemToShow: MutableLiveData<Receipt>,
    toggleShowItem: (Boolean) -> Unit,
) {
    val receipt = itemToShow.observeAsState().value
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { toggleShowItem(false) },
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        IksicaReceiptDetailed(receipt)
    }
}

@Composable
fun IksicaReceiptDetailed(
    receipt: Receipt?
) {
    LazyColumn {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp, 20.dp, 10.dp)
            ) {
                Text(text = receipt?.restaurant ?: "", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = (receipt?.dateString ?: "") + ", " + (receipt?.time ?: ""), fontSize = 15.sp)
            }
        }
        items(receipt?.receiptDetails ?: emptyList()) {
            IksicaItemDetailed(it)
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                Modifier
                    .padding(20.dp, 10.dp, 20.dp, 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Text(text = "Ukupno plaćeno: ", fontSize = 18.sp)
                    Text(
                        text = receipt?.paidAmount?.toBigDecimal()
                            ?.setScale(2, RoundingMode.HALF_EVEN).toString() + " €", fontSize = 18.sp
                    )
                }
                HorizontalDivider()
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Text(text = "Ukupno subvencionirano: ", fontSize = 18.sp)
                    Text(text = receipt?.subsidizedAmount.toString() + " €", fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun IksicaItemDetailed(
    item: ReceiptItem
) {
    Row(
        Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .padding(15.dp, 10.dp, 15.dp, 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(Modifier.weight(0.85f)) {
            Row(Modifier.padding(bottom = 5.dp)) {
                Text(text = item.amount.toString() + "x", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = item.articleName, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "Cijena: " + item.price.toString() + " €",
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = "Subvencija: " + item.subsidizedAmount.toString() + " €",
                color = MaterialTheme.colorScheme.outline
            )
        }
        Column(
            Modifier.padding(start = 15.dp)
        ) {
            Text(
                text = item.total.toBigDecimal().minus(item.subsidizedAmount.toBigDecimal())
                    .times(item.amount.toBigDecimal()).toString() + " €"
            )
        }
    }
    HorizontalDivider()
}

@Preview
@Composable
fun IksicaItemPreview() {
    IksicaItem(
        receipt = Receipt(
            restaurant = "Restoran",
            dateString = "Datum",
            time = "Vrijeme",
            receiptDetails = listOf(
                ReceiptItem(
                    articleName = "Naziv",
                    amount = 1,
                    total = 0.55,
                    subsidizedAmount = 0.27,
                    price = 0.58
                )
            ),
            receiptAmount = 0.55,
            subsidizedAmount = 0.27,
            paidAmount = 0.55,
            authorised = "Autorizacija",
            url = "https://www.google.com",
            date = LocalDate.now()
        )
    ) {}
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun IksicaReceiptDetailedPreview() {
    Box(Modifier.background(MaterialTheme.colorScheme.background)) {
        IksicaReceiptDetailed(
            receipt = Receipt(
                restaurant = "Restoran",
                dateString = "Datum",
                time = "Vrijeme",
                receiptDetails = listOf(
                    ReceiptItem(
                        articleName = "Naziv",
                        amount = 1,
                        total = 0.55,
                        subsidizedAmount = 0.27,
                        price = 0.58
                    ),
                    ReceiptItem(
                        articleName = "Naziv",
                        amount = 1,
                        total = 0.55,
                        subsidizedAmount = 0.27,
                        price = 0.58
                    )
                ),
                receiptAmount = 0.55,
                subsidizedAmount = 0.27,
                paidAmount = 0.55,
                authorised = "Autorizacija",
                url = "https://www.google.com",
                date = LocalDate.now()
            )
        )
    }
}