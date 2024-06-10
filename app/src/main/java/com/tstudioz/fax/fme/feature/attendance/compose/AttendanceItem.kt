package com.tstudioz.fax.fme.feature.attendance.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.AttendanceEntry


@Composable
fun AttendanceItem(attendanceItem: AttendanceEntry) {

    val percent = attendanceItem.attended.toFloat() / attendanceItem.total.toFloat()
    ListItem(
        headlineContent = {
            Text(
                (attendanceItem.vrsta ?: "").replaceFirstChar { it.uppercase() })
        },
        supportingContent = {
            Column {
                LinearProgressIndicator(
                    progress = { percent }, modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text((stringResource(R.string.attendance_required, attendanceItem.required ?: "")), fontSize = 12.sp)
            }
        },
        trailingContent = {
             Text(("${attendanceItem.attended}/${attendanceItem.total}"), fontSize = 14.sp)
        },
    )
}

@Preview
@Composable
fun AttendanceItem2() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .background(color = androidx.compose.ui.graphics.Color.White)
            .padding(16.dp)
    ) {
        Column {
            Text("Laboratorijske vježbe")
            LinearProgressIndicator(
                progress = { 0.5f }, modifier = Modifier.padding(top = 8.dp)
            )
            Text("Obavezno 10/10", modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        Text("9/10")
    }
}

@Preview
@Composable
fun PreviewAttendanceItem() {
    AttendanceItem(AttendanceEntry())
}