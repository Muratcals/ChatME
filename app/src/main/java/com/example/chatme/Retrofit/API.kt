package com.example.chatme.Retrofit

import android.net.Uri
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface API {

    @Multipart
    @POST("upload_photo.php")
    fun uploadData(@Part  uri:MultipartBody.Part): Call<Void>
}