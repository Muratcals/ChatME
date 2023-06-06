package com.example.chatme.viewmodel

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class MainLoginViewModel @Inject constructor(
    val database:FirebaseFirestore,
    val auth:FirebaseAuth
) : ViewModel() {

    val progress =MutableLiveData<Boolean>()
    fun loginUser(activity: Activity, userName:String, password:String){
        progress.value=true
        println(userName)
        database.collection("User Information").whereEqualTo("authName",userName).get().addOnSuccessListener {
            if (it.isEmpty){
                progress.value=false
                Toast.makeText(activity.applicationContext, "Böyle bir kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
            }else{
                val email =it.documents.get(0).get("mail") as String
                auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                    Toast.makeText(activity.applicationContext, "Giriş başarılı.", Toast.LENGTH_SHORT).show()
                    val intent =Intent(activity.applicationContext, MainActivity::class.java)
                    startActivity(activity,intent,null)
                    progress.value=false
                    activity.finish()
                }.addOnFailureListener {
                    Toast.makeText(activity.applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    progress.value=false
                }
            }
        }.addOnFailureListener {
            Toast.makeText(activity.applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
            progress.value=false
        }
    }
}