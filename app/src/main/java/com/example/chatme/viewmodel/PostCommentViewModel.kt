package com.example.chatme.viewmodel

import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatme.model.CommentModel
import com.example.chatme.model.PostModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.scopes.ActivityScoped
import java.util.UUID
import javax.inject.Inject

@ActivityScoped
class PostCommentViewModel @Inject constructor(
    val database:FirebaseFirestore,
    val getAuth:FirebaseUser,
) : ViewModel() {

    val post=MutableLiveData<PostModel>()
    val progress=MutableLiveData<Boolean>()
    val comments=MutableLiveData<List<CommentModel>?>()
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
        database.collection("Posts").document(documentId).collection("comments").orderBy("commentTime",
            Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            val datas =value?.toObjects(CommentModel::class.java)
            comments.value=datas
        }
    }

    fun postComments(view: View, documentId:String, user:CommentModel){
        database.collection("Posts").document(documentId).collection("comments").document(UUID.randomUUID().toString()).set(user).addOnSuccessListener {
            Toast.makeText(view.context, "Yorum kaydedildi", Toast.LENGTH_SHORT).show()
        }
    }
}