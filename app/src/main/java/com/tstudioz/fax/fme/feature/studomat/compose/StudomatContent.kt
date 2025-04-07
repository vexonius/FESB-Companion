package com.tstudioz.fax.fme.feature.studomat.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.BalloonIndicatorType
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.studomatBlue
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYear
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYearInfo

@Composable
fun StudomatContent(studomatData: List<StudomatYear>, onClick: () -> Unit = {}) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        val list = studomatData.sortedByDescending { it.yearInfo.academicYear }
        val pageCount = list.size
        val pagerState = rememberPagerState(pageCount = { pageCount })

        list.getOrNull(pagerState.currentPage)?.yearInfo?.let { YearTitle(it) }
        DotIndicatorsStudomat(pageCount, pagerState)

        HorizontalPager(verticalAlignment = Alignment.Top, state = pagerState) { page ->
            Column(Modifier.wrapContentSize()) {
                YearView(list[page].subjects)
                Row(
                    Modifier
                        .padding(24.dp, 12.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .clickable { onClick() }
                        .background(colorResource(id = R.color.raisin_black))
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.open_icon),
                        contentDescription = stringResource(R.string.webview),
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
}


@Composable
fun YearTitle(yearInfo: StudomatYearInfo) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = yearInfo.courseName,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp)
        )
        Text(
            text = yearInfo.academicYear,
            fontSize = 15.sp,
            modifier = Modifier.padding(16.dp, 4.dp, 16.dp, 0.dp)
        )
    }
}

@Composable
fun DotIndicatorsStudomat(pageCount: Int, pagerState: PagerState) {
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
}