package com.example.chatme.viewmodel

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatme.R
import com.example.chatme.databinding.FragmentProffilBinding
import com.example.chatme.databinding.FragmentSearchBinding
import com.example.chatme.databinding.FragmentSearchProfilBinding
import com.example.chatme.model.PostModel
import com.example.chatme.model.UserInformationModel
import com.example.chatme.model.followedModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class ProffilViewModel @Inject constructor(
    val database: FirebaseFirestore,
    val getAuth: FirebaseUser
) : ViewModel() {
    val follow =MutableLiveData<List<followedModel>>()
    val followed =MutableLiveData<List<followedModel>>()
    val progress = MutableLiveData<Boolean>()
    val postList =MutableLiveData<List<PostModel>>()
    val savePostList=MutableLiveData<List<PostModel>>()
    val authInformation = MutableLiveData<UserInformationModel>()

    fun authProfilInformation(){
        database.collection("User Information")
            .document(getAuth.email.toString()).get()
            .addOnSuccessListener { user->
                if (user!=null){
                    val result = user.toObject(UserInformationModel::class.java)
                    authInformation.value=result!!
                    getFollowAndFollowed(user.reference)
                }
            }
    }

    fun getPost(){
        database.collection("User Information").document(getAuth.email.toString()).get().addOnSuccessListener {authResult->
            if (authResult.exists()){
                database.collection("Posts").whereEqualTo("userWhoShared",authResult.get("authName")).get().addOnSuccessListener { postResult->
                    val result =postResult.toObjects(PostModel::class.java)
                    postList.value=result
                }
            }
        }
    }

    fun savePost(){
        database.collection("User Information").document(getAuth.email.toString()).collection("savePost").get().addOnSuccessListener {
            val savePostListResult=it.toObjects(PostModel::class.java)
            savePostList.value=savePostListResult
        }
    }

    fun getFollowAndFollowed(reference: DocumentReference){
        reference.collection("follow").get().addOnSuccessListener { followResult->
            val items =followResult.toObjects(followedModel::class.java)
            follow.value=items
            reference.collection("followed").get().addOnSuccessListener { followedResult->
                followed.value=followedResult.toObjects(followedModel::class.java)
                progress.value = false
            }
        }
    }
}