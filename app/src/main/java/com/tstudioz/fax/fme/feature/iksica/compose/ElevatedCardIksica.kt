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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.feature.iksica.models.StudentData

@Preview
@Composable
fun ElevatedCardIksica(
    name: String = "Ime Prezime",
    iksicaNumber: String = "0000000000000000000",
    balance: String = "0.00",
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
                    text = "$balance €",
                    fontSize = 25.sp,
                    lineHeight = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CardIksicaPopupContent(
    studentData: StudentData,
    iksicaBalance: Double
) {
    Column(
        Modifier
            .padding(15.dp)
            .background(MaterialTheme.colorScheme.background)
            .width(300.dp)
    ) {
        CardIksicaPopupRow(leftText = "Ime", rightText = studentData.nameSurname)
        CardIksicaPopupRow(leftText = "Razina prava", rightText = studentData.rightsLevel)
        CardIksicaPopupRow(leftText = "Dnevna potpora", rightText = studentData.dailySupport.toString())
        CardIksicaPopupRow(leftText = "OIB", rightText = studentData.oib)
        CardIksicaPopupRow(leftText = "JMBAG", rightText = studentData.jmbag)
        CardIksicaPopupRow(leftText = "Broj iksice", rightText = studentData.iksicaNumber)
        CardIksicaPopupRow(leftText = "Prava od", rightText = studentData.rightsFrom)
        CardIksicaPopupRow(leftText = "Prava do", rightText = studentData.rightsTo)
        CardIksicaPopupRow(leftText = "Stanje iksice", rightText = iksicaBalance.toString(), divider = false)
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
        Text(text = leftText)
        Text(text = rightText)
    }
    if (divider) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    }
}