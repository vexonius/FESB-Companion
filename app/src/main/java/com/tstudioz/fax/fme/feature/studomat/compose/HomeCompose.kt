package com.tstudioz.fax.fme.feature.studomat.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.studomat.view.StudomatViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeCompose(studomatViewModel: StudomatViewModel) {

    val predmetList = studomatViewModel.predmetList.observeAsState().value
    val loadedTxt = studomatViewModel.loadedTxt.observeAsState().value
    val snackbarHostState = remember { studomatViewModel.snackbarHostState }
    val isRefreshing = studomatViewModel.isRefreshing.observeAsState().value
    val pullRefreshState = isRefreshing?.let { it ->
        rememberPullRefreshState(it, {
            studomatViewModel.selectedGodina.value?.let {
                studomatViewModel.getOdabranuGodinu(it, true)
            }
        })
    }

    pullRefreshState?.let {
        Modifier
            .pullRefresh(it)
            .padding(0.dp)
    }?.let {
        Scaffold(modifier = it,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->

            if ((loadedTxt == "fetching" || loadedTxt == "unset") && !isRefreshing) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .zIndex(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(64.dp)
                            .height(64.dp),
                        color = colorResource(id = R.color.StudomatBlue),
                        trackColor = colorResource(id = R.color.StudomatBlueLite),
                        strokeWidth = 4.dp,
                        strokeCap = StrokeCap.Round
                    )

                }
            }

            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(innerPadding),
            ) {
                if (!predmetList.isNullOrEmpty()) {
                    PullRefreshIndicator(
                        isRefreshing, pullRefreshState, Modifier
                            .align(Alignment.TopCenter)
                            .zIndex(2f), scale = true
                    )
                    LazyColumn(
                        modifier = Modifier.padding(16.dp, 10.dp, 16.dp, 0.dp)
                    ) {
                        item {
                            Column(Modifier.zIndex(1f)) {
                                Dropdown(studomatViewModel)
                                Row {
                                    Text(
                                        text = "Generirano: ${studomatViewModel.generated.value ?: ""}",
                                        Modifier.padding(8.dp, 4.dp)
                                    )
                                }
                                studomatViewModel.polozeniKrozUpisani.value?.let { it1 -> ProgressBarCompose(it1) }
                            }
                        }
                        items(predmetList.size) { item ->
                            PredmetCompose(predmet = predmetList[item])
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
    val godine = studomatViewModel.godine.observeAsState().value

    if (!godine.isNullOrEmpty()) {
        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember { mutableStateOf(godine[0].first) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextField(
                readOnly = true,
                value = selectedOptionText,
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
                expanded = expanded,
                onDismissRequest = { expanded = false },
                Modifier
                    .exposedDropdownSize()
            ) {
                if (godine.isNotEmpty()) {
                    godine.forEach {
                        if (it != godine[0]) {
                            HorizontalDivider(Modifier.padding(4.dp))
                        }
                        DropdownMenuItem(onClick = {
                            studomatViewModel.getOdabranuGodinu(it)
                            selectedOptionText = it.first
                            expanded = false
                        }, text = { Text(text = it.first) })
                    }
                }
            }
        }
    }
}