package com.example.movieapp.Activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.movieapp.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import java.util.concurrent.TimeUnit

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String
    private lateinit var firebaseRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_login)
        var phoneEditText = findViewById<EditText>(R.id.nhap_sdt_dang_nhap)
        var otpEditText = findViewById<EditText>(R.id.nhap_mat_khau_dang_nhap)
        val sendOtpButton = findViewById<Button>(R.id.button_dang_nhap)
        auth.setLanguageCode("vi")
        sendOtpButton.setOnClickListener {
            val phoneNumber = phoneEditText.text.toString().trim()
            sendVerificationCode(phoneNumber)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    // Show a message and update your UI.
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // The SMS verification code has been sent to the provided phone number,
                    // we now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    this@Login.verificationId = verificationId
                    // Save verification ID and resending token so we can use them later
                }
            }) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = task.result?.user
                    Log.d("LoginActivity", "signInWithCredential:success")
                } else {
                    Log.d("LoginActivity", "signInWithCredential: false")
                }
            }
    }
}