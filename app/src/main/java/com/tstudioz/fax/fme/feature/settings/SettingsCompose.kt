package com.tstudioz.fax.fme.feature.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun SettingsCompose(viewModel: SettingsViewModel = koinViewModel()) {
    val context = LocalContext.current

    AppTheme {
        Scaffold {
            LazyColumn(
                modifier = Modifier.padding(it)
            ) {
                item {
                    CategoryTitle(title = "Korisnik")
                }
                item {
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
                }
                item {
                    CategoryTitle(title = "SUDJELUJ U RAZVOJU APLIKACIJE")
                }
                item {
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
                }
                item {
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
                }
                item {
                    CategoryTitle(title = "O APLIKACIJI")
                }
                item {
                    SettingsItem(
                        title = "Verzija",
                        supportText = viewModel.getBuildVersion(context)
                    )
                }
                item {
                    SettingsItem(
                        title = "Developer",
                        supportText = "Tino Emer @ tstud.io",
                        onClick = {
                            viewModel.launchCustomTab(context, "http://tstud.io/")
                        }
                    )
                }
                item {
                    SettingsItem(
                        title = "Privatnost podataka",
                        supportText = "Vidi uvjete korištenja",
                        onClick = {
                            viewModel.launchCustomTab(context, "http://tstud.io/privacy")
                        }
                    )
                }
                item {
                    SettingsItem(
                        title = "Licence korištenih biblioteka",
                        supportText = "Prikaži licence",
                        onClick = {
                            viewModel.displayLicensesDialog(context, BottomSheetDialog(context))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryTitle(title: String) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                modifier = Modifier.padding(start = 10.dp),
                color = colorResource(id = R.color.blue_nice),
                fontWeight = FontWeight.Bold
            )
        }
    )
}

@Composable
fun SettingsItem(
    title: String,
    supportText: String,
    onClick: () -> Unit = {}
) {
    ListItem(
        modifier = Modifier
            .padding(start = 10.dp)
            .clickable { onClick() },
        headlineContent = {
            Text(
                text = title,
            )
        }, supportingContent = {
            Text(
                text = supportText,
            )
        })
}
