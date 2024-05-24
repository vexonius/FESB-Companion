package com.tstudioz.fax.fme.feature.attendance.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.database.models.Dolazak

@Composable
fun AttendanceCompose(attendanceItems: Map<String, MutableList<Dolazak>>) {

    AppTheme {
        LazyColumn {
            items(attendanceItems.map { it.key }) { item ->
                Text(text = item)
                Column {
                    (attendanceItems[item]?.toList() ?: emptyList()).forEach() {
                        AttendanceItem(it)
                    }
                }
            }
        }
    }
}