package com.tstudioz.fax.fme.feature.studomat.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.passGreen
import com.tstudioz.fax.fme.feature.home.compose.noRippleClickable
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject


@Composable
fun YearView(list: List<StudomatSubject>) {

    Column(
        modifier = Modifier
            .padding(24.dp, 12.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp, 24.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.enrolled_subjects),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Left,
            modifier = Modifier.padding(12.dp, 4.dp, 12.dp, 16.dp)
        )
        SubjectsList(list)
    }
}

@Composable
fun SubjectsList(list: List<StudomatSubject>) {

    list.forEachIndexed { index, it ->
        val opened = remember { mutableStateOf(false) }
        if (index != 0) HorizontalDivider(
            Modifier.padding(horizontal = 12.dp),
            color = MaterialTheme.colorScheme.outline
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { opened.value = !opened.value }
                .padding(12.dp, 8.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = it.name,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Left
            )
            Text(
                text = if (it.isPassed) it.grade else "-",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Right
            )
        }
        if (opened.value)
            Box(Modifier.noRippleClickable { opened.value = !opened.value }) { SubjectView(it) }
    }
}

@Composable
fun SubjectView(subject: StudomatSubject) {
    Column(
        Modifier.padding(14.dp, 8.dp, 14.dp, 20.dp)
    ) {
        SubjectText(text = stringResource(id = R.string.elective_group), value = subject.electiveGroup)
        SubjectText(text = stringResource(id = R.string.semester), value = subject.semester)
        SubjectText(text = stringResource(id = R.string.lectures), value = subject.lectures)
        SubjectText(text = stringResource(id = R.string.exercises), value = subject.exercises)
        SubjectText(text = stringResource(id = R.string.ects_enrolled), value = subject.ectsEnrolled)
        SubjectText(text = stringResource(id = R.string.is_taken), value = subject.isTaken)
        SubjectText(text = stringResource(id = R.string.status), value = subject.status, isPassed = subject.isPassed)
        SubjectText(text = stringResource(id = R.string.grade), value = subject.grade)
        SubjectText(text = stringResource(id = R.string.exam_date), value = subject.examDate)
    }
}

@Composable
fun SubjectText(text: String, value: String, isPassed: Boolean = false) {
    val gradeModifier = if (isPassed) {
        Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(10.dp))
            .background(passGreen)
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
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            modifier = gradeModifier,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Right
        )
    }
}