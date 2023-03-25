package com.example.mat3_nav.model

data class Profile(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val description: String?,
    val imageUri: String?,

    var userId: String? = null
)
