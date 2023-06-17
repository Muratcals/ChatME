package com.example.chatme.viewmodel

import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatme.model.CommentModel
import com.example.chatme.model.NatificationModel.CommentsModel
import com.example.chatme.model.PostModel
import com.example.chatme.model.UserInformationModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObjects
import dagger.hilt.android.scopes.ActivityScoped
import org.w3c.dom.Comment
import java.util.UUID
import javax.inject.Inject

@ActivityScoped
class PostCommentViewModel @Inject constructor(
    val database:FirebaseFirestore,
    val getAuth:FirebaseUser,
) : ViewModel() {

    val post=MutableLiveData<PostModel>()
    val progress=MutableLiveData<Boolean>()
    val comments=MutableLiveData<List<CommentsModel>?>()
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
        database.collection("Posts").document(documentId).collection("comments").orderBy("time",
            Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            val datas =value?.toObjects(CommentsModel::class.java)
            comments.value=datas
        }
    }

    fun postComments(view: View, documentId:String,commentText:String,mail:String){
        val uuid=UUID.randomUUID().toString()
        database.collection("User Information").document(getAuth.email.toString()).get().addOnSuccessListener {userList->
            val userInformation=userList.toObject(UserInformationModel::class.java)
            val commentsData=CommentsModel(documentId,uuid,commentText,userInformation!!.authName,userInformation.profilImage,"comment",userInformation.mail)
            database.collection("Posts").document(documentId).collection("comments").document(uuid).set(commentsData).addOnSuccessListener {
                database.collection("User Information").document(mail).get().addOnSuccessListener {sharedUser->
                    if (sharedUser.exists()){
                        val sharedUserInformation =sharedUser.toObject(UserInformationModel::class.java)
                        if (!sharedUserInformation!!.mail.equals(getAuth.email.toString())){
                            database.collection("User Information").document(sharedUserInformation.mail).collection("notification").document(uuid).set(commentsData).addOnSuccessListener {
                                Toast.makeText(view.context, "Yorum kaydedildi", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(view.context, "Yorum kaydedildi", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

            }
        }

    }

}