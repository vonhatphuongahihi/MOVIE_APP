package com.example.movieapp.Activities.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.MovieHistory

class HistoryAdapter(private val context: Context, private val historyList: List<MovieHistory>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.history_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movieHistory = historyList[position]
        holder.movieTitleTextView.text = movieHistory.title
        holder.viewedTimeTextView.text = movieHistory.viewedTime
        // Load image using Glide or Picasso library
        Glide.with(context).load(movieHistory.imageUrl).placeholder(R.drawable.tien_kiem_4).into(holder.movieImageView)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val movieImageView: ImageView = itemView.findViewById(R.id.movieImageView)
        val movieTitleTextView: TextView = itemView.findViewById(R.id.movieTitleTextView)
        val viewedTimeTextView: TextView = itemView.findViewById(R.id.viewedTimeTextView)
    }
}
