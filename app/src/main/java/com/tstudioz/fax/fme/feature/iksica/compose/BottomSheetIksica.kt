package com.tstudioz.fax.fme.feature.iksica.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.contentColors
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.ReceiptItem
import com.tstudioz.fax.fme.feature.iksica.roundToTwo
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetIksica(
    receipt: Receipt?,
    toggleShowItem: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { toggleShowItem() },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = { WindowInsets(0.dp) },
        dragHandle = { },
    ) {
        IksicaReceiptDetailed(receipt)
    }
}

@Composable
fun IksicaReceiptDetailed(
    receipt: Receipt?
) {
    LazyColumn(
        Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 20.dp, 20.dp, 10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.transaction_details),
                    color = MaterialTheme.contentColors.primary,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                Text(
                    text = receipt?.restaurant ?: "",
                    color = MaterialTheme.contentColors.secondary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = (receipt?.dateString ?: "") + ", " + (receipt?.time ?: ""),
                    color = MaterialTheme.contentColors.secondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        items(receipt?.receiptDetails ?: emptyList()) {
            IksicaItemDetailed(it)
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .padding(20.dp, 10.dp, 20.dp, 10.dp)
                    .fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(id = R.string.transaction_total),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.contentColors.primary
                        )
                        Text(
                            text = stringResource(id = R.string.transaction_subsidized),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.contentColors.secondary
                        )
                        Text(
                            text = stringResource(id = R.string.transaction_paid),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.contentColors.secondary,
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text(
                            text = receipt?.receiptAmount?.roundToTwo() + stringResource(R.string.currency),
                            color = MaterialTheme.contentColors.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = receipt?.subsidizedAmount?.roundToTwo() + stringResource(R.string.currency),
                            color = MaterialTheme.contentColors.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = receipt?.paidAmount?.roundToTwo() + stringResource(R.string.currency),
                            color = MaterialTheme.contentColors.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun IksicaItemDetailed(item: ReceiptItem) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(20.dp, 5.dp, 15.dp, 5.dp)
    ) {

        Text(
            text = stringResource(R.string.amount_x, item.amount.toString()),
            color = MaterialTheme.contentColors.primary,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = item.articleName,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(0.7f)
                )
                Text(
                    text = item.total.toBigDecimal()
                        .minus(item.subsidizedAmount.toBigDecimal())
                        .times(item.amount.toBigDecimal())
                        .toString() + stringResource(R.string.currency),
                    modifier = Modifier
                        .weight(0.20f)
                        .padding(start = 10.dp),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.End
                )
            }
            Column {
                Text(
                    text = stringResource(
                        R.string.price_of_item,
                        item.price.toString()
                    ) + stringResource(R.string.currency),
                    color = MaterialTheme.contentColors.tertiary,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = stringResource(
                        R.string.subsidized_price_of_item,
                        item.subsidizedAmount.toString()
                    ) + stringResource(R.string.currency),
                    color = MaterialTheme.contentColors.tertiary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
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