package com.tstudioz.fax.fme.feature.studomat.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.studomat.view.StudomatViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StudomatCompose(studomatViewModel: StudomatViewModel) {

    val subjectList = studomatViewModel.subjectList.observeAsState().value
        ?.sortedBy { it.name }
        ?.sortedBy { it.semester }
    val loading = studomatViewModel.loading.observeAsState().value
    val snackbarHostState = remember { studomatViewModel.snackbarHostState }
    val isRefreshing = studomatViewModel.isRefreshing.observeAsState().value
    val pullRefreshState = isRefreshing?.let { it ->
        rememberPullRefreshState(it, {
            studomatViewModel.selectedYear.value?.let {
                studomatViewModel.getChosenYear(it, true)
            }
        })
    }

    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentState
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                studomatViewModel.selectedYear.value?.let { studomatViewModel.getChosenYear(it) }
            }
            else ->{}
        }
    }

    Scaffold(
        modifier = Modifier
            .pullRefresh(pullRefreshState ?: rememberPullRefreshState(false, {}))
            .padding(0.dp),
        bottomBar = {
            if (studomatViewModel.offline) {
                Row(
                    Modifier
                        .background(color = MaterialTheme.colorScheme.errorContainer)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Nema interneta", color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->

        if (loading == true && isRefreshing == false) {
            LinearProgressIndicator(
                Modifier
                    .fillMaxWidth()
                    .zIndex(2f),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Box(
            modifier = Modifier
                .wrapContentHeight()
                .padding(innerPadding),
        ) {
            if (pullRefreshState != null) {
                PullRefreshIndicator(
                    isRefreshing, pullRefreshState,
                    Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(2f), scale = true
                )
            }
            LazyColumn(
                modifier = Modifier.padding(16.dp, 10.dp, 16.dp, 0.dp)
            ) {
                item {
                    Column(Modifier.zIndex(1f)) {
                        Dropdown(studomatViewModel)
                        studomatViewModel.generated.value?.let {
                            Row {
                                Text(
                                    text = "Generirano: $it",
                                    Modifier.padding(8.dp, 4.dp)
                                )
                            }
                        }
                        ProgressBarCompose(subjectList?.count { it.isPassed } ?: 0, subjectList?.size ?: 0)
                    }
                }
                if (!subjectList.isNullOrEmpty()) {
                    items(subjectList.size) { item ->
                        SubjectView(subject = subjectList[item])
                    }
                } else {
                    item {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.no_data_icon),
                                contentDescription = "page_not_found",
                                modifier = Modifier
                                    .padding(12.dp, 80.dp, 12.dp, 12.dp)
                                    .size(80.dp)
                            )
                            Text("Nema podataka za prikazati.")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(studomatViewModel: StudomatViewModel) {
    val years = studomatViewModel.years.observeAsState().value

    if (!years.isNullOrEmpty()) {
        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember { mutableStateOf(years.firstOrNull()?.title) }

        ExposedDropdownMenuBox(
            expanded = expanded, onExpandedChange = {
                expanded = !expanded
            }, modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                readOnly = true,
                value = selectedOptionText ?: "",
                onValueChange = { },
                label = { Text("Godina") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .wrapContentWidth()
            )
            DropdownMenu(
                expanded = expanded, onDismissRequest = { expanded = false }, Modifier.exposedDropdownSize()
            ) {
                if (years.isNotEmpty()) {
                    years.forEach {
                        if (it != years.firstOrNull()) {
                            HorizontalDivider(Modifier.padding(4.dp))
                        }
                        DropdownMenuItem(onClick = {
                            studomatViewModel.getChosenYear(it)
                            selectedOptionText = it.title
                            expanded = false
                        }, text = { Text(text = it.title) })
                    }
                }
            }
        }
    }
}

@Composable
fun StudomatLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LinearProgressIndicator(
                Modifier
                    .fillMaxWidth()
                    .zIndex(2f)
            )
        }
    }
}