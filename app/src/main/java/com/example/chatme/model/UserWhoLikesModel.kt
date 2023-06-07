package com.example.chatme.model

import com.google.firebase.Timestamp

class UserWhoLikesModel(
    val mail: String = "",
    val name: String = "",
    val authName: String = "",
    val imageUrl: String = "",
    val time: Timestamp,
) {
    constructor() : this("", "", "", "",Timestamp.now())
}