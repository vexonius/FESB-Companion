package com.tstudioz.fax.fme.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tstudioz.fax.fme.feature.timetable.view.TimetableViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun MainBottomBar(
    navController: NavHostController,
    topLevelRoutes: List<TopLevelRoute<out Any>>,
    timetableViewModel: TimetableViewModel
) {
    NavigationBar(
        contentColor = MaterialTheme.colorScheme.onSurface,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val currentDestinationTop =
            navController.currentBackStackEntryAsState().value?.destination?.route?.split(".")?.lastOrNull()
                ?: ""
        topLevelRoutes.forEach { topLevelRoute ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(topLevelRoute.iconId),
                        contentDescription = stringResource(topLevelRoute.nameId),
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(topLevelRoute.nameId),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                },
                selected = currentDestination?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true,
                alwaysShowLabel = false,
                onClick = {
                    if (currentDestinationTop != topLevelRoute.route.toString()) {
                        navController.navigate(topLevelRoute.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    } else if (currentDestinationTop == topLevelRoute.route.toString()) {
                        when (topLevelRoute.route) {
                            TimeTable -> timetableViewModel.showWeekChooseMenu()
                        }
                    }
                }
            )
        }
    }
}