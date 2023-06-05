package com.example.chatme.repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.chatme.model.UserInformationModel
import com.example.chatme.model.followNotificationModel
import com.example.chatme.model.followedModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject


@ActivityScoped
class repo @Inject constructor(
    val database:FirebaseFirestore,
    val auth:FirebaseAuth,
    val getAuth:FirebaseUser
) {
    val authList=MutableLiveData<List<UserInformationModel>>()

    fun getAllUser(){
        MainScope().launch {
            val querySnapshot=database.collection("User Information").get().await()
            val items =querySnapshot.toObjects(UserInformationModel::class.java)
            authList.value=items
        }
    }
    /*
    fun getSearchAuth (char:String){
        MainScope().launch {
            val querySnapshot=database.collection("User Information").whereArrayContains("authName",char).get().await()
            val items =querySnapshot.toObjects(UserInformationModel::class.java)
            val list = ArrayList<followedModel>()
            for (item in items){
                val user=followedModel(UUID.randomUUID().toString(),item.customerId,item.name,item.authName,item.profilImage)
                list.add(user)
                authList.value=list
            }
        }
    }

     */
}