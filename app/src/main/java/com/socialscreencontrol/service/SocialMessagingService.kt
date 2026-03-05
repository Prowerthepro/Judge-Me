package com.socialscreencontrol.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.socialscreencontrol.core.util.AppNotifier

class SocialMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title
            ?: message.data["title"]
            ?: "Social Screen Control"
        val body = message.notification?.body
            ?: message.data["body"]
            ?: "New accountability update"

        AppNotifier.notify(
            context = this,
            title = title,
            body = body
        )
    }
}
