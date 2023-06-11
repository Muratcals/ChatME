package com.example.chatme.model.NatificationModel

import com.google.firebase.Timestamp

class LikeModel(
    val postId:String,
    val authName:String,
    val authImage:String,
    val postImage:String,
    val categoryName:String,
    val mail:String,
    val state:Boolean=false,
    val time: Timestamp=Timestamp.now()
) {
    constructor():this("","","","","","")
}