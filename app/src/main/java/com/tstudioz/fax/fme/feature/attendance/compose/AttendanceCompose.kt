package com.tstudioz.fax.fme.feature.attendance.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.compose.spacing
import com.tstudioz.fax.fme.feature.attendance.view.AttendanceViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun AttendanceCompose(attendanceViewModel: AttendanceViewModel) {

    val items = attendanceViewModel.attendanceListFull.observeAsState().value ?: emptyList()

    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val snackbarHostState = attendanceViewModel.snackbarHostState

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                attendanceViewModel.fetchAttendance()
            }

            else -> {}
        }
    }

    AppTheme {
        if (items.isNotEmpty()) {
            CreateAttendanceListView(attendanceViewModel, snackbarHostState)
        } else {
            EmptyView()
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

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun CreateAttendanceListView(attendanceViewModel: AttendanceViewModel, snackbarHostState: SnackbarHostState) {
    val list by attendanceViewModel.attendance.observeAsState(emptyList())
    val shownSemester by attendanceViewModel.shownSemester.observeAsState()

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Row {
                FilterButton(
                    selected = shownSemester == AttendanceViewModel.ShownSemester.FIRST ,
                    text = stringResource(id = R.string.first_semester),
                    onClick = { attendanceViewModel.showSemester(AttendanceViewModel.ShownSemester.FIRST) }
                )
                FilterButton(
                    selected = shownSemester == AttendanceViewModel.ShownSemester.SECOND,
                    text = stringResource(id = R.string.second_semester),
                    onClick = { attendanceViewModel.showSemester(AttendanceViewModel.ShownSemester.SECOND) }
                )
            }

            list.forEach() { item ->
                AttendanceItem(item)
            }
        }
    }
}

@Composable
fun FilterButton(
    selected: Boolean,
    text: String,
    onClick: () -> Unit
) {
    Spacer(modifier = Modifier.padding(MaterialTheme.spacing.small))
    TextButton(
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .background(color = colorResource(id = if (selected) R.color.brandeis_blue else R.color.raisin_black)),
        onClick = onClick
    ) {
        Text(text, color = colorResource(id = R.color.white))
    }
}