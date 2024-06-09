package com.tstudioz.fax.fme.feature.attendance.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.database.models.Dolazak

@Composable
fun AttendanceCompose(attendanceItems: LiveData<List<List<Dolazak>>>) {

    AppTheme {
        if (attendanceItems.observeAsState().value?.isEmpty() == true) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp)
                )
            }
        } else {
            val items = attendanceItems.observeAsState().value ?: emptyList()
            LazyColumn {
                items(items.filter { it.all { it1 -> it1.semestar == 1 } }.size) { index ->
                    ListItem(headlineContent = {
                        Text(
                            text = items[index].first().predmet ?: ""
                        )
                    },
                        supportingContent = {
                            Column {
                                (items[index]).forEach {
                                    AttendanceItem(it)
                                }
                            }
                        }
                    )
                    Divider(modifier = Modifier.padding(4.dp))
                }
                items(items.filter { it.all { it1 -> it1.semestar == 2 } }.size) { index ->
                    ListItem(headlineContent = {
                        Text(
                            text = items[index].first().predmet ?: ""
                        )
                    },
                        supportingContent = {
                            Column {
                                (items[index]).forEach {
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
}