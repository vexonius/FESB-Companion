package com.tstudioz.fax.fme.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import org.koin.androidx.compose.koinViewModel

val leftPadding = 10.dp
val ListItemStartPadding = 16.dp

@Composable
fun SettingsCompose(viewModel: SettingsViewModel = koinViewModel()) {
    val context = LocalContext.current

    AppTheme {
        Scaffold {
            Column(
                modifier = Modifier.padding(it)
            ) {
                CategoryTitle(title = "KORISNIK")
                val korisnik = viewModel.getLoggedInUser() ?: "Nepoznat korisnik"
                SettingsItem(
                    title = "Odjava",
                    supportText = "Prijavljeni ste kao $korisnik",
                    onClick = {
                        viewModel.deleteRealmAndSharedPrefs()
                        viewModel.deleteWebViewCookies()
                        viewModel.goToLoginScreen(context)
                    }
                )
                CategoryTitle(title = "SUDJELUJ U RAZVOJU APLIKACIJE")
                SettingsItem(
                    title = "Pošalji povratne informacije",
                    supportText = "Pomogni učiniti ovu aplikaciju još boljom",
                    onClick = {
                        viewModel.sendFeedbackEmail(
                            context,
                            "[FEEDBACK] FESB Companion",
                            ""
                        )
                    }
                )
                SettingsItem(
                    title = "Prijavi bug",
                    supportText = "Pomozi učiniti aplikaciju stabilnijom slanjem bug reporta",
                    onClick = {
                        viewModel.sendFeedbackEmail(
                            context,
                            "[BUG REPORT] FESB Companion",
                            ""
                        )
                    }
                )
                CategoryTitle(title = "O APLIKACIJI")
                SettingsItem(
                    title = "Verzija",
                    supportText = viewModel.getBuildVersion(context)
                )
                SettingsItem(
                    title = "Developeri",
                    supportText = "Tino Emer i Stipe Jurković"
                )
                SettingsItem(
                    title = "Privatnost podataka",
                    onClick = {
                        viewModel.launchCustomTab(context, "https://privacy.etino.dev/")
                    }
                )
                SettingsItem(
                    title = "Licence korištenih biblioteka",
                    onClick = {
                        viewModel.displayLicensesDialog(context, BottomSheetDialog(context))
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryTitle(title: String) {
    HorizontalDivider()
    Surface{
        Box(
            modifier = Modifier
                .padding(
                    start = ListItemStartPadding,
                    end = ListItemStartPadding,
                    top = 20.dp,
                    bottom = 0.dp
                )
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(start = leftPadding),
                color = colorResource(id = R.color.blue_nice),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    supportText: String="",
    onClick: () -> Unit = {}
) {
    ListItem(
        modifier = Modifier
            .clickable { onClick() },
        headlineContent = {
            Text(
                text = title,
                modifier = Modifier.padding(start = leftPadding)
            )
        }, supportingContent = {
            if (supportText.isNotEmpty()){
                Text(
                    text = supportText,
                    modifier = Modifier.padding(start = leftPadding)
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewSettingsCompose() {
    AppTheme {
        Column {
            CategoryTitle(title = "KORISNIK")
            SettingsItem(
                title = "Odjava",
                supportText = "Prijavljeni ste kao Tino Emer",
                onClick = {}
            )
        }
    }
}
