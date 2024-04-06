package com.example.movieapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.movieapp.Domain.SliderItems
import com.example.movieapp.R

class SliderAdapters(private var sliderItems: List<SliderItems>, private val viewPager2: ViewPager2) : RecyclerView.Adapter<SliderAdapters.SliderViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.slide_item_container, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.setImage(sliderItems[position])
        if (position == sliderItems.size - 2) {
            viewPager2.post(runnable)
        }
    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }

    inner class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageSlide)

        fun setImage(slideItem: SliderItems) {
            val requestOptions = RequestOptions()
                .transforms(CenterCrop(), RoundedCorners(60))
            Glide.with(context)
                .load(slideItem.getImage())
                .apply(requestOptions)
                .into(imageView)
        }
    }

    private val runnable = Runnable {
        sliderItems += sliderItems
        notifyDataSetChanged()
    }
}
