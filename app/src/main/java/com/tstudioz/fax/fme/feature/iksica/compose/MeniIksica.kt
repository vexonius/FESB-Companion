package com.tstudioz.fax.fme.feature.iksica.compose

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.meniBackground
import com.tstudioz.fax.fme.compose.meniGlow
import com.tstudioz.fax.fme.feature.iksica.models.MenzaLocation
import com.tstudioz.fax.fme.feature.menza.models.MealTime
import com.tstudioz.fax.fme.feature.menza.models.MeniSpecial
import com.tstudioz.fax.fme.feature.menza.models.Menu
import com.tstudioz.fax.fme.feature.menza.models.Menza

@Composable
fun MeniComposeIksica(meni: Pair<MenzaLocation, Menza?>?) {
    val menzaLocation = meni?.first
    val menies = meni?.second
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp))
            .background(meniBackground)
            .padding(16.dp)
            .heightIn(min = screenHeight.times(0.7f))
            .fillMaxWidth()
    ) {
        Text(
            text = menzaLocation?.name ?: "",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(16.dp, 8.dp, 0.dp, 0.dp)
        )
        Text(
            text = menzaLocation?.address ?: "",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 24.dp)
        )
        if (meni?.second?.dateFetched == meni?.second?.datePosted) {
            menies?.let { MealTimeContent(it, MealTime.LUNCH) }
            menies?.let { MealTimeContent(it, MealTime.DINNER) }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.no_data_icon),
                    contentDescription = stringResource(R.string.page_not_found),
                    modifier = Modifier
                        .padding(12.dp, 80.dp, 12.dp, 12.dp)
                        .size(80.dp)
                )
                Text(stringResource(R.string.menza_no_data))
            }
        }
    }
}

@Composable
fun MealTimeContent(menza: Menza, mealTime: MealTime) {

    val menies = if (mealTime == MealTime.LUNCH) menza.meniesLunch else menza.meniesDinner
    val meniesSpecial = if (mealTime == MealTime.LUNCH) menza.meniesSpecialLunch else menza.meniesSpecialDinner

    if (menies.isEmpty() && meniesSpecial.isEmpty()) return

    val glowingRadius = 20.dp
    val cornerRadius = 15.dp
    val mealModifier = Modifier
        .padding(bottom = 16.dp)
        .glow(meniGlow, cornerRadius, glowingRadius)
        .clip(RoundedCornerShape(cornerRadius))
        .background(MaterialTheme.colorScheme.background)
        .padding(24.dp, 8.dp)
        .fillMaxWidth()
    Row(
        Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (mealTime == MealTime.LUNCH) stringResource(R.string.lunch_title)
            else stringResource(R.string.dinner_title),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
    menies.forEach { MeniItem(it, mealModifier) }
    MeniSpecialIksica(meniesSpecial, mealModifier)
}

@Composable
fun MeniItem(meni: Menu, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Top, modifier = modifier
    ) {
        Text(
            text = meni.name,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        val soupOrTea = if (meni.mealTime == MealTime.LUNCH) stringResource(R.string.soup)
        else stringResource(R.string.beverage)

        MeniTextIksica(meni.soupOrTea, soupOrTea)
        MeniTextIksica(meni.mainCourse, stringResource(R.string.main_course))
        MeniTextIksica(meni.sideDish, stringResource(R.string.side_dish))
        MeniTextIksica(meni.salad, stringResource(R.string.salad))
        MeniTextIksica(meni.dessert, stringResource(R.string.dessert), false)
        if (meni.price != "") {
            Text(
                text = stringResource(id = R.string.meni_price, meni.price),
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            )
        } else {
            Spacer(Modifier.height(5.dp))
        }
    }
}

@Composable
fun MeniSpecialIksica(meni: List<MeniSpecial>, modifier: Modifier) {
    if (meni.isNotEmpty()) {
        Column(
            horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Top, modifier = modifier
        ) {
            Text(
                text = stringResource(id = R.string.meals_by_choice),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 10.dp),
            )
            meni.forEachIndexed { index, it ->
                if (index != 0) HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {
                    Text(
                        text = it.meal,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(0.8f),
                    )
                    Text(
                        text = stringResource(id = R.string.meni_price, it.price),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(0.2f),
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun MeniTextIksica(text: String, type: String, divider: Boolean = true) {
    if (text.isNotEmpty()) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = type,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = 5.dp)
            )
            Text(
                text = text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 5.dp)
            )
        }
        if (divider) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
        }
    }
}

fun Modifier.glow(
    color: Color,
    cornersRadius: Dp = 10.dp,
    glowingRadius: Dp = 10.dp,
): Modifier = drawBehind {
    val canvasSize = size
    drawContext.canvas.nativeCanvas.apply {
        drawRoundRect(
            0f, 0f, canvasSize.width, canvasSize.height, cornersRadius.toPx(), cornersRadius.toPx(), Paint().apply {
                isAntiAlias = true
                setShadowLayer(glowingRadius.toPx(), 0f, 0f, color.toArgb())
            })
        drawRoundRect(
            0f, 0f, canvasSize.width, canvasSize.height, cornersRadius.toPx(), cornersRadius.toPx(), Paint().apply {
                isAntiAlias = true
                setShadowLayer((glowingRadius / 2).toPx(), 0f, 0f, color.toArgb())
            })
        drawRoundRect(
            0f, 0f, canvasSize.width, canvasSize.height, cornersRadius.toPx(), cornersRadius.toPx(), Paint().apply {
                isAntiAlias = true
                setShadowLayer((glowingRadius / 4).toPx(), 0f, 0f, color.toArgb())
            })
    }
}