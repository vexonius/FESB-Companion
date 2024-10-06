package com.tstudioz.fax.fme.navigation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.timetable.view.TimetableViewModel
import com.tstudioz.fax.fme.view.activities.SettingsActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(ExperimentalMaterial3Api::class, InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun MainTopAppBar(context: Context, navController: NavHostController, timetableViewModel: TimetableViewModel) {
    val currentDestination =
        navController.currentBackStackEntryAsState().value?.destination?.route?.split(".")?.lastOrNull() ?: ""
    TopAppBar(
        colors = if (currentDestination != "Home") {
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
            )
        } else {
            TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(id = R.color.dark_cyan),
                titleContentColor = Color.White
            )
        },
        title = {
            Text(
                topLevelRoutes.find {
                    it.route.toString() == currentDestination
                }?.nameId?.let { stringResource(it) } ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            if (currentDestination == "TimeTable") {
                IconButton(
                    onClick = { timetableViewModel.showWeekChooseMenu() },
                    colors = IconButtonDefaults.iconButtonColors().copy(
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.timetable_date_select_icon),
                        contentDescription = stringResource(id = R.string.change_week),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            IconButton(
                onClick = { context.startActivity(Intent(context, SettingsActivity::class.java)) },
                colors = IconButtonDefaults.iconButtonColors().copy(
                    contentColor = Color.White
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.settings_icon),
                    contentDescription = stringResource(id = R.string.settings),
                    modifier = Modifier.size(30.dp)
                )
            }
        },
    )
}