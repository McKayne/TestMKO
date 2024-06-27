package com.elnico.testmko

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat
import java.util.SortedMap
import java.util.TreeMap


class InterceptorService: AccessibilityService() {

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED or AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED or AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT or AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED or AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT or AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY or AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED or AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED or AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT or AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        info.notificationTimeout = 300
        serviceInfo = info
    }

    /*override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        println("Now received some event")

        val data = event?.parcelableData
        /*val notification = data as? Notification
        val packageName = event?.packageName.toString()
        val ticker = notification?.tickerText?.toString()*/
        val notificationText = event?.text.toString()

        println(event)
        println(data)
        println(packageName)
        //println(ticker)
        println(notificationText)

        showTestNotification(packageName, notificationText)
    }*/

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val eventType = event.eventType

        // Check if the event type is a text change event
        if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED || eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
            val text = event.text.toString().replace("[", "").replace("]", "")

            val app = foregroundApp()
            if (app == "com.instagram.android") {
                if (text != null && text.isNotEmpty()) {
                    StringValueHolder.loggedInUserName = text
                    (application as InterceptApplication).appendNewName(text)
                    showTestNotification("Результат", "Получено имя $text")
                }
            }
        }
    }

    override fun onInterrupt() {
        startService(Intent(this, InterceptorService::class.java))
        //setRingerMode(NotificationService_API18.this, AudioManager.RINGER_MODE_SILENT);
    }

    private fun foregroundApp(): String? {
        val usm = this.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        val appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)

        if (appList != null && appList.size > 0) {
            val mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
            for (usageStats in appList) {
                mySortedMap[usageStats.lastTimeUsed] = usageStats
            }
            if (mySortedMap != null && !mySortedMap.isEmpty()) {
                return mySortedMap[mySortedMap.lastKey()]!!.packageName
            }
        }

        val appProcesses = (getSystemService(ACTIVITY_SERVICE) as ActivityManager).runningAppProcesses
        for (appProcess in appProcesses) {
            if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return appProcess.processName
            }
        }

        return null
    }

    private fun showTestNotification(packageName: String, text: String) {
        if (text != "[]") {
            val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // The id of the channel.
            val id = "my_channel_01"

            // The user-visible name of the channel.
            val name: CharSequence = "Test-Channel"

            // The user-visible description of the channel.
            val description = "Test-desc"

            val importance = NotificationManager.IMPORTANCE_MAX

            val mChannel = NotificationChannel(id, name, importance)

            // Configure the notification channel.
            mChannel.description = description

            mChannel.enableLights(true)
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.lightColor = Color.RED

            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)

            mNotificationManager.createNotificationChannel(mChannel)

            ////

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this)
                .setContentText(text)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setChannelId(id)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
        }
    }
}