package com.example.chatme.viewmodel

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatme.R
import com.example.chatme.adapter.FollowRquestRecyclerAdapter
import com.example.chatme.databinding.FragmentSearchProfilBinding
import com.example.chatme.model.NatificationModel.FollowModel
import com.example.chatme.model.NatificationModel.RequestModel
import com.example.chatme.model.PostModel
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
    val currentUserInformation = MutableLiveData<UserInformationModel>()
    val userPost =MutableLiveData<List<PostModel>>()

    fun authFollowController(binding: FragmentSearchProfilBinding, mail: String, authName: String) {
        progress.value = true
        database.collection("User Information").document(getAuth.email.toString()).addSnapshotListener { value, error ->
                if (value?.exists()==true) {
                    val currentData = value.toObject(UserInformationModel::class.java)
                    val currentReference = value.reference
                    val userReference = database.collection("User Information").document(mail)
                    currentUserInformation.value = currentData!!
                    currentReference.collection("followed").whereEqualTo("authName", authName).get().addOnSuccessListener { value->
                            if (value?.isEmpty != true) {
                                binding.follow.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
                                binding.follow.setText("Takip")
                            } else {
                                currentReference.collection("follow")
                                    .whereEqualTo("authName", authName).get()
                                    .addOnSuccessListener { request ->
                                        if (!request.isEmpty) {
                                            binding.follow.setBackgroundResource(R.drawable.button_background_shape)
                                            binding.follow.setText("Sende onu takip et")
                                        } else {
                                            currentReference.collection("sendRequest")
                                                .whereEqualTo("authName", authName).get()
                                                .addOnSuccessListener { requests ->
                                                    if (!requests.isEmpty) {
                                                        binding.follow.setBackgroundResource(R.drawable.button_backgorund_gray_shape)
                                                        binding.follow.setText("Bekleniyor")
                                                    } else {
                                                        userReference.collection("sendRequest")
                                                            .whereEqualTo(
                                                                "authName",
                                                                currentData.authName
                                                            ).get()
                                                            .addOnSuccessListener { request ->
                                                                if (!request.isEmpty) {
                                                                    binding.follow.setBackgroundResource(
                                                                        R.drawable.button_backgorund_gray_shape
                                                                    )
                                                                    binding.follow.setText("Onayla")
                                                                    binding.follow.setBackgroundResource(R.drawable.button_background_shape)
                                                                }
                                                            }
                                                    }
                                                }
                                        }
                                    }
                            }
                        }
                    userInformation(binding, mail, currentData.authName)
                }
            }
    }

    fun authFollow(
        context: Context,
        users: UserInformationModel,
        currentUsers: UserInformationModel
    ) {
        val currentUser = followedModel(
            currentUsers.mail,
            currentUsers.authName,
            currentUsers.authName,
            currentUsers.profilImage,
            Timestamp.now(),
            bool = false
        )
        val userReference = database.collection("User Information").document(users.mail)
        val currentUserReference =
            database.collection("User Information").document(getAuth.email.toString())
        userReference.collection("request").document(currentUsers.authName).set(currentUser)
            .addOnSuccessListener {
                val notificationModel = RequestModel(
                    currentUser.authName,
                    currentUser.name,
                    currentUser.imageUrl,
                    "request",
                    currentUser.mail
                )
                userReference.collection("notification")
                    .document("${currentUser.authName} request").set(notificationModel)
                    .addOnSuccessListener {
                        val followAuth = followedModel(
                            users.mail,
                            users.name,
                            users.authName,
                            users.profilImage,
                            Timestamp.now()
                        )
                        currentUserReference.collection("sendRequest").document(users.authName)
                            .set(followAuth).addOnSuccessListener {
                                Toast.makeText(context, "İstek gönderildi.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
            }
    }

    fun reFollow(binding: FragmentSearchProfilBinding, users: UserInformationModel) {
        binding.searchSuccessProgress.visibility = View.VISIBLE
        binding.follow.visibility = View.INVISIBLE
        val userReference = database.collection("User Information").document(users.mail)
        val currentUserReference = database.collection("User Information").document(getAuth.email.toString())
        userReference.collection("sendRequest").document(users.authName).delete()
            .addOnSuccessListener {
                currentUserReference.collection("request").document(users.authName)
                    .delete().addOnSuccessListener {
                        currentUserReference.collection("follow").document(users.authName)
                            .set(users).addOnSuccessListener {
                                currentUserReference.get().addOnSuccessListener {
                                    if (it.exists()) {
                                        val currentUser =
                                            it.toObject(UserInformationModel::class.java)
                                        val currentUserFollowed = followedModel(
                                            currentUser!!.mail,
                                            currentUser.name,
                                            currentUser.authName,
                                            currentUser.profilImage,
                                            Timestamp.now()
                                        )
                                        userReference.collection("followed")
                                            .document(currentUser.authName)
                                            .set(currentUserFollowed)
                                            .addOnSuccessListener {
                                                currentUserReference.collection("notification")
                                                    .document("${users.authName} request").get()
                                                    .addOnSuccessListener {requestState->
                                                        if (requestState.exists()) {
                                                            val result =
                                                                requestState.toObject(
                                                                    RequestModel::class.java
                                                                )
                                                            val item = FollowModel(
                                                                result!!.authName,
                                                                result.name,
                                                                result.authImage,
                                                                "follow",result.mail
                                                            )
                                                            currentUserReference.collection("notification")
                                                                .document("${users.authName} request")
                                                                .delete().addOnSuccessListener {
                                                                    currentUserReference.collection(
                                                                        "notification"
                                                                    )
                                                                        .document("${users.authName} follow")
                                                                        .set(item)
                                                                        .addOnSuccessListener {
                                                                            userReference.collection(
                                                                                "sendRequest"
                                                                            )
                                                                                .document(
                                                                                    currentUser.authName
                                                                                )
                                                                                .delete()
                                                                                .addOnSuccessListener {
                                                                                    binding.follow.visibility =
                                                                                        View.VISIBLE
                                                                                    binding.searchSuccessProgress.visibility =
                                                                                        View.INVISIBLE
                                                                                }
                                                                        }
                                                                }
                                                        }
                                                    }
                                            }
                                    }
                                }
                            }

                    }
            }
    }

    fun authDeleteFollow(
        binding: FragmentSearchProfilBinding,
        user: followedModel,
        currentUsers: UserInformationModel
    ) {
        binding.follow.visibility = View.INVISIBLE
        binding.searchSuccessProgress.visibility = View.VISIBLE
        val currentUserReference = database.collection("User Information").document(getAuth.email.toString())
        val userReference = database.collection("User Information").document(user.mail)
        currentUserReference.collection("followed").document(user.authName).delete().addOnSuccessListener {
               userReference.collection("follow").document(currentUsers.authName).delete()
                    .addOnSuccessListener {
                        userReference.collection("notification")
                            .document("${currentUsers.authName} follow").delete().addOnSuccessListener {
                                binding.follow.visibility = View.VISIBLE
                                binding.searchSuccessProgress.visibility = View.INVISIBLE
                            }
                    }
            }
    }

    fun requestDeleteFollow(users: UserInformationModel, currentUsers: UserInformationModel) {
        val userReference = database.collection("User Information").document(users.mail)
        val currentUserReference =
            database.collection("User Information").document(getAuth.email.toString())
        userReference.collection("request").document(currentUsers.authName).delete()
            .addOnSuccessListener {
                currentUserReference.collection("sendRequest").document(users.authName).delete()
                    .addOnSuccessListener {
                        userReference.collection("notification")
                            .document("${currentUsers.authName} request").delete()
                            .addOnSuccessListener {

                            }
                    }
            }
    }

    fun userInformation(binding: FragmentSearchProfilBinding, mail: String, authName: String) {
        database.collection("User Information").document(mail).get()
            .addOnSuccessListener { user ->
                user.reference.collection("sendRequest").document(authName).get()
                    .addOnSuccessListener {
                        if (it.exists()) {
                            binding.follow.setBackgroundResource(R.drawable.button_background_shape)
                            binding.follow.setText("Onayla")
                        }
                    }
                val authItems = user.toObject(UserInformationModel::class.java)
                authInformation.value = authItems!!
                getFollowAndFollowed(user.reference)
            }
    }

    fun getFollowAndFollowed(reference: DocumentReference) {
        reference.collection("follow").get().addOnSuccessListener { followResult ->
            val items = followResult.toObjects(followedModel::class.java)
            follow.value = items
            reference.collection("followed").get().addOnSuccessListener { followedResult ->
                followed.value = followedResult.toObjects(followedModel::class.java)
                progress.value = false
            }
        }
    }

    fun getUserImage(users: UserInformationModel){
        database.collection("Posts").whereEqualTo("userWhoShared",users.authName).get().addOnSuccessListener {
            val posts=it.toObjects(PostModel::class.java)
            userPost.value=posts
        }
    }
}