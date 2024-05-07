package com.example.movieapp.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.movieapp.R

class History : AppCompatActivity() {
    lateinit var custom_adapter_history_film_list : CustomAdapter_history_film_list
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.history)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var dataList = mutableListOf<History_film_class>()
        dataList.add(History_film_class(R.drawable.thien_thien, "Liên Hoa Lâu", R.drawable.twenty_fiv_twenty_one_fav, "Twenty Five Twenty One"))
        dataList.add(History_film_class(R.drawable.thien_thien, "Liên Hoa Lâu", R.drawable.twenty_fiv_twenty_one_fav, "Twenty Five Twenty One"))
        dataList.add(History_film_class(R.drawable.thien_thien, "Liên Hoa Lâu", R.drawable.twenty_fiv_twenty_one_fav, "Twenty Five Twenty One"))
        dataList.add(History_film_class(R.drawable.thien_thien, "Liên Hoa Lâu", R.drawable.twenty_fiv_twenty_one_fav, "Twenty Five Twenty One"))
        dataList.add(History_film_class(R.drawable.thien_thien, "Liên Hoa Lâu", R.drawable.twenty_fiv_twenty_one_fav, "Twenty Five Twenty One"))
        custom_adapter_history_film_list = CustomAdapter_history_film_list(this, dataList)
        val lvHistory = findViewById<android.widget.ListView>(R.id.list_view_history)
        lvHistory.adapter = custom_adapter_history_film_list
    }
}