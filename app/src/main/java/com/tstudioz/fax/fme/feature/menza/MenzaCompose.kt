package com.tstudioz.fax.fme.feature.menza

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.menza.models.MeniSpecial
import com.tstudioz.fax.fme.feature.menza.models.Menu
import com.tstudioz.fax.fme.feature.menza.models.Menza

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenzaCompose(meni: LiveData<Menza?>, menzaShow: MutableState<Boolean>) {
    val menies = meni.observeAsState().value
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { menzaShow.value = false },
        containerColor = colorResource(id = R.color.welcome2),
        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
        dragHandle = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Menza", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    ) {
        MenzaBottomSheet(menies)
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
        menies?.menies?.filter { it.mealTime == "RUČAK" }?.forEach {
            MeniCompose(it)
        }
        menies?.meniesSpecial?.let { MeniComposeChoose(it) }

    }
}

@Composable
fun MeniCompose(meni: Menu) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .background(Color.White)
            .padding(20.dp, 10.dp)
            .fillMaxWidth()
    ) {
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
            text = meni.price,
            fontSize = 20.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun MeniComposeChoose(meni: MutableList<MeniSpecial>) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .background(Color.White)
            .padding(20.dp, 10.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "JELA PO IZBORU",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        meni.forEach {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
            ) {
                Text(
                    text = it.meal,
                    fontSize = 16.sp
                )
                Text(
                    text = it.price,
                    fontSize = 16.sp
                )
            }
            HorizontalDivider()
        }
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
            HorizontalDivider()
        }
    }
}