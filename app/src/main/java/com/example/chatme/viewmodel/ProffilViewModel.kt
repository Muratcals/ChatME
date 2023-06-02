package com.example.chatme.viewmodel

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatme.R
import com.example.chatme.databinding.FragmentProffilBinding
import com.example.chatme.databinding.FragmentSearchBinding
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
    val authInformation = MutableLiveData<UserInformationModel>()
    val currentUserInformation=MutableLiveData<UserInformationModel>()

    fun authFollowController(binding: FragmentProffilBinding, mail: String,authName:String) {
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
    fun authFollow(context: Context,users: UserInformationModel,currentUsers: UserInformationModel) {
        val currentUser = followedModel(currentUsers.customerId,currentUsers.authName,currentUsers.authName,currentUsers.profilImage,Timestamp.now(),bool = false)
        database.collection("User Information").document(users.mail)
            .collection("request").document(currentUsers.authName).set(currentUser).addOnSuccessListener {
                val followAuth=followedModel(users.customerId,users.name,users.authName,users.profilImage,Timestamp.now())
                database.collection("User Information").document(getAuth.email.toString())
                    .collection("sendRequest").document(users.authName).set(followAuth).addOnSuccessListener {
                        Toast.makeText(context, "İstek gönderildi.", Toast.LENGTH_SHORT).show()
                    }
            }
    }

    fun authDeleteFollow(user: followedModel,mail: String) {
        database.collection("User Information").document(getAuth.email.toString())
            .collection("followed").document(user.authName).delete().addOnSuccessListener {
                database.collection("User Information").document(mail)
                    .collection("follow").document(user.authName).delete().addOnSuccessListener {

                    }
            }
    }

    fun requestDeleteFollow(users: UserInformationModel,currentUsers: UserInformationModel){
        database.collection("User Information").document(users.mail)
            .collection("request").document(currentUsers.authName).delete().addOnSuccessListener {
                database.collection("User Information").document(getAuth.email.toString())
                    .collection("sendRequest").document(users.authName).delete().addOnSuccessListener {

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