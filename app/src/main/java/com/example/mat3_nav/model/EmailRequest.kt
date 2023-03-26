package com.example.mat3_nav.model

data class EmailRequest(
    val personalizations: List<Personalization>,
    val from: EmailAddress,
    val subject: String,
    val content: List<Content>
)

data class Personalization(val to: List<EmailAddress>)
data class EmailAddress(val email: String)
data class Content(val type: String, val value: String)

