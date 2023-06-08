package com.example.chatme.viewmodel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.example.chatme.AddPostActivity
import com.example.chatme.R
import com.example.chatme.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
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
        binding.mainBottomMenu.setOnItemSelectedListener { menuItem ->
            handleNavigation(menuItem.itemId)
            true
        }
    }
    private fun handleNavigation(itemId: Int) {
        val navController = findNavController(R.id.fragmentContainerView)
        val currentDestination = navController.currentDestination?.id
        println(currentDestination.toString())
        when (itemId) {
            R.id.profilFragment -> {
                println(R.id.proffilFragment)
                // Eğer şu anki hedef "profilFragment" ise yenileme işlemi yapma
                if (currentDestination!= R.id.proffilFragment) {
                    navController.setGraph(R.navigation.profil_grapht)
                }
            }
            R.id.homePageMenu -> {
                if (currentDestination != R.id.homePageMenu) {
                    navController.setGraph(R.navigation.main_grapht)
                }
            }
            R.id.searchMenu -> {
                if (currentDestination != R.id.searchFragment) {
                    navController.setGraph(R.navigation.search_grahpt)
                }
            }
            R.id.notification -> {
                if (currentDestination != R.id.notificationFragment) {
                    navController.setGraph(R.navigation.notification_grapht)
                }
            }
            R.id.addPhoto ->{
               val intent =Intent(applicationContext, AddPostActivity::class.java)
                startActivity(intent)
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