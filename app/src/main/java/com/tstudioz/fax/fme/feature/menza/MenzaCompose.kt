package com.tstudioz.fax.fme.feature.menza

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.tstudioz.fax.fme.database.models.Meni

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenzaCompose(meni: LiveData<List<Meni>>, menzaShow: MutableState<Boolean>) {
    val menies = meni.observeAsState().value
    val meniesMenies = menies?.filter { it.id == "R-MENI" }
    val meniesChoose = menies?.filter { it.id == "R-JELO PO IZBORU" }
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
        MenzaBottomSheet(meniesMenies, meniesChoose)
    }
}

@Composable
fun MenzaBottomSheet(menies: List<Meni>?, meniesChoose: List<Meni>?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(16.dp, 0.dp, 16.dp, 16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        menies?.forEach {
            MeniCompose(it)
        }
        MeniComposeChoose(meniesChoose ?: emptyList())

    }
}

@Composable
fun MeniCompose(meni: Meni) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .background(Color.White)
            .padding(20.dp, 10.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = meni.type ?: "",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        MeniText(meni.jelo1 ?: "")
        MeniText(meni.jelo2 ?: "")
        MeniText(meni.jelo3 ?: "")
        MeniText(meni.jelo4 ?: "")
        MeniText(meni.desert ?: "", false)
        Text(
            text = meni.cijena ?: "",
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
fun MeniComposeChoose(meni: List<Meni>) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .background(Color.White)
            .padding(20.dp, 10.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "JELA PO IZBORU" ,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        meni.forEach {
            MeniText(it.jelo1 ?: "", false)
            Text(
                text = it.cijena ?: "",
                fontSize = 20.sp,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            )
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
        if (divider){ HorizontalDivider() }
    }
}