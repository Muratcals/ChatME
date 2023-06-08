package com.example.chatme.view

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PostDetailsViewModel @Inject constructor(
    val database:FirebaseFirestore,
    val getAuth: FirebaseUser
) : ViewModel() {
    // TODO: Implement the ViewModel
}