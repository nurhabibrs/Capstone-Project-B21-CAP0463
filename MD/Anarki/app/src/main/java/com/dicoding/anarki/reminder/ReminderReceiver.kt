package com.dicoding.anarki.reminder

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.dicoding.anarki.MainActivity
import com.dicoding.anarki.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "reminder"
        const val EXTRA_MESSAGE = "message"
        private const val REPEATING_REQUEST_CODE = 100
        private const val TIME_FORMAT = "HH:mm"
    }

    override fun onReceive(context: Context, intent: Intent) {
        sendNotification(context)
    }

    private fun sendNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java)

        val pendingIntent = TaskStackBuilder.create(context)
            .addParentStack(MainActivity::class.java)
            .addNextIntent(intent)
            .getPendingIntent(REPEATING_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_app)
            .setContentTitle(context.resources.getString(R.string.app_name))
            .setContentText(context.getString(R.string.notif_message))
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH)
            notificationBuilder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val builder = notificationBuilder.build()
        notificationManager.notify(1, builder)
    }

    fun setReminderRepeater(context: Context, time: String, message: String) {
        if (invalidDate(time, TIME_FORMAT))
            return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)

        val timeData = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeData[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeData[1]))
        calendar.set(Calendar.SECOND, 0)
        if (Calendar.getInstance().after(calendar)){
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, REPEATING_REQUEST_CODE, intent, 0)
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent)
        toastNotify(context, "On")
    }

    private fun invalidDate(time: String, timeFormat: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
            dateFormat.isLenient = false
            dateFormat.parse(time)
            false
        } catch (e: ParseException) {
            true
        }
    }

    fun unSetReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, REPEATING_REQUEST_CODE, intent, 0)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
        toastNotify(context, "Off")
    }

    private fun toastNotify(context: Context, message: String) {
        Toast.makeText(context, "Reminder : $message", Toast.LENGTH_SHORT).show()
    }
}