package com.example.mat3_nav.util

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordUtils {

    private const val bcryptStrength = 12

    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(bcryptStrength, password.toCharArray())
    }

    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        val bcrypt = BCrypt.verifyer()
        val result = bcrypt.verify(password.toCharArray(), hashedPassword)
        return result.verified
    }

}