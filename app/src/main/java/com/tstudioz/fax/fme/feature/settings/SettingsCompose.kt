package com.tstudioz.fax.fme.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.routing.SettingsRouter
import org.koin.androidx.compose.koinViewModel

val leftPadding = 10.dp
val listItemStartPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCompose(viewModel: SettingsViewModel = koinViewModel(), router: SettingsRouter) {
    val context = LocalContext.current

    AppTheme {
        BottomSheetScaffold(
            modifier = Modifier.fillMaxSize(),
            sheetPeekHeight = 0.dp,
            sheetContent = {
                if (viewModel.displayLicences.observeAsState().value == true) {
                    ModalBottomSheet(onDismissRequest = { viewModel.hideLicensesDialog() }) {
                        LazyColumn {
                            item {
                                LicenceItem(
                                    title = stringResource(id = R.string.ok_http_title),
                                    supportText = stringResource(id = R.string.ok_http_desc)
                                )
                            }
                            item {
                                LicenceItem(
                                    title = stringResource(id = R.string.jsoup_title),
                                    supportText = stringResource(id = R.string.jsoup_desc)
                                )
                            }
                            item {
                                LicenceItem(
                                    title = stringResource(id = R.string.realm_title),
                                    supportText = stringResource(id = R.string.realm_desc)
                                )
                            }
                            item {
                                LicenceItem(
                                    title = stringResource(id = R.string.privacy_policy_title),
                                    supportText = stringResource(id = R.string.privacy_policy_desc)
                                )
                            }

                        }
                    }
                }
            })
        {
            Column(
                modifier = Modifier.padding(it)
            ) {
                CategoryTitle(title = stringResource(id = R.string.category_user))
                SettingsItem(
                    title = stringResource(id = R.string.logout),
                    supportText = stringResource(
                        id = R.string.logged_in_as,
                        viewModel.username.observeAsState().value ?: ""
                    ),
                    onClick = {
                        viewModel.logout()
                        router.routeToLogin()
                    }
                )
                CategoryTitle(title = stringResource(id = R.string.contribute))
                SettingsItem(
                    title = stringResource(id = R.string.send_feedback),
                    supportText = stringResource(id = R.string.help_improve_app),
                    onClick = {
                        router.sendEmail(viewModel.getSupportEmailModalModel())
                    }
                )
                SettingsItem(
                    title = stringResource(id = R.string.report_bug),
                    supportText = stringResource(id = R.string.help_stabilize_app),
                    onClick = {
                        router.sendEmail(viewModel.getBugReportEmailModalModel())
                    }
                )
                CategoryTitle(title = stringResource(id = R.string.about_app))
                SettingsItem(
                    title = stringResource(id = R.string.version),
                    supportText = viewModel.version.observeAsState().value ?: ""
                )
                SettingsItem(
                    title = stringResource(id = R.string.developers),
                    supportText = stringResource(id = R.string.developer_names)
                )
                SettingsItem(
                    title = stringResource(id = R.string.data_privacy),
                    supportText = null,
                    onClick = {
                        viewModel.launchCustomTab(context)
                    }
                )
                SettingsItem(
                    title = stringResource(id = R.string.library_licenses),
                    supportText = null,
                    onClick = {
                        viewModel.displayLicensesDialog()
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryTitle(title: String) {
    HorizontalDivider()
    Box(
        modifier = Modifier
            .padding(
                start = listItemStartPadding,
                end = listItemStartPadding,
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

@Composable
fun SettingsItem(
    title: String,
    supportText: String?,
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
        },
        supportingContent = {
            supportText?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(start = leftPadding)
                )
            }
        }
    )
}

@Composable
fun LicenceItem(
    title: String,
    supportText: String?
) {
    Column(modifier = Modifier.padding(16.dp)) {
        ListItem(headlineContent = { Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold) })
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(10.dp, 15.dp)
        ) { Text(text = supportText ?: "", modifier = Modifier.padding(start = leftPadding)) }
    }
    HorizontalDivider()
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
