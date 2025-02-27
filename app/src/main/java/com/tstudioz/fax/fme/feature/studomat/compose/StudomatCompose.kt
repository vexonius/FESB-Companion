package com.tstudioz.fax.fme.feature.studomat.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.BalloonIndicatorType
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.blueNice
import com.tstudioz.fax.fme.feature.studomat.view.StudomatViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StudomatCompose(studomatViewModel: StudomatViewModel) {

    val allYears = studomatViewModel.allYears.observeAsState().value
    val loading = studomatViewModel.loading.observeAsState().value
    val snackbarHostState = remember { studomatViewModel.snackbarHostState }
    val isRefreshing = studomatViewModel.isRefreshing.observeAsState().value
    val pullRefreshState =
        rememberPullRefreshState(isRefreshing == true, { studomatViewModel.fetchAllYears(pulldownTriggered = true) })


    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentState
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                studomatViewModel.fetchAllYears()
            }

            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.pullRefresh(pullRefreshState),
        contentWindowInsets = WindowInsets(0.dp),
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
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(colors = listOf(blueNice, blueNice.copy(alpha = 0.1f)))
                ),
        ) {
            PullRefreshIndicator(
                isRefreshing == true,
                pullRefreshState,
                Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(2f),
                scale = true
            )
            Column {
                Row(
                    modifier = Modifier.background(Color.Transparent)
                ) {
                    Text(
                        text = stringResource(id = R.string.tab_studomat),
                        fontSize = 30.sp,
                        modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 16.dp)
                    )
                }
                LazyColumn {
                    if (!allYears.isNullOrEmpty()) {
                        item {
                            val list = allYears.sortedByDescending { it.first }
                            val pagerState = rememberPagerState(pageCount = { list.size })
                            val pageCount = list.size

                            Column {
                                Text(
                                    text = list[pagerState.currentPage].first,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp)
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.Center, modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp, 16.dp)
                            ) {
                                DotsIndicator(
                                    dotCount = pageCount,
                                    type = BalloonIndicatorType(
                                        dotsGraphic = DotGraphic(
                                            color = MaterialTheme.colorScheme.primary,
                                            size = 6.dp
                                        ),
                                        balloonSizeFactor = 1.7f
                                    ),
                                    dotSpacing = 20.dp,
                                    pagerState = pagerState
                                )
                            }

                            HorizontalPager(verticalAlignment = Alignment.Top, state = pagerState) { page ->
                                YearView(list[page].second)

                            }
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
}
