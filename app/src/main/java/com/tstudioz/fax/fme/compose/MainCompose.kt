package com.tstudioz.fax.fme.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.attendance.compose.AttendanceCompose
import com.tstudioz.fax.fme.feature.attendance.view.AttendanceViewModel
import com.tstudioz.fax.fme.feature.home.view.HomeTabCompose
import com.tstudioz.fax.fme.feature.home.view.HomeViewModel
import com.tstudioz.fax.fme.feature.iksica.IksicaViewModel
import com.tstudioz.fax.fme.feature.iksica.compose.IksicaCompose
import com.tstudioz.fax.fme.feature.studomat.compose.StudomatCompose
import com.tstudioz.fax.fme.feature.studomat.view.StudomatViewModel
import com.tstudioz.fax.fme.feature.timetable.view.TimetableCompose
import com.tstudioz.fax.fme.feature.timetable.view.TimetableViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@OptIn(InternalCoroutinesApi::class)
@Composable
fun MainCompose() {
    val navController = rememberNavController()

    AppTheme { SampleNavHost(navController = navController) }
}

@Serializable
data object Iksica

@Serializable
data object Studomat

@Serializable
data object Home

@Serializable
data object Attendance

@Serializable
data object TimeTable


data class TopLevelRoute<T : Any>(val nameId: Int, val route: T, val iconId: Int)

val topLevelRoutes = listOf(
    TopLevelRoute(R.string.tab_iksica, Iksica, R.drawable.iksica),
    TopLevelRoute(R.string.tab_studomat, Studomat, R.drawable.studomat_icon),
    TopLevelRoute(R.string.tab_home, Home, R.drawable.command_line),
    TopLevelRoute(R.string.tab_attendance, Attendance, R.drawable.attend),
    TopLevelRoute(R.string.tab_timetable, TimeTable, R.drawable.cal)
)

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SampleNavHost(
    navController: NavHostController,
    iksicaViewModel: IksicaViewModel = koinViewModel(),
    studomatViewModel: StudomatViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
    attendanceViewModel: AttendanceViewModel = koinViewModel(),
    timetableViewModel: TimetableViewModel = koinViewModel()
) {
    Scaffold(
        bottomBar = {
            BottomNavigation(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                topLevelRoutes.forEach { topLevelRoute ->
                    BottomNavigationItem(
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
                        }
                    )
                }
            }
        },
        topBar = {
            val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        topLevelRoutes.find {
                            it.route.toString() == (currentDestination?.split(".")?.lastOrNull() ?: "")
                        }?.nameId?.let { stringResource(it) } ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = Iksica, modifier = Modifier.padding(innerPadding)) {
            composable<Iksica> {
                IksicaCompose(iksicaViewModel)
            }
            composable<Attendance> {
                AttendanceCompose(attendanceViewModel)
            }
            composable<Home> {
                HomeTabCompose(homeViewModel)
            }
            composable<TimeTable> {
                TimetableCompose(timetableViewModel)
            }
            composable<Studomat> {
                StudomatCompose(studomatViewModel)
            }
        }
    }
}

@Preview
@Composable
fun NavbarPreview() {
    AppTheme {
        BottomNavigation(
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            topLevelRoutes.forEach { topLevelRoute ->
                BottomNavigationItem(
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
                    selected = true,
                    alwaysShowLabel = false,
                    onClick = {
                    }
                )
            }
        }
    }
}