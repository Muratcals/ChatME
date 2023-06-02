package com.example.chatme.viewmodel

import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatme.R
import com.example.chatme.databinding.FragmentProffilBinding
import com.example.chatme.databinding.FragmentSearchBinding
import com.example.chatme.model.UserInformationModel
import com.example.chatme.model.followedModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
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

    fun authFollowController(binding: FragmentProffilBinding, mail: String) {
        progress.value = true
        database.collection("User Information").document(getAuth.email.toString()).get()
            .addOnSuccessListener { user ->
                user.reference.collection("follow").whereEqualTo("mail", mail).get()
                    .addOnSuccessListener { follow ->
                        if (!follow.isEmpty) {
                            binding.follow.setBackgroundResource(R.drawable.button_background_shape)
                            binding.follow.setText("Takip")
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
    fun authFollow(user: followedModel) {
        database.collection("User Information").document(getAuth.email.toString())
            .collection("follow").document(user.authName).set(user).addOnSuccessListener {
                println("okey")
            }
    }

    fun authDeleteFollow(user: followedModel) {
        database.collection("User Information").document(getAuth.email.toString())
            .collection("follow").document(user.authName).delete().addOnSuccessListener {
                println("okey")
            }
    }

    fun userInformation(mail: String) {
        database.collection("User Information").document(mail).get()
            .addOnSuccessListener {user->
                val authItems = user.toObject(UserInformationModel::class.java)
                authInformation.postValue(authItems!!)
                getFollowAndFollowed(user.reference)
            }
    }

    fun getFollowAndFollowed(reference: DocumentReference){
        reference.collection("follow").get().addOnSuccessListener { followResult->
            val items =followResult.toObjects(followedModel::class.java)
            println(items[0].name)
            follow.value=items
            reference.collection("followed").get().addOnSuccessListener { followedResult->
                followed.value=followedResult.toObjects(followedModel::class.java)
                progress.value = false
            }
        }

    }
}