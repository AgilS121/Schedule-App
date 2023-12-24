package com.dicoding.courseschedule.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.data.DataRepository
import com.dicoding.courseschedule.ui.home.HomeActivity
import com.dicoding.courseschedule.util.ID_REPEATING
import com.dicoding.courseschedule.util.NOTIFICATION_CHANNEL_ID
import com.dicoding.courseschedule.util.NOTIFICATION_CHANNEL_NAME
import com.dicoding.courseschedule.util.NOTIFICATION_ID
import com.dicoding.courseschedule.util.executeThread
import java.util.Calendar

class DailyReminder : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        executeThread {
            val repository = DataRepository.getInstance(context)
            val courses = repository?.getTodaySchedule()

            courses?.let {
                if (it.isNotEmpty()) showNotification(context, it)
            }
        }
    }

    //TODO 12 : Implement daily reminder for every 06.00 a.m using AlarmManager
    fun setDailyReminder(context: Context) {
        val setAlarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val newIntent = Intent(context, DailyReminder::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, newIntent,
            PendingIntent.FLAG_IMMUTABLE)
        val newCalendar = Calendar.getInstance()
        newCalendar.timeInMillis = System.currentTimeMillis()
        newCalendar.set(Calendar.HOUR_OF_DAY, 6)
        newCalendar.set(Calendar.MINUTE, 0)
        newCalendar.set(Calendar.SECOND, 0)

        setAlarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, newCalendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    fun cancelAlarm(context: Context) {
        val setAlarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val newIntent = Intent(context, DailyReminder::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, newIntent,
            PendingIntent.FLAG_IMMUTABLE)
        pendingIntent.cancel()
        setAlarm.cancel(pendingIntent)
    }

    private fun showNotification(context: Context, content: List<Course>) {
        //TODO 13 : Show today schedules in inbox style notification & open HomeActivity when notification tapped
        val notificationStyle = NotificationCompat.InboxStyle()
        val timeString = context.resources.getString(R.string.notification_message_format)
        val setNotification = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        content.forEach {
            val courseData = String.format(timeString, it.startTime, it.endTime, it.courseName)
            notificationStyle.addLine(courseData)
        }


        val notificationsBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(context.getString(R.string.today_schedule))
            .setContentText(context.getString(R.string.message_text))
            .setStyle(notificationStyle)
            .setContentIntent(newPendingIntent(context))
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val newChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT
            )
            newChannel.enableVibration(true)
            newChannel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

            notificationsBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            setNotification.createNotificationChannel(newChannel)
        }

        setNotification.notify(NOTIFICATION_ID, notificationsBuilder.build())
    }

    private fun newPendingIntent(context: Context): PendingIntent {
        val newIntent = Intent(context, HomeActivity::class.java)
        val builder = TaskStackBuilder.create(context)
        builder.addNextIntentWithParentStack(newIntent)
        return builder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}