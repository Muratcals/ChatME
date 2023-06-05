package com.example.chatme.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatme.model.followNotificationModel
import com.example.chatme.model.followedModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class NotificationViewModel @Inject constructor(
    val database :FirebaseFirestore,
    val getAuth:FirebaseUser
) : ViewModel() {

    val followRequest =MutableLiveData<List<followNotificationModel>?>()
    val notification=MutableLiveData<List<followNotificationModel>?>()
    val progress=MutableLiveData<Boolean>()
    private val  currentUser=database.collection("User Information").document(getAuth.email.toString())
    fun getFollowRequestData(){
        progress.value=true
        currentUser.collection("notification").whereEqualTo("categoryName","request").get().addOnSuccessListener { value ->
            if (value!=null){
                val followedModel =value.toObjects(followNotificationModel::class.java)
                followRequest.value=followedModel
                progress.value=false
            }
        }
    }

    fun getAllNotification(){
        currentUser.collection("notification").addSnapshotListener { value, error ->
            val followedModel =value?.toObjects(followNotificationModel::class.java)
            notification.value=followedModel
        }
    }

    fun getNotification(){
        currentUser.collection("notification").addSnapshotListener { value, error ->
            if (value!=null){
                notification.value=value.toObjects(followNotificationModel::class.java)
            }
        }
    }
}