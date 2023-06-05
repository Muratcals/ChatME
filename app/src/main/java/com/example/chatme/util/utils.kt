package com.example.chatme.util

import android.content.Context
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatme.Retrofit.API
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.plugins.RxJavaPlugins
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object utils {

    const val BASE_URL="http://omerahiskali.com.tr/"

    fun retrofitServices() = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(API::class.java)

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