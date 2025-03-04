package com.tstudioz.fax.fme.feature.studomat.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject


@Composable
fun YearView(list: List<StudomatSubject>) {
    Column(
        modifier = Modifier
            .padding(24.dp, 12.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(colorResource(id = R.color.raisin_black))
            .padding(24.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text(
            text = stringResource(R.string.enrolled_subjects),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            modifier = Modifier.padding(0.dp, 4.dp, 0.dp, 16.dp)
        )
        list.forEachIndexed { index, it ->
            if (index != 0) HorizontalDivider()
            val opened = remember { mutableStateOf(false) }
            Column(Modifier.clickable { opened.value = !opened.value }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .clickable { opened.value = !opened.value }
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .wrapContentHeight(),
                ) {
                    Text(
                        text = it.name,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Left
                    )
                    Text(
                        text = it.grade.takeUnless { it.contains("podatak") } ?: "-",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Right
                    )
                }
                if (opened.value) SubjectView(it)
            }
        }
    }
}

@Composable
fun SubjectView(subject: StudomatSubject) {
    Column(Modifier.padding(4.dp, 0.dp, 0.dp, 8.dp)) {
        PredmetText(text = stringResource(id = R.string.elective_group), value = subject.electiveGroup)
        PredmetText(text = stringResource(id = R.string.semester), value = subject.semester)
        PredmetText(text = stringResource(id = R.string.lectures), value = subject.lectures)
        PredmetText(text = stringResource(id = R.string.exercises), value = subject.exercises)
        PredmetText(text = stringResource(id = R.string.ects_enrolled), value = subject.ectsEnrolled)
        PredmetText(text = stringResource(id = R.string.is_taken), value = subject.isTaken)
        PredmetText(text = stringResource(id = R.string.status), value = subject.status, isPassed = subject.isPassed)
        PredmetText(text = stringResource(id = R.string.grade), value = subject.grade)
        PredmetText(text = stringResource(id = R.string.exam_date), value = subject.examDate)
    }
}

@Composable
fun PredmetText(text: String, value: String, isPassed: Boolean = false) {
    val gradeModifier = if (isPassed) {
        Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(12.dp, 12.dp, 12.dp, 12.dp))
            .background(colorResource(id = R.color.pass_green))
            .padding(8.dp, 4.dp, 16.dp, 4.dp)
    } else {
        Modifier
            .wrapContentSize()
            .padding(8.dp, 4.dp, 16.dp, 4.dp)
    }
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
                .padding(16.dp, 0.dp, 8.dp, 0.dp),
            fontSize = 14.sp,
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            modifier = gradeModifier,
            fontSize = 14.sp,
            textAlign = TextAlign.Right
        )
    }
}