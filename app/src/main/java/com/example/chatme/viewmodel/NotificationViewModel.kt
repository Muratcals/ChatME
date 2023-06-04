package com.example.chatme.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatme.model.followNotificationModel
import com.example.chatme.model.followedModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class NotificationViewModel @Inject constructor(
    val database :FirebaseFirestore,
    val getAuth:FirebaseUser
) : ViewModel() {

    val followRequest =MutableLiveData<List<followedModel>?>()
    val notification=MutableLiveData<List<followNotificationModel>>()
    fun getFollowRequestData(){
        database.collection("User Information").document(getAuth.email.toString()).
        collection("request").whereEqualTo("bool",false).addSnapshotListener { value, error ->
            val followedModel =value?.toObjects(followedModel::class.java)
            followRequest.value=followedModel
        }
    }

    fun getNotification(){
        database.collection("User Information").document(getAuth.email.toString()).collection("notification").addSnapshotListener { value, error ->
            if (value!=null){
                notification.value=value.toObjects(followNotificationModel::class.java)
            }
        }
    }
}