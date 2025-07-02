package com.tstudioz.fax.fme.feature.menza.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.BalloonIndicatorType
import com.tstudioz.fax.fme.feature.menza.menzaLocations
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(ExperimentalMaterial3Api::class, InternalCoroutinesApi::class)
@Composable
fun MenzaCompose(menzaViewModel: MenzaViewModel) {

    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState().value
    val imageUrl = menzaViewModel.images.observeAsState().value
    val menzas = menzaViewModel.menza.observeAsState().value

    Surface(modifier = Modifier.fillMaxSize()) {
        val pageCount = menzaLocations.size
        val state = rememberPagerState(
            initialPage = (pageCount.div(2)),
            pageCount = { pageCount }
        )
        DisposableEffect(lifecycleState) {
            onDispose {
                menzaViewModel.closeMenza()
            }
        }
        LaunchedEffect(state.settledPage) {
            menzaViewModel.updateMenzaUrl(menzaLocations[state.settledPage])
        }
        Column {
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(0.dp, 24.dp, 0.dp, 24.dp)
            ) {
                DotsIndicator(
                    dotCount = pageCount,
                    type = BalloonIndicatorType(
                        dotsGraphic = DotGraphic(
                            color = Color.White,
                            size = 6.dp
                        ),
                        balloonSizeFactor = 1.7f
                    ),
                    dotSpacing = 20.dp,
                    pagerState = state,
                )
            }
            HorizontalPager(state, pageSpacing = 16.dp) {
                val meni = menzas?.get(it)
                ImageMeniView(menzaViewModel, imageUrl, meni)
            }
        }
    }
}