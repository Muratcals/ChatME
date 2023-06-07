package com.example.chatme.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatme.model.PostModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityScoped
class MainPageViewModel @Inject constructor(
    val database:FirebaseFirestore,
    val getAuth:FirebaseUser
) : ViewModel() {

    val postList=MutableLiveData<List<PostModel>>()
    val progress=MutableLiveData<Boolean>()
    fun getPostList(){
        viewModelScope.launch(Dispatchers.IO) {
            database.collection("Posts").addSnapshotListener { value, _ ->
                if (value!=null){
                    val postListResult=value.toObjects(PostModel::class.java)
                    val followedInPost=ArrayList<PostModel>()
                    postList.postValue(postListResult)
                    progress.postValue(false)
                }
            }
        }
    }

}