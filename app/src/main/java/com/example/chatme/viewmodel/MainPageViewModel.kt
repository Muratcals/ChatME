package com.example.chatme.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatme.model.PostModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityScoped
class MainPageViewModel @Inject constructor(
    val database: FirebaseFirestore,
    val getAuth: FirebaseUser,
    val storage: FirebaseStorage
) : ViewModel() {

    val postList = MutableLiveData<List<PostModel>>()
    val progress = MutableLiveData<Boolean>()
    fun getPostList() {
        progress.value = true
        database.collection("Posts").get().addOnSuccessListener { value ->
            if (value != null) {
                val postListResult = value.toObjects(PostModel::class.java)
                postList.value=postListResult
                progress.value=false
            }
        }
    }
}