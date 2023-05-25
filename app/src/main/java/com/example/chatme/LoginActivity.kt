package com.example.chatme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatme.databinding.ActivityLoginBinding
import com.example.chatme.view.MainLoginFragment
import com.example.chatme.view.RegisterFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        val incoming =intent.getStringExtra("incoming")
        if (incoming.equals("register")){
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView2,RegisterFragment()).commit()
        }else {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView2,MainLoginFragment()).commit()
        }

    }
}