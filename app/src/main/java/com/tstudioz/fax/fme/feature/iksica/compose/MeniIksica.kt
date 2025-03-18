package com.tstudioz.fax.fme.feature.iksica.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.dividerColor
import com.tstudioz.fax.fme.feature.menza.models.MeniSpecial
import com.tstudioz.fax.fme.feature.menza.models.Menu
import com.tstudioz.fax.fme.feature.menza.models.Menza

@Composable
fun MeniComposeIksica(menies: Menza?) {

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val mealModifier = Modifier
        .padding(bottom = 16.dp)
        .clip(RoundedCornerShape(15.dp))
        .background(colorResource(id = R.color.raisin_black))
        .border(1.dp, colorResource(R.color.quartz), RoundedCornerShape(16.dp))
        .padding(24.dp, 8.dp)
        .fillMaxWidth()

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp,30.dp,0.dp,0.dp))
            .background(colorResource(R.color.raisin_black))
            .padding(16.dp)
            .heightIn(min = screenHeight.times(0.7f))

    ) {
        Text(
            text = menies?.name?:"",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp, 8.dp, 0.dp, 0.dp)
        )
        Text(
            text = "Restoran Kampus, Ul. Ruđera Boškovića 32",
            fontSize = 15.sp,
            modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 24.dp)
        )
        Text(
            text = "Ručak",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp, 8.dp)
        )
        menies?.menies?.filter { it.mealTime == "RUČAK" }?.forEach {
            MeniItem(it, mealModifier)
        }
        menies?.meniesSpecial?.filter { it.mealTime == "RUČAK" }?.let {
            MeniSpecialIksica(it, mealModifier)
        }
        Text(
            text = "Večera",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        menies?.menies?.filter { it.mealTime == "VEČERA" }?.forEach {
            MeniItem(it, mealModifier)
        }
        menies?.meniesSpecial?.filter { it.mealTime == "VEČERA" }?.let {
            MeniSpecialIksica(it, mealModifier)
        }
    }
}

@Composable
fun MeniItem(meni: Menu, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
    )
    {
        Text(
            text = meni.name,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        MeniTextIksica(meni.soupOrTea)
        MeniTextIksica(meni.mainCourse)
        MeniTextIksica(meni.sideDish)
        MeniTextIksica(meni.salad)
        MeniTextIksica(meni.dessert, false)
        Text(
            text = stringResource(id = R.string.meni_price, meni.price),
            fontSize = 20.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        )
    }
}

@Composable
fun MeniSpecialIksica(meni: List<MeniSpecial>, mealModifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = mealModifier
    ) {
        Text(
            text = stringResource(id = R.string.meals_by_choice),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 10.dp),
        )
        meni.forEach {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Text(
                    text = it.meal,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(0.8f),
                )
                Text(
                    text = stringResource(id = R.string.meni_price, it.price),
                    fontSize = 16.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(0.2f),
                )
            }
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun MeniTextIksica(text: String, divider: Boolean = true) {
    if (text.isNotEmpty()) {
        Text(
            text = text,
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        if (divider) {
            HorizontalDivider(color = dividerColor, thickness = 1.dp)
        }
    }
}