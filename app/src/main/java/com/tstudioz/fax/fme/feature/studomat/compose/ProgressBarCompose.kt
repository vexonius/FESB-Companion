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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.R

@Composable
fun ProgressBarCompose(passed: Int, total: Int) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (passed <= total && total != 0) {
            LinearProgressIndicator(
                progress = { passed.toFloat() / total.toFloat() },
                modifier = Modifier.wrapContentWidth(),
                color = colorResource(id = R.color.Endeavour),
            )
            Text(
                text = "$passed/$total",
                Modifier
                    .wrapContentWidth()
                    .padding(10.dp, 0.dp),
            )
        }

    }
}