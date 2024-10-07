package com.tstudioz.fax.fme.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.tstudioz.fax.fme.feature.iksica.IksicaViewModel
import com.tstudioz.fax.fme.feature.iksica.compose.IksicaCompose
import com.tstudioz.fax.fme.feature.studomat.compose.StudomatCompose
import com.tstudioz.fax.fme.feature.studomat.view.StudomatViewModel
import com.tstudioz.fax.fme.feature.timetable.view.TimetableCompose
import com.tstudioz.fax.fme.feature.timetable.view.TimetableViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.koinViewModel

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun MainCompose(startDestination: Any) {
    val navController = rememberNavController()
    AppTheme { MainNavHost(navController = navController, startDestination = startDestination ) }
}

val topLevelRoutes = listOf(
    TopLevelRoute(R.string.tab_iksica, Iksica, R.drawable.iksica),
    TopLevelRoute(R.string.tab_attendance, Attendance, R.drawable.attend),
    TopLevelRoute(R.string.tab_home, Home, R.drawable.command_line),
    TopLevelRoute(R.string.tab_timetable, TimeTable, R.drawable.cal),
    TopLevelRoute(R.string.tab_studomat, Studomat, R.drawable.studomat_icon),
)

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination:Any,
    iksicaViewModel: IksicaViewModel = koinViewModel(),
    studomatViewModel: StudomatViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
    attendanceViewModel: AttendanceViewModel = koinViewModel(),
    timetableViewModel: TimetableViewModel = koinViewModel()
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            MainTopAppBar(
                context = context,
                navController = navController,
                timetableViewModel = timetableViewModel
            )
        },
        bottomBar = {
            MainBottomBar(
                navController = navController,
                topLevelRoutes = topLevelRoutes,
                timetableViewModel = timetableViewModel
            )
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = startDestination, modifier = Modifier.padding(innerPadding),
            enterTransition = {
                // you can change whatever you want transition
                EnterTransition.None
            },
            exitTransition = {
                // you can change whatever you want transition
                ExitTransition.None
            }) {
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
                    onClick = {
                    }
                )
            }
        }
    }
}