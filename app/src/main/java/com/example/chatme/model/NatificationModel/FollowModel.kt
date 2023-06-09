package com.example.chatme.model.NatificationModel

import com.google.firebase.Timestamp

class FollowModel(
    val authName:String,
    val name:String,
    val authImage:String,
    val categoryName :String,
    val mail:String,
    val time: Timestamp=Timestamp.now()
) {
    constructor():this("","","","","")
}