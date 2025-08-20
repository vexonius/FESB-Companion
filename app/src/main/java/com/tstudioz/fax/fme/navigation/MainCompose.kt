package com.tstudioz.fax.fme.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.feature.attendance.compose.AttendanceCompose
import com.tstudioz.fax.fme.feature.attendance.view.AttendanceViewModel
import com.tstudioz.fax.fme.feature.home.view.HomeTabCompose
import com.tstudioz.fax.fme.feature.home.view.HomeViewModel
import com.tstudioz.fax.fme.feature.iksica.compose.IksicaCompose
import com.tstudioz.fax.fme.feature.iksica.view.IksicaViewModel
import com.tstudioz.fax.fme.feature.studomat.compose.StudomatCompose
import com.tstudioz.fax.fme.feature.studomat.view.StudomatViewModel
import com.tstudioz.fax.fme.feature.timetable.view.TimetableViewModel
import com.tstudioz.fax.fme.feature.timetable.view.compose.TimetableCompose
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.koinViewModel

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun MainCompose(startDestination: Any) {
    val navController = rememberNavController()
    AppTheme { MainNavHost(navController = navController, startDestination = startDestination) }
}

val topLevelRoutes = listOf(
    TopLevelRoute(R.string.tab_iksica, Iksica, R.drawable.icon_iksica),
    TopLevelRoute(R.string.tab_attendance, Attendance, R.drawable.icon_attendance),
    TopLevelRoute(R.string.tab_home, Home, R.drawable.icon_home),
    TopLevelRoute(R.string.tab_timetable, TimeTable, R.drawable.icon_timetable),
    TopLevelRoute(R.string.tab_studomat, Studomat, R.drawable.icon_studomat),
)

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: Any,
    iksicaViewModel: IksicaViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
    attendanceViewModel: AttendanceViewModel = koinViewModel(),
    studomatViewModel: StudomatViewModel = koinViewModel(),
    timetableViewModel: TimetableViewModel = koinViewModel()
) {
    val internetAvailable = homeViewModel.internetAvailable.observeAsState().value == true

    Scaffold(
        bottomBar = {
            MainBottomBar(
                navController = navController,
                topLevelRoutes = topLevelRoutes,
                timetableViewModel = timetableViewModel
            )
        },
        floatingActionButton = {
            if (!internetAvailable) NoInternetIcon()
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                ExitTransition.None
            }) {
            composable<Iksica> {
                IksicaCompose(iksicaViewModel, innerPaddingValues = innerPadding)
            }
            composable<Attendance> {
                AttendanceCompose(attendanceViewModel, innerPaddingValues = innerPadding)
            }
            composable<Home> {
                HomeTabCompose(homeViewModel, innerPaddingValues = innerPadding)
            }
            composable<TimeTable> {
                TimetableCompose(timetableViewModel, innerPaddingValues = innerPadding)
            }
            composable<Studomat> {
                StudomatCompose(studomatViewModel, innerPaddingValues = innerPadding)
            }
        }
    }
}

@Preview
@Composable
fun NavbarPreview() {
    AppTheme {
        NavigationBar(
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
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
                    selected = true,
                    alwaysShowLabel = false,
                    onClick = {}
                )
            }
        }
    }
}