package com.example.movieapp

import Movie
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.UUID

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditMovieScreen.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditMovieScreen : Fragment() {
    private lateinit var editTextMovieName: EditText
    private lateinit var editTextMovieYear: EditText
    private lateinit var editTextDirector: EditText
    private lateinit var editTextActor: EditText
    private lateinit var editTextCountry: EditText
    private lateinit var editTextCategory: EditText
    private lateinit var editTextAge: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var buttonSelectBanner: Button
    private lateinit var buttonSelectVideo: Button
    private lateinit var buttonUpdateMovie: Button
    private lateinit var imageViewBannerPreview: ImageView
    private lateinit var videoView: VideoView
    private lateinit var progressBar: ProgressBar
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var selectedBannerUri: Uri? = null
    private var selectedVideoUri: Uri? = null
    private var movie: Movie? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_edit_movie_screen, container, false)

        editTextMovieName = root.findViewById(R.id.editTextMovieName)
        editTextMovieYear = root.findViewById(R.id.editTextMovieYear)
        editTextDirector = root.findViewById(R.id.editTextDirector)
        editTextActor = root.findViewById(R.id.editTextActor)
        editTextCountry = root.findViewById(R.id.editTextCountry)
        editTextCategory = root.findViewById(R.id.editTextCategory)
        editTextAge = root.findViewById(R.id.editTextAge)
        editTextDescription = root.findViewById(R.id.editTextDescription)
        buttonSelectBanner = root.findViewById(R.id.buttonSelectBanner)
        buttonSelectVideo = root.findViewById(R.id.buttonSelectVideo)
        buttonUpdateMovie = root.findViewById(R.id.buttonUpdateMovie)
        imageViewBannerPreview = root.findViewById(R.id.imageViewBannerPreview)
        videoView = root.findViewById(R.id.videoView)
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        progressBar = root.findViewById(R.id.progressBar)
        val backButton = view?.findViewById<ImageView>(R.id.back)
        backButton?.setOnClickListener {
            requireActivity().onBackPressed()
        }
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        movie = arguments?.getParcelable("movie")

        movie?.let {
            editTextMovieName.setText(it.name)
            editTextMovieYear.setText(it.releaseYear.toString())
            editTextDirector.setText(it.director)
            editTextActor.setText(it.actor)
            editTextCountry.setText(it.country)
            editTextCategory.setText(it.category)
            editTextAge.setText(it.age.toString())
            editTextDescription.setText(it.description)
            Picasso.get().load(it.bannerURL).into(imageViewBannerPreview)
            setupVideoPreview(it.videoUrl)
        }

        buttonSelectBanner.setOnClickListener {
            selectBanner()
        }

        buttonSelectVideo.setOnClickListener {
            selectVideo()
        }

        buttonUpdateMovie.setOnClickListener {
            updateMovieDetails()
        }

        return root
    }

    private fun selectBanner() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        bannerResultLauncher.launch(intent)
    }

    private fun selectVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        videoResultLauncher.launch(intent)
    }

    private val bannerResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedBannerUri = result.data?.data
                selectedBannerUri?.let {
                    Picasso.get().load(it).into(imageViewBannerPreview)
                }
            }
        }

    private val videoResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedVideoUri = result.data?.data
                setupVideoPreview(selectedVideoUri.toString())
            }
        }

    private fun setupVideoPreview(videoUrl: String?) {
        videoUrl?.let { url ->
            val uri = Uri.parse(url)
            videoView.setVideoURI(uri)

            videoView.requestFocus()
            videoView.start()
        }
    }

    private fun updateMovieDetails() {
        val movieName = editTextMovieName.text.toString()
        val movieYear = editTextMovieYear.text.toString().toIntOrNull()
        val director = editTextDirector.text.toString()
        val actor = editTextActor.text.toString()
        val country = editTextCountry.text.toString()
        val category = editTextCategory.text.toString()
        val age = editTextAge.text.toString().toIntOrNull()
        val description = editTextDescription.text.toString()
        if (movieName.isEmpty() || movieYear == null || director.isEmpty() || actor.isEmpty() || age == null || description.isEmpty() || country.isEmpty() || category.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter valid details", Toast.LENGTH_SHORT)
                .show()
            return
        }

        progressBar.visibility = View.VISIBLE

        movie?.let {
            it.name = movieName
            it.releaseYear = movieYear
            it.director = director
            it.actor = actor
            it.country = country
            it.category = category
            it.age = age
            it.description = description
            val bannerPath = "banners/${UUID.randomUUID()}.jpg"
            val videoPath = "videos/${UUID.randomUUID()}.mp4"

            val bannerRef = storageReference.child(bannerPath)
            val videoRef = storageReference.child(videoPath)

            val uploadTasks = mutableListOf<Task<Uri>>()

            selectedBannerUri?.let { uri ->
                val bannerUploadTask = bannerRef.putFile(uri).continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    bannerRef.downloadUrl
                }
                uploadTasks.add(bannerUploadTask)
            }

            selectedVideoUri?.let { uri ->
                val videoUploadTask = videoRef.putFile(uri).continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    videoRef.downloadUrl
                }
                uploadTasks.add(videoUploadTask)
            }

            Tasks.whenAllSuccess<Uri>(uploadTasks).addOnSuccessListener { uris ->
                if (uris.isNotEmpty()) {
                    if (selectedBannerUri != null) it.bannerURL = uris[0].toString()
                    if (selectedVideoUri != null) it.videoUrl = uris[1].toString()
                }
                saveMovieToDatabase(it)
            }.addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to update movie", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun saveMovieToDatabase(movie: Movie) {
        database.child("movies").child(movie.id).setValue(movie)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Movie updated", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to update movie", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun updateMovieInFirebase(movie: Movie) {
        database.child("movies").child(movie.id).setValue(movie)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Movie updated successfully", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Failed to update movie: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditMovieScreen.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditMovieScreen().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}