package com.example.chatme.viewmodel

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatme.model.PostModel
import com.example.chatme.model.UserInformationModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.scopes.ActivityScoped
import java.util.UUID
import javax.inject.Inject

@ActivityScoped
class ImageSuccessragmentViewModel @Inject constructor(
    val database:FirebaseFirestore,
    val storage :FirebaseStorage,
    val getAuth:FirebaseUser
) : ViewModel() {

    val progress =MutableLiveData<Boolean>()
    fun postImage(activity:Activity,uri: Uri,explanation:String){
        progress.value=false
        val storageUuid =UUID.randomUUID().toString()
        val databaseUuid =UUID.randomUUID().toString()
        val storageReference =storage.getReference(storageUuid)
        storageReference.putFile(uri).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { downloadUrl->
                database.collection("User Information").document(getAuth.email.toString()).get().addOnSuccessListener {
                    if (it.exists()){
                        val currentUserData=it.toObject(UserInformationModel::class.java)
                        val url =downloadUrl.toString()
                        val imageData=PostModel(currentUserData!!.authName,currentUserData.profilImage,explanation,url,databaseUuid,0)
                        database.collection("Posts").document(databaseUuid).set(imageData).addOnSuccessListener {
                            Toast.makeText(activity.applicationContext, "Fotoğraf başarıyla yüklendi", Toast.LENGTH_SHORT).show()
                            progress.value=false
                            activity.finish()
                        }
                    }
                }

            }
        }
    }
}