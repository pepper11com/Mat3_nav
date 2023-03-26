package com.example.mat3_nav.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SendGridApi {
    @POST("v3/mail/send")
    fun sendEmail(
        @Header("Authorization") authHeader: String,
        @Body emailRequest: EmailRequest
    ): Call<Void>
}
