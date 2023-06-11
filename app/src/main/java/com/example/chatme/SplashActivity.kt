package com.example.chatme

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.chatme.databinding.MainDialogViewBinding
import com.example.chatme.util.utils
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
        if (ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),100)
        }
        if ( ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.VIBRATE)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.VIBRATE),101)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==100){
            if (ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),100)
            }
        }else if (requestCode==101){
            if ( ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.VIBRATE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.VIBRATE),101)
            }
        }
    }
}