package com.tstudioz.fax.fme.feature.attendance.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.database.models.Dolazak

@Composable
fun AttendanceCompose(attendanceItems: List<List<Dolazak>>) {

    AppTheme {
        LazyColumn {
            items(attendanceItems.filter { it.all { it1 -> it1.semestar == 1 } }.size) { index ->
                ListItem(headlineContent = { Text(text = attendanceItems[index].first().predmet ?: "Ime Predmeta") },
                    supportingContent = {
                        Column {
                            (attendanceItems[index]).forEach {
                                AttendanceItem(it)
                            }
                        }
                    }
                )
                Divider(modifier = Modifier.padding(4.dp))
            }
            items(attendanceItems.filter { it.all { it1 -> it1.semestar == 2 } }.size) { index ->
                ListItem(headlineContent = { Text(text = attendanceItems[index].first().predmet ?: "Ime Predmeta") },
                    supportingContent = {
                        Column {
                            (attendanceItems[index]).forEach {
                                AttendanceItem(it)
                            }
                        }
                    }
                )
                Divider(modifier = Modifier.padding(4.dp))
            }
        }
    }
}