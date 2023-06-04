package com.example.chatme.util

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class firebaseMessagingServices : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Gelen bildirimi işleme kodlarını buraya yazın
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    fun sendMessage(){
        val message =RemoteMessage.Builder("deneme")
            .setMessageId("unique_message_id")
            .addData("asda","esdg")
            .build()
    }
}
