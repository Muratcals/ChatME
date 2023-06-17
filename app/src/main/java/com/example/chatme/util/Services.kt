package com.example.chatme.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.chatme.R

class Services():Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        // Hizmet sonlandığında yapılması gereken işlemleri burada tanımlayabilirsiniz.
        super.onDestroy()
    }



    fun createNotificationChannel(context: Context){
        val name ="Application Notification"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("my_channel",name,importance)
        val notificationManager: NotificationManager =context.getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    fun deleteNot(context: Context){
        val notificationManager=context.getSystemService(NotificationManager::class.java) as NotificationManager
        println(notificationManager.notificationChannels[0].id)
    }

    fun sendNotification(context: Context,baslik:String, icerik:String){
        createNotificationChannel(context)
        val builder = NotificationCompat.Builder(this,"my_channel")
            .setSmallIcon(R.drawable._logo)
            .setContentTitle(baslik)
            .setContentText(icerik)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.VIBRATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(102, builder.build())
        }
    }
}