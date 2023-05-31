package com.example.chatme.viewmodel

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.chatme.model.UserInformationModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class ProfilEditDetailsViewModel @Inject constructor(
    val database:FirebaseFirestore,
    val auth:FirebaseAuth
) : ViewModel() {

    fun nameUpdate(activity:Activity,id:String,name:String){
        database.collection("User Information").document(id).update("name",name).addOnSuccessListener {
            Toast.makeText(activity.applicationContext,"güncellendi", Toast.LENGTH_SHORT).show()
            activity.onBackPressed()
        }
    }
    fun genderUpdate(activity:Activity,id:String,gender:String){
        database.collection("User Information").document(id).update("gender",gender).addOnSuccessListener {
            Toast.makeText(activity.applicationContext,"güncellendi", Toast.LENGTH_SHORT).show()
            activity.onBackPressed()
        }
    }
    fun authNameUpdate(activity:Activity,id:String,authName:String){
        database.collection("User Information").document(id).update("authName",authName).addOnSuccessListener {
            Toast.makeText(activity.applicationContext,"güncellendi", Toast.LENGTH_SHORT).show()
            activity.onBackPressed()
        }
    }
    fun biographyUpdate(activity:Activity,id:String,biography:String){
        database.collection("User Information").document(id).update("biography",biography).addOnSuccessListener {
            Toast.makeText(activity.applicationContext,"güncellendi", Toast.LENGTH_SHORT).show()
            activity.onBackPressed()
        }
    }
}