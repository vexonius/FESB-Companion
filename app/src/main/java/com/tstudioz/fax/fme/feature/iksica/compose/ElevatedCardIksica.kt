package com.tstudioz.fax.fme.feature.iksica.compose

import android.graphics.Paint
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.compose.glowingColor
import com.tstudioz.fax.fme.compose.gradientColors
import com.tstudioz.fax.fme.feature.iksica.models.StudentData
import java.util.Locale

@Preview
@Composable
fun ElevatedCardIksica(
    name: String = "Ime Prezime",
    iksicaNumber: String = "0000000000000000000",
    balance: Double = 0.00,
    onClick: () -> Unit = {}
) {
    val cornersRadius = 30.dp
    val glowingRadius = 100.dp

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp, 30.dp, 24.dp, 50.dp)
        .aspectRatio(1.586f)
        .drawBehind {
            val canvasSize = size
            drawContext.canvas.nativeCanvas.apply {
                drawRoundRect(0f,
                    0f,
                    canvasSize.width, canvasSize.height,
                    cornersRadius.toPx(), cornersRadius.toPx(),
                    Paint().apply {
                        isAntiAlias = true
                        setShadowLayer(glowingRadius.toPx(), 0f, 0f, glowingColor.toArgb())
                    })
                drawRoundRect(0f,
                    0f,
                    canvasSize.width, canvasSize.height,
                    cornersRadius.toPx(), cornersRadius.toPx(),
                    Paint().apply {
                        isAntiAlias = true
                        setShadowLayer((glowingRadius / 4).toPx(), 0f, 0f, glowingColor.toArgb())
                    })
            }
        }
        .clip(shape = RoundedCornerShape(cornersRadius))
        .angledGradientBackground(
            colors = gradientColors, degrees = 32f
        )) {
        Column(
            Modifier.clickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                Modifier
                    .padding(25.dp)
                    .weight(0.7f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(0.dp, 0.dp)
                ) {
                    Text(
                        text = name, fontSize = 25.sp, fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val formattedNumber = buildList {
                        add(iksicaNumber.take(6))
                        add(iksicaNumber.drop(6).take(2))
                        add(iksicaNumber.drop(8).take(10))
                        add(iksicaNumber.takeLast(1))
                    }.joinToString(" ")
                    Text(
                        text = formattedNumber,
                        fontSize = 16.sp,
                    )
                }
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxSize()
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.iksica_balance, String.format(Locale.getDefault(), "%.2f", balance)
                        ),
                        fontSize = 25.sp,
                        lineHeight = 25.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
    }
}

@Composable
fun CardIksicaPopupContent(studentInfo: StudentData) {
    AppTheme {
        Column(
            Modifier
                .padding(15.dp)
                .background(MaterialTheme.colorScheme.background)
                .width(300.dp)
        ) {
            CardIksicaPopupRow(leftText = stringResource(R.string.name_label), rightText = studentInfo.nameSurname)
            CardIksicaPopupRow(leftText = stringResource(R.string.rights_level_label), rightText = studentInfo.rightsLevel)
            CardIksicaPopupRow(
                leftText = stringResource(R.string.daily_support_label), rightText = stringResource(
                    id = R.string.iksica_balance, String.format(Locale.getDefault(), "%.2f", studentInfo.dailySupport)
                )
            )
            CardIksicaPopupRow(leftText = stringResource(R.string.oib_label), rightText = studentInfo.oib)
            CardIksicaPopupRow(leftText = stringResource(R.string.jmbag_label), rightText = studentInfo.jmbag)
            CardIksicaPopupRow(leftText = stringResource(R.string.card_number_label), rightText = studentInfo.cardNumber)
            CardIksicaPopupRow(leftText = stringResource(R.string.rights_from_label), rightText = studentInfo.rightsFrom)
            CardIksicaPopupRow(leftText = stringResource(R.string.right_until_label), rightText = studentInfo.rightsTo)
            CardIksicaPopupRow(
                leftText = stringResource(R.string.card_balance_label), rightText = stringResource(
                    id = R.string.iksica_balance, String.format(Locale.getDefault(), "%.2f", studentInfo.balance)
                ), divider = false
            )
        }
    }
}

@Composable
fun CardIksicaPopupRow(
    leftText: String, rightText: String, divider: Boolean = true
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