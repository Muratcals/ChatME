package com.example.chatme.model.NatificationModel

import android.media.Image
import com.google.firebase.Timestamp

class CommentsModel(
    val postId:String,
    val commentId:String,
    val commentText:String,
    val authName:String,
    val authImage: String,
    val categoryName:String,
    val mail:String,
    val state:Boolean=false,
    val time: Timestamp=Timestamp.now()
) {
    constructor():this("","","","","","","")
}