package com.example.chatme.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatme.R
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

    fun CircleImageView.downloadUrl(url: String,placeholder: ProgressBar){
        val progressDrawable = createProgressDrawable(context)
        placeholder.progressDrawable = progressDrawable
        Glide.with(context).load(url).placeholder(progressDrawable).into(this)
    }

    fun ImageView.downloadUrl(url: String, placeholder: ProgressBar){
        val progressDrawable = createProgressDrawable(context)
        placeholder.progressDrawable = progressDrawable
        Glide.with(context).load(url).placeholder(progressDrawable).into(this)
    }

    fun createProgressDrawable(context: Context): Drawable {
        return CircularProgressDrawable(context).apply {
            strokeWidth = 8f
            centerRadius = 40f
            start()
        }
    }
    fun placeHolder(context: Context): ProgressBar {
        return ProgressBar(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            // İlerleme çizimiyle ilgili isteğe bağlı özelleştirmeleri yapabilirsiniz
            // Örneğin, stil, renk, vb. ayarları burada yapabilirsiniz
        }
    }

}