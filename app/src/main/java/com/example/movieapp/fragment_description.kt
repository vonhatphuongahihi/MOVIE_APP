package com.example.movieapp

import Movie
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso


class fragment_description : Fragment() {

    private var movie: Movie? = null
    private lateinit var imageViewBannerPreview: ImageView
    private lateinit var textViewTitle: TextView
    private lateinit var textViewDuration: TextView
    private lateinit var textViewYear: TextView
    private lateinit var textViewGenre1: TextView
    private lateinit var textViewGenre2: TextView
    private lateinit var textViewContent: TextView
    private lateinit var textViewDirector: TextView

    private lateinit var btnBack: Button
    private lateinit var btnWatch: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_description, container, false)
        imageViewBannerPreview=root.findViewById(R.id.image_rectangle1)
        textViewTitle=root.findViewById(R.id.tua_de_phim)
        textViewDuration=root.findViewById(R.id.text_duration)
        textViewYear=root.findViewById(R.id.text_date)
        textViewGenre1=root.findViewById(R.id.text_the_loai_1)
        textViewGenre2=root.findViewById(R.id.text_the_loai_2)
        textViewContent=root.findViewById(R.id.text_content)
        textViewDirector=root.findViewById(R.id.text_dao_dien)
        btnBack=root.findViewById(R.id.ic_previous_ltr)
        btnWatch=root.findViewById((R.id.button_bat_dau_xem))

        movie = arguments?.getParcelable("movie")

        movie?.let {
            textViewTitle.setText(it.name)
            textViewYear.setText(it.releaseYear.toString())
            textViewDirector.setText(it.director)
            //editTextActor.setText(it.actor)
            //editTextAge.setText(it.age.toString())
            textViewContent.setText(it.description)
            Picasso.get().load(it.bannerURL).into(imageViewBannerPreview)
            //setupVideoPreview(it.videoUrl)
        }

        btnBack.setOnClickListener {onBackClick()}
        btnWatch.setOnClickListener{onWatchClick()}
        return root
    }


    private fun onBackClick () {
        findNavController().navigate(R.id.action_fragment_description_to_fragment_home_screen)

    }

    private fun onWatchClick (){
        val bundle = Bundle().apply {
            putParcelable("movie", movie)
        }
        findNavController().navigate(R.id.action_fragment_description_to_fragment_watch_film, bundle)
    }
    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_description().apply {
                arguments = Bundle().apply {

                }
            }
    }
}