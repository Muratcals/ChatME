package com.example.chatme.viewmodel

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatme.databinding.FragmentSearchProfilBinding
import com.example.chatme.model.UserInformationModel
import com.example.chatme.model.followNotificationModel
import com.example.chatme.model.followedModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class FollowAndFollowedViewModel @Inject constructor(
    val database:FirebaseFirestore,
    val getAuth: FirebaseUser
) : ViewModel() {

    val followItem=MutableLiveData<List<followedModel>>()
    val progress=MutableLiveData<Boolean>()
    val followedItem=MutableLiveData<List<followedModel>>()

    fun getUserFollow(authName:String){
        progress.value=true
        database.collection("User Information").whereEqualTo("authName",authName).get().addOnSuccessListener {
            if (!it.isEmpty){
                database.collection("User Information").document(it.documents[0].id).collection("follow").get().addOnSuccessListener {user->
                    val result =user.toObjects(followedModel::class.java)
                    followItem.value=result
                    progress.value=false
                }
            }
        }
    }

    fun getUserFollowed(authName: String){
        progress.value=true
        database.collection("User Information").whereEqualTo("authName",authName).get().addOnSuccessListener {
            if (!it.isEmpty){
                database.collection("User Information").document(it.documents[0].id).collection("followed").get().addOnSuccessListener {user->
                    val result =user.toObjects(followedModel::class.java)
                    followedItem.value=result
                    progress.value=false
                }
            }
        }
    }

    fun authFollow(context: Context, users: followedModel) {
        val userReference = database.collection("User Information").document(users.mail)
        val currentUserReference=database.collection("User Information").document(getAuth.email.toString())
        currentUserReference.get().addOnSuccessListener {
            if (it.exists()){
                val currentUser=it.toObject(UserInformationModel::class.java)
                val followAuth= followedModel(currentUser!!.mail,currentUser.name,currentUser.authName,currentUser.profilImage, Timestamp.now())
                userReference.collection("request").document(currentUser.authName).set(followAuth).addOnSuccessListener {
                    val notificationModel =followNotificationModel("request",currentUser.mail,currentUser.profilImage,currentUser.authName,currentUser.name,state = false)
                    userReference.collection("notification").document("${currentUser.authName} request").set(notificationModel).addOnSuccessListener {
                        currentUserReference.collection("sendRequest").document(users.authName).set(users).addOnSuccessListener {
                            Toast.makeText(context, "İstek gönderildi.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }
    }

    fun requestDeleteFollow(users: followedModel){
        val userReference = database.collection("User Information").document(users.mail)
        val currentUserReference=database.collection("User Information").document(getAuth.email.toString())
        currentUserReference.get().addOnSuccessListener {
            if (it.exists()){
                val currentUser=it.toObject(followedModel::class.java)
                userReference.collection("request").document(currentUser!!.authName).delete().addOnSuccessListener {
                    currentUserReference.collection("sendRequest").document(users.authName).delete().addOnSuccessListener {
                        userReference.collection("notification").document("${currentUser.authName} request").delete().addOnSuccessListener {

                        }
                    }
                }
            }
        }
    }

    fun authDeleteFollow(binding: FragmentSearchProfilBinding,user: followedModel, mail: String,authName: String) {
        binding.follow.visibility=View.INVISIBLE
        binding.searchSuccessProgress.visibility=View.VISIBLE
        database.collection("User Information").document(getAuth.email.toString())
            .collection("followed").document(user.authName).delete().addOnSuccessListener {
                database.collection("User Information").document(mail)
                    .collection("follow").document(user.authName).delete().addOnSuccessListener {
                        database.collection("User Information").document(mail).collection("notification")
                            .document("${authName} follow").delete().addOnSuccessListener {
                                binding.follow.visibility=View.VISIBLE
                                binding.searchSuccessProgress.visibility=View.INVISIBLE
                            }
                    }
            }
    }
}