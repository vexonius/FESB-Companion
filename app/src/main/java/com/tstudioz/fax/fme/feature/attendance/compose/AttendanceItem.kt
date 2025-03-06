package com.tstudioz.fax.fme.feature.attendance.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.theme_dark_surface
import com.tstudioz.fax.fme.compose.accentRed
import com.tstudioz.fax.fme.compose.theme_dark_primaryContainer
import com.tstudioz.fax.fme.compose.accentGreen
import com.tstudioz.fax.fme.feature.attendance.models.AttendanceEntry


@Composable
fun AttendanceItem(attendanceItems: List<AttendanceEntry>) {

    Column(
        modifier = Modifier
            .padding(24.dp, 8.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(theme_dark_primaryContainer)
            .padding(24.dp)
    ) {
        Text(
            text = attendanceItems.firstOrNull()?.`class` ?: "",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        attendanceItems.forEach { attendanceItem ->
            Column {
                Text(
                    attendanceItem.type,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    AttendanceProgressBar(
                        total = attendanceItem.total,
                        attended = attendanceItem.attended,
                        absent = attendanceItem.absent
                    )
                    Text(
                        text = stringResource(
                            R.string.attendance_stats_format,
                            attendanceItem.attended,
                            attendanceItem.total,
                            attendanceItem.required
                        ),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AttendanceProgressBar(
    total: Int,
    attended: Int,
    absent: Int,
    radius: Dp = 10.dp
) {
    val green = accentGreen
    val off = theme_dark_surface
    val red = accentRed
    Row {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(radius)
        ) {
            for (i in 0 until total) {
                drawCircle(
                    color = if (i < attended) green else if (i >= total - absent) red else off,
                    radius = radius.toPx(),
                    center = Offset(i * size.width / total + radius.toPx(), 0f),
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewAttendanceItem() {
    val attendanceItems = listOf(
        AttendanceEntry(
        ).apply {
            `class` = "Class 1"
            type = "Type 1"
            total = 10
            attended = 5
            absent = 2
            required = 8
        },
        AttendanceEntry(
        ).apply {
            `class` = "Class 1"
            type = "Type 2"
            total = 10
            attended = 5
            absent = 2
            required = 8
        }
    )
    AttendanceItem(attendanceItems)
}