package com.mobile.kaelo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobile.kaelo.data.local.datastore.ThemePreferences
import com.mobile.kaelo.ui.screens.MainScreen
import com.mobile.kaelo.ui.theme.KaeloTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreferences: ThemePreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            val isDarkMode by themePreferences.isDarkMode.collectAsStateWithLifecycle(initialValue = false)

            KaeloTheme(darkTheme = isDarkMode) {
                MainScreen()
            }
        }
    }
}