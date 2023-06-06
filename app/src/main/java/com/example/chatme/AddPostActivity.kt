package com.example.chatme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatme.R
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel

@AndroidEntryPoint
class AddPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        supportActionBar!!.hide()
    }
}