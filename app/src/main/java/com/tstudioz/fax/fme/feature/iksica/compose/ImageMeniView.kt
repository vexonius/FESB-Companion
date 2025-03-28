package com.tstudioz.fax.fme.feature.iksica.compose

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.iksica.models.MenzaLocation
import com.tstudioz.fax.fme.feature.iksica.view.IksicaViewModel
import com.tstudioz.fax.fme.feature.menza.models.Menza
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.HttpUrl
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(InternalCoroutinesApi::class)
@Composable
fun ImageMeniView(
    iksicaViewModel: IksicaViewModel,
    imageUrl: Pair<MenzaLocation, HttpUrl?>?,
    menza: Pair<MenzaLocation, Menza?>?
) {
    val imgUrl =
        if (imageUrl?.second != null && imageUrl.first == menza?.first) imageUrl.second else null
    BackHandler {
        iksicaViewModel.closeMenza()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(colorResource(R.color.chinese_black)),
        horizontalAlignment = Alignment.Start,
    ) {
        Card(
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .padding(24.dp, 53.dp, 24.dp, 24.dp)
        ) {
            AnimatedVisibility(true, enter = fadeIn(), modifier = Modifier) {
                Rotatable90Image(
                    imageUrl = imgUrl?.toString(),
                    contentDescription = "Menza"
                )

                //imageUrl?.let { RotatableZoomableImage(imageUrl = it, "Menza") }

                imgUrl?.toString()?.let { url->
                    formatTime(url)?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontSize = 10.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(bottomEnd = 15.dp))
                                .background(Color.Black.copy(alpha = 0.25f))
                                .padding(8.dp, 2.dp)
                        )
                    }
                }
            }
        }
        MeniComposeIksica(menza)
    }
}

fun formatTime(url: String): String? {
    try {
        val time = LocalDateTime.parse(
            url.split("/").last().split(".").first(),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'_'HH'i'mm'i'ss")
        )
        return time.format(DateTimeFormatter.ofPattern("dd.MM.yyyy.' 'HH:mm:ss"))
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}