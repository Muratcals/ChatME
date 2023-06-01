package com.example.chatme.model

class UserInformationModel(
    val customerId: String = "",
    val name: String = "",
    val mail: String = "",
    val authName: String = "",
    val gender: String = "",
    val biography: String = "",
    val profilImage: String = "",
    val follow: List<followedModel> = emptyList(),
    val followed: List<followedModel> = emptyList()
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        emptyList(),
        emptyList()
    )
}