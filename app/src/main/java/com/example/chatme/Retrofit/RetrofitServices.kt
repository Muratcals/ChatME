package com.example.chatme.Retrofit

import android.net.Uri
import com.example.chatme.util.utils.retrofitServices
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart

class RetrofitServices {

    fun uploadData(uri:MultipartBody.Part): Call<Void> {
        return retrofitServices().uploadData(uri)
    }
}