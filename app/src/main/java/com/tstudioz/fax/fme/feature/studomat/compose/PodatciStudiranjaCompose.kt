package com.tstudioz.fax.fme.feature.studomat.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.R

@Preview
@Composable
fun ProgressBarCompose(pku: Pair<Int, Int> = Pair(0, 1)) {
    Row(
        Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (pku.first <= pku.second && pku.second != 0) {
            LinearProgressIndicator(
                progress = { pku.first.toFloat() / pku.second.toFloat() },
                modifier = Modifier.wrapContentWidth(),
                color = colorResource(id = R.color.StudomatBlue),
            )
            Text(
                text = pku.first.toString() + "/" + pku.second.toString(),
                Modifier
                    .wrapContentWidth()
                    .padding(10.dp, 0.dp),
            )
        }

    }
}