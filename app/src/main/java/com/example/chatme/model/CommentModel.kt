package com.example.chatme.model

import com.google.firebase.Timestamp

class CommentModel(
    val commenter:String,
    val commentText:String,
    val commentNumberOfLike:Int,
    val commenterImage:String,
    val commentTime:Timestamp=Timestamp.now()
) {
    constructor() :this("","",0,"",Timestamp.now())
}