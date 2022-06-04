package com.uberando.hipasar.service

import android.os.Bundle
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.uberando.hipasar.R
import com.uberando.hipasar.data.repository.MessagingRepository
import com.uberando.hipasar.notification.NotificationBuilder
import com.uberando.hipasar.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

  @Inject lateinit var messagingRepository: MessagingRepository

  private val serviceScope = CoroutineScope(Dispatchers.Default)

  override fun onMessageReceived(p0: RemoteMessage) {
    super.onMessageReceived(p0)
    Timber.d("message from: ${p0.from}")
    Timber.d("message data: ${p0.data}")
    Timber.d("message notification body: ${p0.notification?.title}")
    NotificationBuilder(applicationContext).apply {
      setNotificationTitle(p0.notification?.title ?: applicationContext.getString(R.string.notification_default_title))
      setNotificationText(p0.notification?.body ?: applicationContext.getString(R.string.notification_default_body))
      setNotificationDeepLink(
        graph = R.navigation.main_graph,
        destination = R.id.userOrderDetailFragment,
        arguments = generateOrderArguments(p0.data),
        componentName = MainActivity::class.java
      )
    }
      .build()
      .show()
  }

  override fun onNewToken(p0: String) {
    super.onNewToken(p0)
    serviceScope.launch {
      messagingRepository.updateMessagingToken(p0)
    }
  }

  private fun generateOrderArguments(data: Map<String, String>): Bundle {
    return Bundle().apply {
      putInt("order_id", data["id"]?.toInt() ?: 0)
    }
  }

}