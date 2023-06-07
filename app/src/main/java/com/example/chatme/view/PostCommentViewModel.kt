package com.example.chatme.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatme.model.CommentModel
import com.example.chatme.model.PostModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
class PostCommentViewModel(
    val database:FirebaseFirestore,
    val getAuth:FirebaseUser,
) : ViewModel() {

    val post=MutableLiveData<PostModel>()
    val progress=MutableLiveData<Boolean>()
    val comments=MutableLiveData<List<CommentModel?>>()
    fun getPost(documentId:String){
        progress.value=true
        database.collection("Posts").document(documentId).get().addOnSuccessListener {
            if (it.exists()){
                post.value=it.toObject(PostModel::class.java)
                progress.value=false
            }
        }
    }

    fun getComments(documentId: String){
        database.collection("Posts").document(documentId).collection("comments").get().addOnSuccessListener {
            val datas =it.toObjects(CommentModel::class.java)
            comments.value=datas
        }
    }
}