package com.tstudioz.fax.fme.feature.menza.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.menza.models.MenzaLocation
import com.tstudioz.fax.fme.feature.menza.models.Menza
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.HttpUrl
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(InternalCoroutinesApi::class)
@Composable
fun ImageMeniView(
    menzaViewModel: MenzaViewModel,
    imgUrl:HttpUrl?,
    menza: Pair<MenzaLocation, Menza?>?
) {
    BackHandler {
        menzaViewModel.closeMenza()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .padding(24.dp, 12.dp, 24.dp, 24.dp)
                .clip(RoundedCornerShape(15.dp))
        ) {
            if (menza?.first?.cameraName?.isNotEmpty() == true) {
                Rotatable90Image(
                    imageUrl = imgUrl?.toString(),
                    contentDescription = "Menza"
                )
                imgUrl?.toString()?.let { url ->
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
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.no_camera),
                    contentDescription = "Camera",
                    tint = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .padding(8.dp, 2.dp)
                        .size(40.dp)
                )
            }
        }
        MeniComposeIksica(menza)
    }
}

private fun formatTime(url: String): String? {
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