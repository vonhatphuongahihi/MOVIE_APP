package com.example.movieapp.data

import com.example.movieapp.data.model.LoggedInUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    private lateinit var firebaseRef: DatabaseReference

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe", "123")
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }


    fun getUsersFromFirebase(callback: (Result<List<LoggedInUser>>) -> Unit) {
        firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<LoggedInUser>()

                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.child("userId").getValue(String::class.java) ?: ""
                    var userName = userSnapshot.child("userName").getValue(String::class.java) ?: ""
                    val password = userSnapshot.child("password").getValue(String::class.java) ?: ""

                    // Create LoggedInUser object from retrieved data
                    val user = LoggedInUser(userId, userName, password)
                    userList.add(user)
                }

                // Invoke callback with the list of LoggedInUser objects
                callback(Result.Success(userList))
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                callback(
                    Result.Error(
                        IOException(
                            "Error getting users from Firebase",
                            error.toException()
                        )
                    )
                )
            }
        })
    }

    fun signUp(user: LoggedInUser) {
        firebaseRef = FirebaseDatabase.getInstance().getReference("user_name")
        try {
            firebaseRef.setValue(user).addOnSuccessListener {
                print("signin success")
            }
        } catch (e: Throwable) {
            print(e.message)
        }
    }
}