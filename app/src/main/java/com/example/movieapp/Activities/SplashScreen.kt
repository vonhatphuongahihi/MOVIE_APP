package com.example.movieapp.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.movieapp.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_creen)
        Handler().postDelayed(Runnable {
            val intent = Intent(
                this@SplashScreen,
                activity_login::class.java
            )
            startActivity(intent)
            finish()
        }, 2000)
    }
}

