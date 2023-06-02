package com.example.chatme.util

import android.content.Context
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView

object utils {
    fun CircleImageView.downloadUrl(url: String,placeholder: CircularProgressDrawable){
        Glide.with(context).load(url).placeholder(placeholder).into(this)
    }

    fun placeHolder(context: Context):CircularProgressDrawable{
        return CircularProgressDrawable(context).apply {
            strokeWidth=8F
            centerRadius=40F
            start()
        }
    }

}