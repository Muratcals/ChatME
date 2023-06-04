package com.example.chatme.model

import com.google.firebase.Timestamp

data class followedModel(
    val mail: String = "",
    val name: String = "",
    val authName: String = "",
    val imageUrl: String = "",
    val time:Timestamp,
    val bool:Boolean?=false,
) {
    constructor() : this("", "", "", "",Timestamp.now())
}