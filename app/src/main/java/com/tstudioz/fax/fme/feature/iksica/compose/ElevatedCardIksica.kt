package com.tstudioz.fax.fme.feature.iksica.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.feature.iksica.models.StudentData
import com.tstudioz.fax.fme.feature.iksica.models.StudentDataRealm
import java.util.Locale

@Preview
@Composable
fun ElevatedCardIksica(
    name: String = "Ime Prezime",
    iksicaNumber: String = "0000000000000000000",
    balance: Double = 0.00,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.586f)
            .padding(25.dp)
            .clip(shape = RoundedCornerShape(10.dp))
            .angledGradientBackground(
                colors = listOf(
                    Color(0xFFfa2531),
                    Color(0xFF0075B2)
                ),
                degrees = 45f,
                halfHalf = true
            )
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .clickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                Modifier
                    .padding(25.dp, 25.dp, 0.dp, 0.dp)
                    .weight(0.7f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(0.dp, 5.dp)
                ) {
                    Text(
                        text = name,
                        fontSize = 25.sp,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .padding(0.dp, 5.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = iksicaNumber,
                        fontSize = 20.sp,
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(0.dp, 0.dp, 25.dp, 25.dp)
                    .weight(0.3f)
                    .fillMaxSize()
            ) {
                Text(
                    text = stringResource(
                        id = R.string.iksica_balance, String.format(Locale.US, "%.2f", balance)
                    ),
                    fontSize = 25.sp,
                    lineHeight = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CardIksicaPopupContent(model: StudentData) {
    AppTheme {
        Column(
            Modifier
                .padding(15.dp)
                .background(MaterialTheme.colorScheme.background)
                .width(300.dp)
        ) {
            CardIksicaPopupRow(leftText = stringResource(R.string.name_label), rightText = model.nameSurname)
            CardIksicaPopupRow(leftText = stringResource(R.string.rights_level_label), rightText = model.rightsLevel)
            CardIksicaPopupRow(
                leftText = stringResource(R.string.daily_support_label), rightText = stringResource(
                    id = R.string.iksica_balance, String.format(Locale.getDefault(), "%.2f", model.dailySupport)
                )
            )
            CardIksicaPopupRow(leftText = stringResource(R.string.oib_label), rightText = model.oib)
            CardIksicaPopupRow(leftText = stringResource(R.string.jmbag_label), rightText = model.jmbag)
            CardIksicaPopupRow(leftText = stringResource(R.string.card_number_label), rightText = model.cardNumber)
            CardIksicaPopupRow(leftText = stringResource(R.string.rights_from_label), rightText = model.rightsFrom)
            CardIksicaPopupRow(leftText = stringResource(R.string.right_until_label), rightText = model.rightsTo)
            CardIksicaPopupRow(
                leftText = stringResource(R.string.card_balance_label), rightText = stringResource(
                    id = R.string.iksica_balance, String.format(Locale.getDefault(), "%.2f", model.balance)
                ), divider = false
            )
        }
    }
}

@Composable
fun CardIksicaPopupRow(
    leftText: String,
    rightText: String,
    divider: Boolean = true
) {
    Row(
        Modifier
            .padding(20.dp, 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = leftText, color = MaterialTheme.colorScheme.onSurface)
        Text(text = rightText, color = MaterialTheme.colorScheme.onSurface)
    }
    if (divider) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    }
}