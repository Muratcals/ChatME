package com.example.chatme.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.bumptech.glide.manager.ConnectivityMonitor.ConnectivityListener

class BroadcastRecevierServices:BroadcastReceiver() {

    companion object{
        var connectivityListener:ConnectivityReceiverListener?=null
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (connectivityListener!=null){
            connectivityListener!!.onNetworkConnectionChanged(isConnectedController(context!!))
        }
    }

    fun isConnectedController(context: Context): Boolean {
        val connectivityManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo =connectivityManager.activeNetworkInfo
        return networkInfo!=null && networkInfo.isConnectedOrConnecting
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

}