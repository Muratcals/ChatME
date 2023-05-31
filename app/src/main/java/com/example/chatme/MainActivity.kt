package com.example.chatme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chatme.databinding.ActivityMainBinding
import com.example.chatme.model.MessageModel
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var firebase: DatabaseReference
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val uuid = UUID.randomUUID().variant()
        val navcontroller =findNavController(binding.fragmentContainerView.id)
        binding.mainBottomMenu.setupWithNavController(navcontroller)
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
}