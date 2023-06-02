package com.example.chatme.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    val followRequest =MutableLiveData<List<followedModel>>()
    fun getFollowRequestData(){
        database.collection("User Information").document(getAuth.email.toString()).collection("request").get().addOnSuccessListener {
            if (it.isEmpty){

            }else{
                val followedModel =it.toObjects(followedModel::class.java)
                followRequest.value=followedModel
            }
        }
    }
}