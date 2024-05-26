package com.tstudioz.fax.fme.feature.attendance.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.database.models.Dolazak

@Composable
fun AttendanceCompose(attendanceItems: Map<String, MutableList<Dolazak>>) {

    AppTheme {
        LazyColumn {
            items(
                attendanceItems.filter { it.value.all { it1 -> it1.semestar == 1 } }.toSortedMap()
                    .map { it.key }) { item ->
                ListItem(headlineContent = { Text(text = item) },
                    supportingContent = { Column {
                        (attendanceItems[item]?.toList() ?: emptyList()).forEach {
                            AttendanceItem(it)
                        }
                    } }
                )
                Divider( modifier = Modifier.padding(4.dp))
            }
            items(
                attendanceItems.filter { it.value.all { it1 -> it1.semestar == 2 } }.toSortedMap()
                    .map { it.key }) { item ->
                ListItem(headlineContent = { Text(text = item) },
                    supportingContent = { Column {
                        (attendanceItems[item]?.toList() ?: emptyList()).forEach {
                            AttendanceItem(it)
                        }
                    } }
                )
                Divider( modifier = Modifier.padding(4.dp))
            }
        }
    }
}