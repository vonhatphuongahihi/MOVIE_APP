package com.example.movieapp.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.movieapp.R
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class Login : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var sdtXacThuc: EditText
    private lateinit var guiMaXacThuc: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_login)

        sdtXacThuc = findViewById(R.id.editNhapSDT)
        guiMaXacThuc = findViewById(R.id.button_gui_ma)
        progressBar = findViewById(R.id.progress_bar)

        mAuth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        guiMaXacThuc.setOnClickListener {
            if (sdtXacThuc.text.toString().isNotEmpty()) {
                if (sdtXacThuc.text.toString().trim().length == 9) {
                    progressBar.visibility = View.VISIBLE
                    guiMaXacThuc.visibility = View.INVISIBLE

                    val options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+84${sdtXacThuc.text}")
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(object :
                            PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                                val code = phoneAuthCredential.smsCode
                                progressBar.visibility = View.GONE
                                guiMaXacThuc.visibility = View.VISIBLE
                            }

                            override fun onVerificationFailed(e: FirebaseException) {
                                progressBar.visibility = View.GONE
                                guiMaXacThuc.visibility = View.VISIBLE
                                Toast.makeText(this@Login, e.message, Toast.LENGTH_SHORT).show()
                            }

                            override fun onCodeSent(
                                verificationId: String,
                                forceResendingToken: PhoneAuthProvider.ForceResendingToken
                            ) {
                                progressBar.visibility = View.GONE
                                guiMaXacThuc.visibility = View.VISIBLE
                                val intent = Intent(applicationContext, Verification::class.java)
                                intent.putExtra("phone_number", sdtXacThuc.text.toString())
                                intent.putExtra("verificationId", verificationId)
                                startActivity(intent)
                            }
                        })
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                    val intent = Intent(applicationContext, Verification::class.java)
                    intent.putExtra("phone_number", sdtXacThuc.text.toString())
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Nhập số điện thoại hợp lệ", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Nhập số điện thoại", Toast.LENGTH_SHORT).show()
            }
        }
    }
}