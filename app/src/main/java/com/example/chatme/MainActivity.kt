package com.example.chatme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.chatme.databinding.ActivityMainBinding
import com.example.chatme.model.MessageModel
import com.example.chatme.model.UserInformationModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var database: FirebaseFirestore
    @Inject lateinit var getAuth:FirebaseUser
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val uuid = UUID.randomUUID().variant()
        supportActionBar!!.hide()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        //binding.mainBottomMenu.setupWithNavController(navHostFragment.navController)
        binding.mainBottomMenu.setOnItemSelectedListener {
            when (it.itemId){
                R.id.profilFragment->{
                    findNavController(R.id.fragmentContainerView).setGraph(R.navigation.profil_grapht)
                    true
                }
                R.id.homePageMenu->{
                    findNavController(R.id.fragmentContainerView).setGraph(R.navigation.main_grapht)
                    true
                }
                R.id.searchMenu->{
                    findNavController(R.id.fragmentContainerView).setGraph(R.navigation.search_grahpt)
                    true
                }
                R.id.notification->{
                    findNavController(R.id.fragmentContainerView).setGraph(R.navigation.notification_grapht)
                    true
                }
                else->{
                    false
                }
            }
        }
    }
    /*
           button.setOnClickListener {
               firebase.orderByChild("receivingTheMessage").equalTo("murat").get().addOnSuccessListener { snapshot->
                   if (snapshot.value==null){
                       postMessage(uuid)
                       println("boş")
                   }else{
                       postMessage(snapshot.key!!)
                       println("dolu")
                   }
               }
           }

            */

    /*
    fun postMessage(uuid:String){
        val user = MessageModel(
            "murat",
            "emulator",
            "hello World",
            "${LocalTime.now().hour}:${LocalTime.now().minute}"
        )
        val ref =firebase.push().key!!
        firebase.child(uuid).child(ref).setValue(user).addOnSuccessListener {
            println("yollandı")
        }.addOnFailureListener {
            println(it.localizedMessage)
        }
    }

     */
}