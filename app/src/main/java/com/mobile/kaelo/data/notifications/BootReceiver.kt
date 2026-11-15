package com.mobile.kaelo.data.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mobile.kaelo.data.local.datastore.ThemePreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var prefs: ThemePreferences
    @Inject lateinit var scheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val validActions = listOf(
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.MY_PACKAGE_REPLACED",
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED
        )

        if (intent.action in validActions) {
            CoroutineScope(Dispatchers.IO).launch {
                val isEnabled = prefs.isReminderEnabled.first()
                val times = prefs.reminderTimes.first()
                scheduler.scheduleAlarms(times, isEnabled)
            }
        }
    }
}