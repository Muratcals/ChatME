package com.example.chatme.model.NatificationModel

import com.google.firebase.Timestamp

class RequestModel (
    val authName:String,
    val name:String,
    val authImage:String,
    val categoryName:String,
    val mail:String,
    val state:Boolean=false,
    val time:Timestamp=Timestamp.now()
    ){
        constructor():this("","","","","")
}