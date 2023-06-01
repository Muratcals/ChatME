package com.example.chatme.util

import android.net.Uri
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

object utils {

    fun CircleImageView.downloadUrl(url: String){
        Glide.with(context).load(url).into(this)
    }
}