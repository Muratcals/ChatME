package com.example.chatme.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatme.R
import com.example.chatme.databinding.FragmentSearchProfilBinding
import com.example.chatme.model.UserInformationModel
import com.example.chatme.model.followNotificationModel
import com.example.chatme.model.followedModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class SearchProfilViewModel @Inject constructor(
    val database: FirebaseFirestore,
    val getAuth: FirebaseUser
) : ViewModel() {
    val follow = MutableLiveData<List<followedModel>>()
    val followed = MutableLiveData<List<followedModel>>()
    val progress = MutableLiveData<Boolean>()
    val authInformation = MutableLiveData<UserInformationModel>()
    val currentUserInformation= MutableLiveData<UserInformationModel>()

    fun authFollowController(binding: FragmentSearchProfilBinding, mail: String, authName:String) {
        progress.value = true
        database.collection("User Information").document(getAuth.email.toString()).get()
            .addOnSuccessListener { user ->
                currentUserInformation.value=user.toObject(UserInformationModel::class.java)
                user.reference.collection("followed").whereEqualTo("authName", authName).get()
                    .addOnSuccessListener { follow ->
                        if (!follow.isEmpty) {
                            binding.follow.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
                            binding.follow.setText("Takip")
                        }
                        user.reference.collection("sendRequest").whereEqualTo("authName",authName).get().addOnSuccessListener { request->
                            if (!request.isEmpty) {
                                binding.follow.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
                                binding.follow.setText("Bekleniyor")
                            }
                        }
                        userInformation(mail)
                    }
            }.addOnFailureListener {
                print(it.localizedMessage)
            }
    }

    fun authFollow(context: Context, users: UserInformationModel, currentUsers: UserInformationModel) {
        val currentUser = followedModel(currentUsers.mail,currentUsers.authName,currentUsers.authName,currentUsers.profilImage,
            Timestamp.now(),bool = false)
        val userReference = database.collection("User Information").document(users.mail)
        val currentUserReference=database.collection("User Information").document(getAuth.email.toString())
        userReference.collection("request").document(currentUsers.authName).set(currentUser).addOnSuccessListener {
                val notificationModel =followNotificationModel("request",currentUser.mail,currentUser.imageUrl,currentUser.authName)
                userReference.collection("notification").document("${currentUser.authName} request").set(notificationModel).addOnSuccessListener {
                    val followAuth= followedModel(users.mail,users.name,users.authName,users.profilImage, Timestamp.now())
                    currentUserReference.collection("sendRequest").document(users.authName).set(followAuth).addOnSuccessListener {
                        Toast.makeText(context, "İstek gönderildi.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    fun authDeleteFollow(user: followedModel, mail: String) {
        database.collection("User Information").document(getAuth.email.toString())
            .collection("followed").document(user.authName).delete().addOnSuccessListener {
                database.collection("User Information").document(mail)
                    .collection("follow").document(user.authName).delete().addOnSuccessListener {

                    }
            }
    }

    fun requestDeleteFollow(users: UserInformationModel, currentUsers: UserInformationModel){
        val userReference = database.collection("User Information").document(users.mail)
        val currentUserReference=database.collection("User Information").document(getAuth.email.toString())
        userReference.collection("request").document(currentUsers.authName).delete().addOnSuccessListener {
                currentUserReference.collection("sendRequest").document(users.authName).delete().addOnSuccessListener {
                        val notificationModel =followNotificationModel("request",users.mail,users.profilImage,users.authName)
                        userReference.collection("notification").add(notificationModel).addOnSuccessListener {

                        }
                    }
            }
    }

    fun userInformation(mail: String) {
        database.collection("User Information").document(mail).get()
            .addOnSuccessListener {user->
                val authItems = user.toObject(UserInformationModel::class.java)
                authInformation.value=authItems!!
                getFollowAndFollowed(user.reference)
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