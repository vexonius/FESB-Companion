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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.AttendanceEntry


@Composable
fun AttendanceItem(attendanceItems: List<AttendanceEntry>) {
    val type = (attendanceItems.firstOrNull()?.type ?: "").replaceFirstChar { it.uppercase() }
    val background = colorResource(id = R.color.raisin_black)
    Column(modifier = Modifier
        .padding(16.dp)
        .clip(RoundedCornerShape(30.dp))
        .background(background)
        .padding(16.dp)
    ){
        Text(
            text = attendanceItems.firstOrNull()?.`class` ?: "",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        attendanceItems.forEach() { attendanceItem ->
            Column(
                modifier = Modifier
            ) {

                Text(type, fontSize = 14.sp)
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    AttendanceProgressBar(
                        total = attendanceItem.total,
                        attended = attendanceItem.attended,
                        absent = attendanceItem.absent
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Odrađeno ${attendanceItem.attended}/${attendanceItem.total} " +
                                (stringResource(
                                    R.string.attendance_required,
                                    attendanceItem.required?.split(" od")?.firstOrNull() ?: ""
                                )),
                        fontSize = 11.sp
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
    thickness: Dp = 10.dp,
    spacing: Dp = 3.dp
) {
    val green = colorResource(id = R.color.ufo_green)
    val off = colorResource(id = R.color.chinese_black)
    val red = colorResource(id = R.color.crayola)
    Row {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(thickness)
        ) {
            for (i in 0 until total) {
                drawCircle(
                    color = if (i < attended) green else if (i >= total - absent) red else off,
                    radius = thickness.toPx(),
                    center = Offset(((i + 0.5) * size.width / total).toFloat(), thickness.toPx() / 2),
                )
            }
        }
    }
}