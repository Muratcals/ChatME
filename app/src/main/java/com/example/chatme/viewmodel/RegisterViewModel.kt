package com.example.chatme.viewmodel

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatme.model.UserInformationModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class RegisterViewModel @Inject constructor(
    val auth:FirebaseAuth,
    val database: FirebaseFirestore
) : ViewModel() {

    val progress =MutableLiveData<Boolean>()
    fun createUser(activity: Activity,userInformation: UserInformationModel,password: String){
        progress.value=true
        database.collection("User Information").whereEqualTo("userName",userInformation.authName).get().addOnSuccessListener {
            if (it.isEmpty){
                auth.fetchSignInMethodsForEmail(userInformation.mail).addOnSuccessListener {
                    if (it.signInMethods?.isEmpty()==true){
                        if (passwordController(activity.applicationContext,password)){
                            database.collection("User Information").document(userInformation.mail).set(userInformation).addOnSuccessListener { databaseResult->
                                database.collection("User Information").document(userInformation.mail).get().addOnSuccessListener { references->
                                    references.reference.collection("follow").add("").addOnSuccessListener {
                                        auth.createUserWithEmailAndPassword(userInformation.mail,password).addOnCompleteListener { authResult->
                                            if (authResult.isSuccessful){
                                                Toast.makeText(activity.applicationContext, "Kayıt başarılı. Giriş yapabilirsin", Toast.LENGTH_SHORT).show()
                                                progress.value=false
                                                activity.finish()
                                            }else{
                                                database.collection("User Information").document(auth.currentUser!!.email.toString()).delete().addOnSuccessListener {
                                                    Toast.makeText(activity.applicationContext, "Bir sorun oluştu", Toast.LENGTH_SHORT).show()
                                                    progress.value=false
                                                }
                                            }
                                        }
                                    }
                                }
                            }.addOnFailureListener {
                                Toast.makeText(activity.applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
                                progress.value=false
                            }
                        }else{
                            progress.value=false
                        }
                    }else{
                        Toast.makeText(activity.applicationContext, "Bu e posta kullanılıyor", Toast.LENGTH_SHORT).show()
                        progress.value=false
                    }
                }
            }else{
                Toast.makeText(activity.applicationContext, "Bu kullanıcı adı zaten kullanılıyor", Toast.LENGTH_SHORT).show()
                progress.value=false
            }
        }.addOnFailureListener {
            Toast.makeText(activity.applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun passwordController(context: Context, password: String):Boolean{
        val upperCharacter="QWERTYUIOPĞÜASDFGHJKLŞİZXCVBNMÖÇ"
        if (password.length>=6){
            val controller =upperCharacter.filter {
                if(upperCharacter.contains(it)){
                    return@filter true
                }
                return@filter false
            }
            if (controller.isNotEmpty()){
                return true
            }else{
                Toast.makeText(context, "En az bir büyük harf kullanın", Toast.LENGTH_SHORT).show()
                return false
            }
        }else{
            Toast.makeText(context, "Şifre uzunlugu 6 karakterden fazla olmalıdır.", Toast.LENGTH_SHORT).show()
            return false
        }
    }
}