package com.mobile.kaelo.data.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.mobile.kaelo.MainActivity
import com.mobile.kaelo.data.local.datastore.ThemePreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject lateinit var prefs: ThemePreferences
    @Inject lateinit var scheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, tapIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, "kaelo_channel")
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Kaelo - Hora do Foco!")
            .setContentText("Opa, não esqueça de visualizar sua lista de hábitos hoje e complete-as para completar sua conquista e não perder sua ofensiva!")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Opa, não esqueça de visualizar sua lista de hábitos hoje e complete-as para completar sua conquista e não perder sua ofensiva!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)

        CoroutineScope(Dispatchers.IO).launch {
            val isEnabled = prefs.isReminderEnabled.first()
            val times = prefs.reminderTimes.first()
            scheduler.scheduleAlarms(times, isEnabled)
        }
    }
}