package com.example.chatme.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatme.model.UserInformationModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityScoped
import java.util.UUID
import javax.inject.Inject

@ActivityScoped
class SearchViewModel @Inject constructor(
    val database :FirebaseFirestore,
    val getAuth: FirebaseUser,
    val auth:FirebaseAuth
) : ViewModel() {

    val authList= MutableLiveData<List<UserInformationModel>>()
    fun getAllUser(){
        database.collection("User Information").get().addOnSuccessListener {
            val list =it.toObjects(UserInformationModel::class.java)
            authList.value=list
        }
    }

}