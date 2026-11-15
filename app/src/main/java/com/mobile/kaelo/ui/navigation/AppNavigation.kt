package com.mobile.kaelo.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mobile.kaelo.ui.screens.achievements.AchievementsScreen
import com.mobile.kaelo.ui.screens.home.HomeScreen
import com.mobile.kaelo.ui.screens.progress.ProgressScreen
import com.mobile.kaelo.ui.screens.settings.SettingsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home,
        modifier = modifier
    ) {
        composable<Screen.Home> {
            HomeScreen()
        }

        composable<Screen.Progress> {
            ProgressScreen()
        }

        composable<Screen.Settings> {
            SettingsScreen()
        }

        composable<Screen.Achievements> {
            AchievementsScreen()
        }
    }
}