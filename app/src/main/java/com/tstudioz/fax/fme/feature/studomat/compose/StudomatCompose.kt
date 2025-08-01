package com.tstudioz.fax.fme.feature.studomat.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.studomatBlue
import com.tstudioz.fax.fme.feature.studomat.view.StudomatViewModel
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import org.koin.compose.koinInject
import androidx.lifecycle.compose.currentStateAsState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StudomatCompose(studomatViewModel: StudomatViewModel) {

    val studomatData = studomatViewModel.studomatData.observeAsState().value
    val snackbarHostState = remember { studomatViewModel.snackbarHostState }
    val isRefreshing = studomatViewModel.isRefreshing.observeAsState().value
    val pullRefreshState = rememberPullRefreshState(isRefreshing == true, {
        studomatViewModel.getStudomatData(pulldownTriggered = true)
    })
    val openedWebview = remember { mutableStateOf(false) }
    val cookieJar = koinInject<MonsterCookieJar>()

    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState().value
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            studomatViewModel.getStudomatData()
        }
    }

    Scaffold(
        modifier = Modifier.pullRefresh(pullRefreshState),
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->

        if (openedWebview.value) {
            BackHandler { openedWebview.value = false }
            WebViewScreen(cookieJar)
            return@Scaffold
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Brush.verticalGradient(listOf(studomatBlue, Color.Transparent))),
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
                Text(
                    text = stringResource(id = R.string.tab_studomat),
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.padding(16.dp)
                )
                if (!studomatData.isNullOrEmpty()) {
                    StudomatContent(studomatData, onClick = { openedWebview.value = true })
                } else {
                    EmptyStudomatView()
                }
            }
        }
    }
}