package com.example.chatme.view

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import com.example.chatme.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class MainLoginViewModel @Inject constructor(
    val database:FirebaseFirestore,
    val auth:FirebaseAuth
) : ViewModel() {

    fun loginUser(activity: Activity, userName:String, password:String){
        database.collection("User Information").whereEqualTo("userName",userName).get().addOnSuccessListener { 
            if (it.isEmpty){
                Toast.makeText(activity.applicationContext, "Böyle bir kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
            }else{
                val email =it.documents.get(0).get("E-Mail") as String
                auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                    Toast.makeText(activity.applicationContext, "Giriş başarılı.", Toast.LENGTH_SHORT).show()
                    val intent =Intent(activity.applicationContext,MainActivity::class.java)
                    startActivity(activity.applicationContext,intent,null)
                    activity.finish()
                }.addOnFailureListener {
                    Toast.makeText(activity.applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(activity.applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }
}