package com.tstudioz.fax.fme.feature.studomat.compose

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.BalloonIndicatorType
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.studomatBlue
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYearInfo
import com.tstudioz.fax.fme.feature.studomat.view.StudomatViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StudomatCompose(studomatViewModel: StudomatViewModel) {

    val studomatData = studomatViewModel.studomatData.observeAsState().value
    val snackbarHostState = remember { studomatViewModel.snackbarHostState }
    val isRefreshing = studomatViewModel.isRefreshing.observeAsState().value
    val pullRefreshState =
        rememberPullRefreshState(isRefreshing == true, { studomatViewModel.getStudomatData(pulldownTriggered = true) })
    val openedWebview = remember { mutableStateOf(false) }

    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentState
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                studomatViewModel.getStudomatData()
            }

            else -> {}
        }
    }

    BackHandler {
        if (openedWebview.value) {
            openedWebview.value = false
        }
    }

    Scaffold(
        modifier = Modifier.pullRefresh(pullRefreshState),
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->

        if (openedWebview.value) {
            WebViewScreen(studomatViewModel)
            return@Scaffold
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            studomatBlue,
                            Color.Transparent
                        )
                    )
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
                StudomatContent(studomatData, onClick = {
                    openedWebview.value = true
                })
            }
        }
    }
}

@Composable
fun StudomatContent(studomatData: List<Pair<StudomatYearInfo, List<StudomatSubject>>>?, onClick: () -> Unit = {}) {
    LazyColumn(Modifier.fillMaxSize()) {
        if (!studomatData.isNullOrEmpty()) {
            item {
                val list = studomatData.sortedByDescending { it.first.year }
                val pagerState = rememberPagerState(pageCount = { list.size })
                val pageCount = list.size

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = list[pagerState.currentPage].first.courseName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp)
                    )
                    Text(
                        text = list[pagerState.currentPage].first.year,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(16.dp, 4.dp, 16.dp, 0.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Center, modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    DotsIndicator(
                        dotCount = pageCount,
                        type = BalloonIndicatorType(
                            dotsGraphic = DotGraphic(
                                color = lerp(studomatBlue, Color.White, 0.5f),
                                size = 6.dp
                            ),
                            balloonSizeFactor = 1.7f
                        ),
                        dotSpacing = 20.dp,
                        pagerState = pagerState
                    )
                }

                HorizontalPager(verticalAlignment = Alignment.Top, state = pagerState) { page ->
                    Column {
                        YearView(list[page].second)
                        Row(
                            Modifier
                                .padding(24.dp, 12.dp)
                                .clip(RoundedCornerShape(30.dp))
                                .clickable { onClick() }
                                .background(colorResource(id = R.color.raisin_black))
                                .padding(24.dp)
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.open_icon),
                                contentDescription = "webview",
                                modifier = Modifier.padding(0.dp, 0.dp, 4.dp, 0.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.open_webview),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Left,
                            )
                        }
                    }
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
                    Text(stringResource(id = R.string.no_data))
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(studomatViewModel: StudomatViewModel) {
    AndroidView(
        factory = { context ->
            val webview = WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()

                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(true)
            }
            studomatViewModel.loadCookieToWebview(webview)
            webview
        },
        modifier = Modifier.background(Color.Black).fillMaxSize(),
    )
}