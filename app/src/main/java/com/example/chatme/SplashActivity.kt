package com.example.chatme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatme.databinding.MainDialogViewBinding
import com.example.chatme.viewmodel.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    @Inject lateinit var auth :FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (auth.currentUser!=null){
            val intent =Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }else{
            dialog()
        }
    }

    fun dialog(){
        val bottomSheetDialog=BottomSheetDialog(this)
        val view =MainDialogViewBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(view.root)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()
        val intent =Intent(applicationContext,LoginActivity::class.java)
        view.login.setOnClickListener {
            intent.putExtra("incoming","login")
            startActivity(intent)
        }
        view.createUser.setOnClickListener {
            intent.putExtra("incoming","register")
            startActivity(intent)
        }
    }
}