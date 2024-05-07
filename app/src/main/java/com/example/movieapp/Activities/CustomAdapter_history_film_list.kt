package com.example.movieapp.Activities

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.movieapp.R

class CustomAdapter_history_film_list (val activity: Activity, val list: List<History_film_class>): ArrayAdapter<History_film_class>(activity, R.layout.history_film_item)
{
    override fun getCount(): Int {
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val contexs = activity.layoutInflater
        val rowview = contexs.inflate(R.layout.history_film_item, parent, false)
        val image1 = rowview.findViewById<ImageView>(R.id.image_history_1)
        val image2 = rowview.findViewById<ImageView>(R.id.image_history_2)
        val title1 = rowview.findViewById<TextView>(R.id.title_history_1)
        val title2 = rowview.findViewById<TextView>(R.id.title_history_2)
        image1.setImageResource(list[position].image1)
        title1.text = list[position].title1
        image2.setImageResource(list[position].image2)
        title2.text = list[position].title2
        return rowview
    }
}