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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.compose.accentGreen


@Composable
fun SubjectView(subject: StudomatSubject) {

    var expanded: Boolean by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier
            .padding(8.dp)
            .wrapContentHeight(),
    ) {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                Modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(4.dp, 2.dp, 4.dp, 2.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                PredmetText(text = "Predmet: ", value = subject.name.trim() ?: "", isTitle = true)
            }
            Column(
                Modifier.padding(4.dp, 0.dp, 0.dp, 8.dp)
            ) {
                if (expanded) {
                    PredmetText(text = stringResource(id = R.string.elective_group), value = subject.electiveGroup ?: "")
                    PredmetText(text = stringResource(id = R.string.semester), value = subject.semester ?: "")
                    PredmetText(text = stringResource(id = R.string.lectures), value = subject.lectures ?: "")
                    PredmetText(text = stringResource(id = R.string.exercises), value = subject.exercises ?: "")
                    PredmetText(text = stringResource(id = R.string.ects_enrolled), value = subject.ectsEnrolled ?: "")
                    PredmetText(text = stringResource(id = R.string.is_taken), value = subject.isTaken ?: "")
                    PredmetText(text = stringResource(id = R.string.status), value = subject.status ?: "", isPassed = subject.isPassed)
                    PredmetText(text = stringResource(id = R.string.grade), value = subject.grade ?: "")
                    PredmetText(text = stringResource(id = R.string.exam_date), value = subject.examDate ?: "")
                } else {
                    PredmetText(text = stringResource(id = R.string.semester), value = subject.semester ?: "")
                    PredmetText(text = stringResource(id = R.string.ects_enrolled), value = subject.ectsEnrolled ?: "")
                    PredmetText(text = stringResource(id = R.string.status), value = subject.status ?: "", isPassed = subject.isPassed)
                    PredmetText(text = stringResource(id = R.string.grade), value = subject.grade ?: "")
                }

            }
        }
    }
}


@Composable
fun PredmetText(text: String, value: String, isTitle: Boolean = false, isPassed: Boolean = false) {
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
            fontSize = if (isTitle) 16.sp else 14.sp,
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            modifier = if (isPassed) {
                Modifier
                    .wrapContentSize()
                    .clip(RoundedCornerShape(12.dp, 0.dp, 0.dp, 12.dp))
                    .background(accentGreen)
                    .padding(8.dp, 8.dp, 16.dp, 8.dp)
            } else {
                Modifier
                    .wrapContentSize()
                    .padding(8.dp, 8.dp, 16.dp, 8.dp)
            },
            fontSize = if (isTitle) 16.sp else 14.sp,
            textAlign = TextAlign.Right
        )
    }

}

@Preview
@Composable
fun PredmetComposePrev() {
    AppTheme {
        SubjectView(
            StudomatSubject(
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
