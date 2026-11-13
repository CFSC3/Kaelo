package com.mobile.kaelo.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mobile.kaelo.ui.screens.add_edit.AddEditHabitScreen
import com.mobile.kaelo.ui.screens.home.HomeScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        // Tela 1: Home
        composable<Screen.Home> {
            HomeScreen(
                onNavigateToAddHabit = {
                    navController.navigate(Screen.AddEditHabit(null))
                }
            )
        }

        composable<Screen.AddEditHabit> {
            AddEditHabitScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}