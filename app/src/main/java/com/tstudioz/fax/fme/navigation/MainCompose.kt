package com.tstudioz.fax.fme.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    studomatViewModel: StudomatViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
    attendanceViewModel: AttendanceViewModel = koinViewModel(),
    timetableViewModel: TimetableViewModel = koinViewModel()
) {
    Scaffold(
        bottomBar = {
            MainBottomBar(
                navController = navController,
                topLevelRoutes = topLevelRoutes,
                timetableViewModel = timetableViewModel
            )
        },
        floatingActionButton = {
            if (homeViewModel.internetAvailable.observeAsState().value != true) {
                var expand by remember { mutableStateOf(false) }
                var modifier = if (expand) Modifier
                else Modifier.width(56.dp)
                FloatingActionButton(
                    onClick = { expand = !expand },
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = modifier
                        .height(56.dp)
                        .padding(8.dp).animateContentSize(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (expand) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.no_internet),
                            contentDescription = stringResource(R.string.no_internet),
                            modifier = Modifier.size(24.dp)
                        )
                        if (expand) {
                            Text(
                                text = stringResource(R.string.no_internet),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
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
                    onClick = {}
                )
            }
        }
    }
}