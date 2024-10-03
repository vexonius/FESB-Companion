package com.tstudioz.fax.fme.feature.menza.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.menza.models.MeniSpecial
import com.tstudioz.fax.fme.feature.menza.models.Menu
import com.tstudioz.fax.fme.feature.menza.models.Menza
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenzaCompose(meni: LiveData<Menza?>, menzaShow: MutableState<Boolean>) {
    val menies = meni.observeAsState().value
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { menzaShow.value = false },
        containerColor = colorResource(id = R.color.greenHighlight),
        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
        dragHandle = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.menza_title),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    ) {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        if (menies?.dateFetched == menies?.datePosted && today == menies?.dateFetched) {
            MenzaBottomSheet(menies)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp, 16.dp, 16.dp)
                    .background(Color.White, RoundedCornerShape(15.dp))
            ) {
                Text(
                    text = stringResource(id = R.string.menza_no_data),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun MenzaBottomSheet(menies: Menza?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(16.dp, 0.dp, 16.dp, 16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        val mealModifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White)
            .padding(20.dp, 10.dp)
            .fillMaxWidth()
        menies?.menies?.filter { it.mealTime == "RUČAK" }?.forEach {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = mealModifier
            ) {
                MeniCompose(it)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        menies?.meniesSpecial?.filter { it.mealTime == "RUČAK" }?.let {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = mealModifier
            ) {
                MeniComposeChoose(it)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

    }
}

@Composable
fun MeniCompose(meni: Menu) {
    Text(
        text = meni.name,
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 10.dp)
    )
    MeniText(meni.soupOrTea)
    MeniText(meni.mainCourse)
    MeniText(meni.sideDish)
    MeniText(meni.salad)
    MeniText(meni.dessert, false)
    Text(
        text = stringResource(id = R.string.meni_price, meni.price),
        fontSize = 20.sp,
        textAlign = TextAlign.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    )
}

@Composable
fun MeniComposeChoose(meni: List<MeniSpecial>) {
    Text(
        text = stringResource(id = R.string.JELA_PO_IZBORU),
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 10.dp)
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
                modifier = Modifier.weight(0.8f)
            )
            Text(
                text = stringResource(id = R.string.meni_price, it.price),
                fontSize = 16.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.2f)
            )
        }
        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun MeniText(text: String, divider: Boolean = true) {
    if (text.isNotEmpty()) {
        Text(
            text = text,
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        if (divider) {
            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
        }
    }
}