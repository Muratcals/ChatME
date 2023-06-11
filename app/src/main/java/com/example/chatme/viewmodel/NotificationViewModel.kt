package com.example.chatme.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatme.model.NatificationModel.CommentsModel
import com.example.chatme.model.NatificationModel.FollowModel
import com.example.chatme.model.NatificationModel.LikeModel
import com.example.chatme.model.NatificationModel.RequestModel
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

    val followRequest =MutableLiveData<List<RequestModel>?>()
    val notification=MutableLiveData<List<Any>?>()
    val progress=MutableLiveData<Boolean>()
    private val  currentUser=database.collection("User Information").document(getAuth.email.toString())
    fun getFollowRequestData(){
        progress.value=true
        currentUser.collection("notification").whereEqualTo("categoryName","request").get().addOnSuccessListener { value ->
            if (value!=null){
                val requestModel=value.toObjects(RequestModel::class.java)
                followRequest.value=requestModel
                progress.value=false
            }
        }
    }

    fun getAllNotification(){
        progress.value=true
        currentUser.collection("notification").orderBy("time",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            val followArray = mutableListOf<Any>()
            if (value!=null){
                for (document in value!!.documents){
                    val categoryName =document.get("categoryName")
                    if (categoryName!!.equals("request")) {
                        val requestModel = document.toObject(RequestModel::class.java)
                        followArray.add(requestModel!!)
                    } else if (categoryName.equals("like")) {
                        val likeModel = document.toObject(LikeModel::class.java)
                        followArray.add(likeModel!!)
                    }else if (categoryName.equals("comment")) {
                        val commentsModel = document.toObject(CommentsModel::class.java)
                        followArray.add(commentsModel!!)
                    } else if (categoryName.equals("follow")) {
                        val followModel = document.toObject(FollowModel::class.java)
                        followArray.add(followModel!!)
                    }
                 }
            }
            notification.value=followArray
            progress.value=false
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