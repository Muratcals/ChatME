package com.example.chatme.model

import com.google.firebase.Timestamp

class CommentModel(
    val commentId:String,
    val commenter:String,
    val commentText:String,
    val commenterImage:String,
    val commentTime:Timestamp=Timestamp.now()
) {
    constructor() :this("","","","",Timestamp.now())
}