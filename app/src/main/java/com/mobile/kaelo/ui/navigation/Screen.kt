package com.mobile.kaelo.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object Progress : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object Achievements : Screen
    @Serializable
    data class AddEditHabit(val habitId: Long? = null) : Screen
}