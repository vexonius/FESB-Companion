package com.tstudioz.fax.fme.feature.iksica.compose

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.dividerColor
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
    val mealModifier = Modifier
        .padding(bottom = 16.dp)
        .clip(RoundedCornerShape(15.dp))
        .background(Color(0xFF101010))
        .padding(24.dp, 8.dp)
        .fillMaxWidth()

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp))
            .background(Color(0xFF202022))
            .padding(16.dp)
            .heightIn(min = screenHeight.times(0.7f))
            .fillMaxWidth()
    ) {
        Text(
            text = menzaLocation?.name ?: "",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp, 8.dp, 0.dp, 0.dp)
        )
        Text(
            text = menzaLocation?.address ?: "",
            fontSize = 15.sp,
            modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 24.dp)
        )
        if (meni?.second?.dateFetched == meni?.second?.datePosted) {
            Text(
                text = "Ručak",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp, 8.dp)
            )
            menies?.menies?.filter { it.mealTime == MealTime.LUNCH }?.forEach {
                MeniItem(it, mealModifier)
            }
            menies?.meniesSpecial?.filter { it.mealTime == MealTime.LUNCH }?.let {
                MeniSpecialIksica(it, mealModifier)
            }
            Text(
                text = "Večera",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 10.dp)
            )
            menies?.menies?.filter { it.mealTime == MealTime.DINNER }?.forEach {
                MeniItem(it, mealModifier)
            }
            menies?.meniesSpecial?.filter { it.mealTime == MealTime.DINNER }?.let {
                MeniSpecialIksica(it, mealModifier)
            }
        } else {
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.no_data_icon),
                    contentDescription = "page_not_found",
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
        val soupOrTea = if (meni.mealTime==MealTime.LUNCH) stringResource(R.string.soup)
        else stringResource(R.string.beverage)

        MeniTextIksica(meni.soupOrTea, soupOrTea)
        MeniTextIksica(meni.mainCourse, stringResource(R.string.main_course))
        MeniTextIksica(meni.sideDish, stringResource(R.string.side_dish))
        MeniTextIksica(meni.salad, stringResource(R.string.salad))
        MeniTextIksica(meni.dessert, stringResource(R.string.dessert), false)
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
fun MeniSpecialIksica(meni: List<MeniSpecial>, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.meals_by_choice),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 10.dp),
        )
        meni.forEachIndexed { index, it ->
            if (index != 0) HorizontalDivider(color = dividerColor, thickness = 1.dp)
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
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun MeniTextIksica(text: String, type: String, divider: Boolean = true) {
    if (text.isNotEmpty()) {
        Column (Modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(type, fontSize = 10.sp, color = Color.White.copy(alpha = 0.3f))
            Text(
                text = text,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(bottom = 5.dp)
            )
        }
        if (divider) {
            HorizontalDivider(color = dividerColor, thickness = 1.dp)
        }
    }
}