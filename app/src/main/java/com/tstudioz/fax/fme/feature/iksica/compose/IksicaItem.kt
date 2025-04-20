package com.tstudioz.fax.fme.feature.iksica.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.accentBlue
import com.tstudioz.fax.fme.compose.contentColors
import com.tstudioz.fax.fme.compose.theme_dark_surface
import com.tstudioz.fax.fme.compose.greyishWhite
import com.tstudioz.fax.fme.compose.theme_dark_outline
import com.tstudioz.fax.fme.compose.theme_dark_outlineVariant
import com.tstudioz.fax.fme.compose.theme_dark_primaryContainer
import com.tstudioz.fax.fme.feature.iksica.daysAgoText
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.roundToTwo
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun IksicaItem(receipt: Receipt, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(16.dp, 8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                receipt.restaurant.trim(),
                color = MaterialTheme.contentColors.primary,
                style = MaterialTheme.typography.titleSmall,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.80f)
            )
            Text(
                text = stringResource(
                    id = R.string.minus_amount, receipt.subsidizedAmount.roundToTwo()
                ) + stringResource(id = R.string.currency),
                color = MaterialTheme.contentColors.primary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(0.20f),
                textAlign = TextAlign.End,
            )
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Row {
                val today = LocalDate.now()
                val daysAgo = ChronoUnit.DAYS.between(receipt.date, today).daysAgoText(LocalContext.current)
                Text(daysAgo, color = MaterialTheme.contentColors.tertiary, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.width(2.dp))
                Text(receipt.time, color = MaterialTheme.contentColors.tertiary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
    HorizontalDivider(Modifier.padding(horizontal = 10.dp), color = theme_dark_outline)
}