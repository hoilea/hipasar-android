package com.uberando.hipasar.notification

import android.app.*
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.uberando.hipasar.R

class NotificationBuilder(private val context: Context) {

  private var notificationTitle: String? = null

  private var notificationText: String? = null

  @DrawableRes private var notificationSmallIcon: Int = R.mipmap.ic_launcher

  private var notificationIntent: PendingIntent? = null

  private val notificationManager =
    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

  private var notification: Notification? = null

  @RequiresApi(Build.VERSION_CODES.N)
  private var notificationImportance: Int = NotificationManager.IMPORTANCE_HIGH

  private var notificationAutoCancel: Boolean = true

  private var notificationId: Int = 0

  fun setNotificationTitle(title: String) {
    notificationTitle = title
  }

  fun setNotificationText(text: String) {
    notificationText = text
  }

  fun setNotificationSmallIcon(@DrawableRes resId: Int) {
    notificationSmallIcon = resId
  }

  fun setNotificationDeepLink(@NavigationRes graph: Int, @IdRes destination: Int, arguments: Bundle? = null, componentName: Class<out Activity?>? = null) {
    notificationIntent = NavDeepLinkBuilder(context).apply {
      setGraph(graph)
      setDestination(destination)
      setArguments(arguments)
      componentName?.let { setComponentName(it) }
    }.createPendingIntent()
  }

  @RequiresApi(Build.VERSION_CODES.N)
  fun setNotificationImportance(importance: Int) {
    notificationImportance = importance
  }

  fun setNotificationAutoCancel(autoCancel: Boolean) {
    notificationAutoCancel = autoCancel
  }

  fun build(): NotificationBuilder {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      notificationManager.createNotificationChannel(
        NotificationChannel(
          context.getString(R.string.notification_default_id),
          context.getString(R.string.notification_default_name),
          notificationImportance
        )
      )
    }
    val notification = NotificationCompat.Builder(context, context.getString(R.string.notification_default_id))
      .apply {
        setContentTitle(notificationTitle)
        setContentText(notificationText)
        setSmallIcon(notificationSmallIcon)
        notificationIntent?.let { setContentIntent(it) }
        setAutoCancel(notificationAutoCancel)
      }
    this.notification = notification.build()
    return this
  }

  fun show() {
    notificationManager.notify(notificationId, notification)
  }

}