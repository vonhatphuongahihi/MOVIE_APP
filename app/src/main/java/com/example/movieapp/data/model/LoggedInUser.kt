package com.example.movieapp.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val userId: String,
    val displayName: String,
    var password: String,
)

data class User(
    var phoneNumber: String? = null,
    var role: String? = null,
)