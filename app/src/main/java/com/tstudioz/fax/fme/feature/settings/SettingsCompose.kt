package com.tstudioz.fax.fme.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.compose.theme_dark_outline
import com.tstudioz.fax.fme.compose.theme_dark_secondaryContainer
import com.tstudioz.fax.fme.compose.theme_dark_surface
import com.tstudioz.fax.fme.routing.SettingsRouter
import org.koin.androidx.compose.koinViewModel

val leftPadding = 10.dp
val listItemStartPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCompose(viewModel: SettingsViewModel = koinViewModel(), router: SettingsRouter) {
    AppTheme {
        BottomSheetScaffold(
            containerColor = theme_dark_surface,
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
                Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState())
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
                CategoryTitle(title = stringResource(id = R.string.customizations))
                SettingsCheckbox(
                    title = stringResource(id = R.string.make_events_glow),
                    supportText = stringResource(id = R.string.make_event_glow_description),
                    checked = viewModel.eventsGlowing.observeAsState().value == true,
                    onCheckedChange = { viewModel.makeEventsGlow(it) },
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
                        router.openCustomTab(SettingsViewModel.pivacyUrl)
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
    HorizontalDivider(color = theme_dark_outline)
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
            color = theme_dark_secondaryContainer,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }

}

@Composable
fun SettingsItem(
    title: String,
    supportText: String?,
    onClick: () -> Unit = {},
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
fun SettingsCheckbox(
    title: String,
    supportText: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
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
        },
        trailingContent = {
            val darkenBy = 0.6f
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSecondary.darken(darkenBy),
                    checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer.darken(darkenBy),
                    checkedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                    uncheckedBorderColor = MaterialTheme.colorScheme.secondaryContainer.darken(darkenBy),
                )
            )
        }
    )
}

fun Color.darken(darkenBy: Float = 0.3f): Color {
    return copy(
        red = red * darkenBy,
        green = green * darkenBy,
        blue = blue * darkenBy,
        alpha = alpha
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

@Preview
@Composable
fun PreviewSettingsToggleCompose() {
    val mutableStateOf = remember { mutableStateOf(false) }
    AppTheme {
        Column {
            CategoryTitle(title = "KORISNIK")
            SettingsCheckbox(
                title = "Odjava",
                supportText = "Prijavljeni ste kao Tino Emer",
                onCheckedChange = { mutableStateOf.value = !mutableStateOf.value },
                checked = mutableStateOf.value
            )
        }
    }
}
