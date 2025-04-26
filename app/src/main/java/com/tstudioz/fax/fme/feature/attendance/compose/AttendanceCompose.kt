package com.tstudioz.fax.fme.feature.attendance.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.compose.contentColors
import com.tstudioz.fax.fme.compose.theme_dark_primaryContainer
import com.tstudioz.fax.fme.compose.theme_dark_secondaryContainer
import com.tstudioz.fax.fme.feature.attendance.ShownSemester
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

    if (items.isNotEmpty()) {
        CreateAttendanceListView(attendanceViewModel, snackbarHostState)
    } else {
        EmptyView()
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
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.contentColors.tertiary
        )
    }
}

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun CreateAttendanceListView(attendanceViewModel: AttendanceViewModel, snackbarHostState: SnackbarHostState) {
    val list by attendanceViewModel.attendance.observeAsState(emptyList())
    val shownSemester by attendanceViewModel.shownSemester.observeAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(id = R.string.tab_attendance),
                modifier = Modifier.padding(32.dp, 40.dp, 0.dp, 8.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.contentColors.primary
            )
            Row(
                Modifier.padding(horizontal = 32.dp)
            ) {
                FilterButton(
                    selected = shownSemester == ShownSemester.FIRST,
                    text = stringResource(id = R.string.first_semester),
                    onClick = { attendanceViewModel.showSemester(ShownSemester.FIRST) })
                FilterButton(
                    selected = shownSemester == ShownSemester.SECOND,
                    text = stringResource(id = R.string.second_semester),
                    onClick = { attendanceViewModel.showSemester(ShownSemester.SECOND) })
            }

            list.forEach { item ->
                AttendanceItem(item)
            }
        }
    }
}

@Composable
fun FilterButton(
    selected: Boolean, text: String, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .background(color = if (selected) theme_dark_secondaryContainer else theme_dark_primaryContainer),
    ) {
        Text(
            text = text,
            color = MaterialTheme.contentColors.primary,
            modifier = Modifier.padding(12.dp, 6.dp),
            fontSize = 14.sp
        )
    }
    Spacer(modifier = Modifier.padding(8.dp))
}

@Preview
@Composable
fun FilterButtonPreview() {
    AppTheme {
        FilterButton(selected = true, text = "First Semester", onClick = {})
    }
}