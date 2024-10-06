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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.database.models.AttendanceEntry
import com.tstudioz.fax.fme.feature.attendance.view.AttendanceViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun AttendanceCompose(attendanceViewModel: AttendanceViewModel) {

    val items = attendanceViewModel.attendanceList.observeAsState().value?.sortedBy { it.first().semester }
        ?: emptyList()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    val snackbarHostState = remember { attendanceViewModel.snackbarHostState }

    LaunchedEffect(lifecycleState) {
        // Do something with your state
        // You may want to use DisposableEffect or other alternatives
        // instead of LaunchedEffect
        when (lifecycleState) {
            Lifecycle.State.DESTROYED -> {}
            Lifecycle.State.INITIALIZED -> {}
            Lifecycle.State.CREATED -> {}
            Lifecycle.State.STARTED -> {}
            Lifecycle.State.RESUMED -> {
                attendanceViewModel.fetchAttendance()
            }
        }
    }

    AppTheme {
        if (items.isEmpty()) {
            EmptyView()
        } else {
            CreateAttendanceListView(items, snackbarHostState)
        }
    }
}

@Composable
fun EmptyView() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp)
        )
    }
}

@Composable
fun CreateAttendanceListView(items: List<List<AttendanceEntry>>, snackbarHostState: SnackbarHostState) {
    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }){
        LazyColumn(Modifier.padding(it)) {
            items(items.size) { index ->
                ListItem(headlineContent = {
                    Text(
                        text = items[index].first().`class` ?: ""
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