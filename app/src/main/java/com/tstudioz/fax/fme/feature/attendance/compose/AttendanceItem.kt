package com.tstudioz.fax.fme.feature.attendance.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.AttendanceEntry


@Composable
fun AttendanceItem(attendanceItem: AttendanceEntry) {
    val type = (attendanceItem.type ?: "").replaceFirstChar { it.uppercase() }
    ListItem(
        headlineContent = {
            Text(type)
        },
        supportingContent = {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                AttendanceProgressBar(
                    total = attendanceItem.total,
                    attended = attendanceItem.attended,
                    absent = attendanceItem.absent
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    (stringResource(R.string.attendance_required, attendanceItem.required ?: "")),
                    fontSize = 12.sp
                )
            }
        },
        trailingContent = {
            Text(("${attendanceItem.attended}/${attendanceItem.total}"), fontSize = 14.sp)
        },
    )
}

@Composable
fun AttendanceProgressBar(
    total: Int,
    attended: Int,
    absent: Int,
    thickness: Dp = 5.dp,
    spacing: Dp = 3.dp
) {
    val green = colorResource(id = R.color.vibrant_green)
    val red = colorResource(id = R.color.rosso_corsa)
    Row {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(thickness)
        ) {
            for (i in 0 until total) {
                val start = i * size.width / total
                val end = (i + 1) * size.width / total
                val isLitGood = i < attended
                val isLitBad = i >= total - absent
                drawLine(
                    color = if (isLitGood) green else if (isLitBad) red else Color.Gray,
                    strokeWidth = thickness.toPx(),
                    start = Offset(start, thickness.toPx() / 2),
                    end = Offset(end - spacing.toPx(), thickness.toPx() / 2),
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewAttendanceItem() {
    AttendanceItem(AttendanceEntry())
}