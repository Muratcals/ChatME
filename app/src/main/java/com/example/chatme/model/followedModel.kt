package com.example.chatme.model

data class followedModel(
    val customerId: String = "",
    val name: String = "",
    val authName: String = "",
    val imageUrl: String = ""
) {
    constructor() : this("", "", "", "")
}