package com.tstudioz.fax.fme.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
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
import com.tstudioz.fax.fme.feature.iksica.view.IksicaViewModel
import com.tstudioz.fax.fme.feature.timetable.view.TimetableViewModel
import com.tstudioz.fax.fme.routing.HomeRouter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(ExperimentalMaterial3Api::class, InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun MainTopAppBar(
    router: HomeRouter,
    navController: NavHostController,
    timetableViewModel: TimetableViewModel,
    iksicaViewModel: IksicaViewModel
) {
    val currentDestination =
        navController.currentBackStackEntryAsState().value?.destination?.route?.split(".")?.lastOrNull() ?: ""
    if (currentDestination != "Iksica" && currentDestination != "Attendance") {
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
        navigationIcon = {
            val imageName = iksicaViewModel.imageName.observeAsState().value
            if (currentDestination == "Iksica" && imageName != null) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, "",
                    Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null) {
                            iksicaViewModel.closeImageMenza()
                        }
                        .padding(7.dp)
                        .size(26.dp))
            }
        },
        title = {
            val imageName = iksicaViewModel.imageName.observeAsState().value
            val tabName =
                topLevelRoutes.find { it.route.toString() == currentDestination }?.nameId?.let { stringResource(it) }
                    ?: ""
            Text(
                if (currentDestination == "Iksica" && !imageName.isNullOrEmpty()) {
                    imageName
                } else {
                    tabName
                },
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
                onClick = { router.routeToSettings() },
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
}