package com.example.movieapp.Activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.movieapp.MainActivity
import com.example.movieapp.R
import com.example.movieapp.data.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit


class Verification : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var inputcode1: EditText
    private lateinit var inputcode2: EditText
    private lateinit var inputcode3: EditText
    private lateinit var inputcode4: EditText
    private lateinit var inputcode5: EditText
    private lateinit var inputcode6: EditText
    private lateinit var xac_thuc_otp_button: Button
    private lateinit var getotpbackend: String
    private lateinit var database: DatabaseReference
    private var isAdmin: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_verification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container_verification)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference


        getotpbackend = intent.getStringExtra("verificationId").toString()

        xac_thuc_otp_button = findViewById(R.id.xac_thuc_otp_button)
        inputcode1 = findViewById(R.id.otp_input_1)
        inputcode2 = findViewById(R.id.otp_input_2)
        inputcode3 = findViewById(R.id.otp_input_3)
        inputcode4 = findViewById(R.id.otp_input_4)
        inputcode5 = findViewById(R.id.otp_input_5)
        inputcode6 = findViewById(R.id.otp_input_6)
        FirebaseApp.initializeApp(this)
        val progressBarverify: ProgressBar = findViewById(R.id.progress_bar)

        xac_thuc_otp_button.setOnClickListener {
            val entercodeotp =
                "${inputcode1.text}${inputcode2.text}${inputcode3.text}${inputcode4.text}${inputcode5.text}${inputcode6.text}"
            if (entercodeotp.length == 6) {
                if (getotpbackend.isNotEmpty()) {
                    progressBarverify.visibility = View.VISIBLE
                    xac_thuc_otp_button.visibility = View.INVISIBLE
                    val phoneAuthCredential =
                        PhoneAuthProvider.getCredential(getotpbackend, entercodeotp)
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener { task ->
                            progressBarverify.visibility = View.GONE
                            xac_thuc_otp_button.visibility = View.VISIBLE
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@Verification,
                                    "Mã xác thực chính xác. Đăng nhập thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val user = task.result.user
                                checkUserRole(user?.uid!!)
                                if (isAdmin) navigateToAdminActivity()
                                else navigateToUserActivity()
                            } else {
                                Toast.makeText(
                                    this@Verification,
                                    "Mã xác thực không chính xác",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        this@Verification,
                        "Vui lòng kiểm tra kết nối Internet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@Verification,
                    "Vui lòng nhập toàn bộ mã xác thực",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        setupOTPInputs()

        val gui_lai_ma_click = findViewById<Button>(R.id.button_gui_lai_ma)
        gui_lai_ma_click.setOnClickListener {
            val options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+84${intent.getStringExtra("phone_number")}")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this@Verification)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                        val code = phoneAuthCredential.smsCode
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        Toast.makeText(this@Verification, e.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onCodeSent(
                        newverificationId: String,
                        forceResendingToken: PhoneAuthProvider.ForceResendingToken
                    ) {
                        getotpbackend = newverificationId
                        Toast.makeText(
                            this@Verification,
                            "Mã xác thực đã được gửi lại",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }

    }

    private fun checkUserRole(userId: String) {
        val number = intent.getStringExtra("phone_number")
        if (number != null)
            database.child("users").child(userId).child("role")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val role = snapshot.getValue(String::class.java)
                        if (role != null) {
                            if (role == "admin") {
                                // Người dùng là admin
                                Log.d("Firebase", "User is admin")
                                isAdmin = true
                            } else {
                                // Người dùng không phải là admin
                                Log.d("Firebase", "User is not admin")
                                isAdmin = false
                            }

                        } else {
                            setUser(number, userId)
                            isAdmin = false
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Xử lý khi có lỗi xảy ra
                        Log.w("Firebase", "checkUserRole:onCancelled", error.toException())
                    }
                })
    }

    private fun setUser(phoneNumber: String, userId: String) {
        Log.w("number not null2", phoneNumber)
        val user = User(userId, "", "", phoneNumber, "", "", "", "member")
        database.child("users").child(userId).setValue(user)

       
e
    }

    private fun navigateToAdminActivity() {
        val intent = Intent(this, AdminActivities::class.java)
        startActivity(intent)
        finish() // Để kết thúc MainActivity
    }

    private fun navigateToUserActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Để kết thúc MainActivity
    }

    private fun setupOTPInputs() {
        inputcode1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) inputcode2.requestFocus()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        inputcode2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 0) inputcode1.requestFocus()
                else if (s?.length == 1) inputcode3.requestFocus()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        inputcode3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 0) inputcode2.requestFocus()
                else if (s?.length == 1) inputcode4.requestFocus()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        inputcode4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 0) inputcode3.requestFocus()
                else if (s?.length == 1) inputcode5.requestFocus()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        inputcode5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 0) inputcode4.requestFocus()
                else if (s?.length == 1) inputcode6.requestFocus()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
