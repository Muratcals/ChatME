package com.example.chatme.model

import com.google.firebase.Timestamp

class followNotificationModel(
    val categoryName:String,
    val mail:String,
    val imageUrl:String,
    val authName:String,
    val name:String,
    val state:Boolean?=false,
    val time:Timestamp
) {
    constructor() : this ("","","","","",state = false,Timestamp.now())
}