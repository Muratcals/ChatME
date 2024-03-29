package com.example.chatme.model

import com.google.firebase.Timestamp


class PostModel(
    val sharedMail:String,
    val explanation:String,
    val imageUrl:String,
    val id:String,
    val numberOfLikes:Int,
    val time: Timestamp =Timestamp.now()

) {

    constructor() : this("","","","",0)
}