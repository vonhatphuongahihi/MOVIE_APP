package com.example.movieapp

import Movie
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView


class fragment_watch_film : Fragment() {

    private lateinit var textTitle : TextView
    private lateinit var textSubtitle : TextView
    private lateinit var videoView: VideoView
    private lateinit var progressBar: ProgressBar

    private var movie: Movie? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root=inflater.inflate(R.layout.fragment_watch_film, container, false)

        textTitle=root.findViewById(R.id.text_ten_phim)
        textSubtitle=root.findViewById(R.id.text_thong_tin_phim)
        videoView = root.findViewById(R.id.phim)
        progressBar=root.findViewById(R.id.progress_bar)

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        movie = arguments?.getParcelable("movie")

        movie?.let {
            textTitle.setText(it.name)
            textSubtitle.setText(it.releaseYear.toString()+"|"+it.director)
            setupVideoPreview(it.videoUrl)
        }

        return root

    }


    private fun setupVideoPreview(videoUrl: String?) {
        videoUrl?.let { url ->
            val uri = Uri.parse(url)
            videoView.setVideoURI(uri)

            videoView.requestFocus()
            videoView.start()
        }
    }
    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_watch_film().apply {
                arguments = Bundle().apply {

                }
            }
    }
}