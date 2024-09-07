package com.tstudioz.fax.fme.feature.studomat.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.feature.studomat.dataclasses.Predmet
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme


@Preview
@Composable
fun PredmetComposePrev() {
    AppTheme {
        PredmetCompose(
            Predmet(
                "Predmet",
                "Izborna grupa",
                "Semestar",
                "Predavanja",
                "Vježbe",
                "ECTS upisano",
                "Polaže se",
                "obavljen",
                "Ocjena",
                "Datum ispitnog roka"
            )
        )
    }
}

@Composable
fun PredmetCompose(predmet: Predmet) {

    var expanded: Boolean by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier
            .padding(8.dp)
            .wrapContentHeight(),
    ) {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.surfaceDim)
        ) {
            Column(
                Modifier
                    .background(color = colorResource(id = R.color.StudomatBlue))
                    .padding(4.dp, 2.dp, 4.dp, 2.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                PredmetText(text = "Predmet: ", value = predmet.name.trim())
            }
            Column(
                Modifier.padding(4.dp, 0.dp, 0.dp, 8.dp)
            ) {
                if (expanded) {
                    PredmetText(text = "Izborna grupa: ", value = predmet.izbornaGrupa)
                    PredmetText(text = "Semestar: ", value = predmet.semestar)
                    PredmetText(text = "Predavanja: ", value = predmet.predavanja)
                    PredmetText(text = "Vježbe: ", value = predmet.vjezbe)
                    PredmetText(text = "ECTS upisano: ", value = predmet.ectsUpisano)
                    PredmetText(text = "Polaže se: ", value = predmet.polazeSe)
                    PredmetText(text = "Status: ", value = predmet.status)
                    PredmetText(text = "Ocjena: ", value = predmet.ocjena)
                    PredmetText(text = "Datum ispitnog roka: ", value = predmet.datumIspitnogRoka)
                } else {
                    PredmetText(text = "Semestar: ", value = predmet.semestar)
                    PredmetText(text = "ECTS upisano: ", value = predmet.ectsUpisano)
                    PredmetText(text = "Status: ", value = predmet.status)
                    PredmetText(text = "Ocjena: ", value = predmet.ocjena)
                }
            }
        }
    }
}


@Composable
fun PredmetText(text: String = "", value: String = "") {
    Row(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp, 8.dp, 8.dp, 8.dp),
            fontSize = if (text.contains("Predmet:")) 16.sp else 14.sp,
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            modifier = if (text == "Status: " && (value.contains("obavljen") || value.contains("priznat"))) {
                Modifier
                    .wrapContentSize()
                    .clip(RoundedCornerShape(12.dp, 0.dp, 0.dp, 12.dp))
                    .background(colorResource(id = R.color.passGreen))
                    .padding(8.dp, 8.dp, 16.dp, 8.dp)
            } else {
                Modifier
                    .wrapContentSize()
                    .padding(8.dp, 8.dp, 16.dp, 8.dp)
            },
            fontSize = if (text.contains("Predmet:")) 16.sp else 14.sp,
            textAlign = TextAlign.Right
        )
    }

}