package com.example.movieapp.data.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val userId: String,
    val displayName: String,
    var password: String,
)


data class User(
    var userId: String = "",
    var name: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var birthdate: String = "",
    var gender: String = "",
    var avatarUrl: String = "",
    var role: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(), parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(phoneNumber)
        parcel.writeString(birthdate)
        parcel.writeString(gender)
        parcel.writeString(avatarUrl)
        parcel.writeString(role)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}