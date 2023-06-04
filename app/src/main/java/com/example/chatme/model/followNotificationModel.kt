package com.example.chatme.model

class followNotificationModel(
    val categoryName:String,
    val mail:String,
    val imageUrl:String,
    val authName:String,
    val name:String,
    val state:Boolean?=false
) {
    constructor() : this ("","","","","")
}