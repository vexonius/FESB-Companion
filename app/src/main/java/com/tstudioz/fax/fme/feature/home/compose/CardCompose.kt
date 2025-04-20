package com.tstudioz.fax.fme.feature.home.compose

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.lust
import com.tstudioz.fax.fme.compose.welcome2
import com.tstudioz.fax.fme.feature.home.view.sidePadding
import com.tstudioz.fax.fme.feature.iksica.compose.angledGradientBackground

@Composable
fun CardsCompose(menzaShow: MutableState<Boolean>) {
    Row(Modifier.padding(horizontal = sidePadding)) {
        val context = LocalContext.current
        Box(
            Modifier
                .weight(0.5f)
        ) {
            CardCompose(
                stringResource(id = R.string.menza_title),
                stringResource(id = R.string.menza_desc),
                welcome2,
                welcome2,
                onClick = {
                    menzaShow.value = true
                })
        }
        Box(
            Modifier
                .weight(0.5f)
        ) {
            CardCompose(
                stringResource(id = R.string.ugovori_title),
                stringResource(id = R.string.ugovori_desc),
                MaterialTheme.colorScheme.secondaryContainer,
                lust,
                onClick = {
                    val appPackageName = "com.ugovori.studentskiugovori"
                    val intent = context.packageManager.getLaunchIntentForPackage(appPackageName)
                    if (intent != null) {
                        context.startActivity(intent)
                    } else {
                        try {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=$appPackageName")
                                )
                            )
                        } catch (ex: ActivityNotFoundException) {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                                )
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun CardCompose(title: String, description: String, color1: Color, color2: Color, onClick: () -> Unit = { }) {
    Column(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .height(200.dp)
            .angledGradientBackground(
                colors = listOf(color1, color2),
                degrees = 60f,
                true
            )
            .padding(15.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = description,
            style = MaterialTheme.typography.titleSmall
        )
    }
}