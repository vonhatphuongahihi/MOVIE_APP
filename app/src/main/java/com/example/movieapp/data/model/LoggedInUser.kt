package com.example.movieapp.data.model

import com.example.movieapp.Activities.Change_Password

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val userId: String,
    val displayName: String,
    var password: String,
)