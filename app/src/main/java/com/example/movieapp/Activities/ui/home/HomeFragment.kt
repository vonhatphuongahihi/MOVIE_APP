package com.example.movieapp.Activities.ui.home

import Movie
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentHomeBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.UUID

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var selectedImageUri: Uri? = null
    private var selectedVideoUri: Uri? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var videoView: VideoView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FirebaseApp.initializeApp(requireContext())
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        videoView = root.findViewById(R.id.videoViewAddMovie)

        progressBar = root.findViewById(R.id.progressBar)
        val buttonSelectImage = root.findViewById<Button>(R.id.button_add_banner)
        val buttonSelectVideo = root.findViewById<Button>(R.id.button_add_file)
        val buttonUpload = root.findViewById<Button>(R.id.button_add_movie)

        buttonSelectImage.setOnClickListener {
            selectImage()
        }

        buttonSelectVideo.setOnClickListener {
            selectVideo()
        }

        buttonUpload.setOnClickListener {
            val movieName = binding.editTextMovieName.text.toString()
            val movieYear = binding.editTextMovieType.text.toString()
            val director = root.findViewById<EditText>(R.id.ediText_daodien).text.toString()
            val actor = root.findViewById<EditText>(R.id.ediText_dienvien).text.toString()
            val country = root.findViewById<EditText>(R.id.ediText_quocgia).text.toString()
            val category = root.findViewById<EditText>(R.id.ediText_category).text.toString()
            val age = root.findViewById<EditText>(R.id.ediText_age).text.toString()
            val description = root.findViewById<EditText>(R.id.ediText_description).text.toString()

            if (movieName.isEmpty() || movieYear.isEmpty() || director.isEmpty() || actor.isEmpty() || age.isEmpty() || description.isEmpty() || selectedImageUri == null || selectedVideoUri == null || country.isEmpty() || category.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Vui lòng nhập đầy đủ thông tin và chọn tệp!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            uploadFiles(
                movieName,
                movieYear,
                director,
                actor,
                country,
                category,
                age,
                description,
                requireContext()
            )
        }


        homeViewModel.text.observe(viewLifecycleOwner) {
        }
        return root

    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imageResultLauncher.launch(intent)
    }

    private fun selectVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        videoResultLauncher.launch(intent)
    }

    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedImageUri = result.data?.data
                val image = binding.imageViewMovieBanner
                Picasso.get().load(selectedImageUri).into(image)
                Toast.makeText(
                    requireContext(),
                    "Image Selected: $selectedImageUri",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val videoResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedVideoUri = result.data?.data
                videoView.setVideoURI(selectedVideoUri)
                val mediaController = MediaController(requireContext())
                mediaController.setAnchorView(videoView)
                videoView.requestFocus()
                videoView.start()
                Toast.makeText(
                    requireContext(),
                    "Video Selected: $selectedVideoUri",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun uploadFiles(
        movieName: String,
        movieYear: String,
        director: String,
        actor: String,
        country: String,
        category: String,
        age: String,
        description: String,
        context: Context
    ) {
        progressBar.visibility = View.VISIBLE
        progressBar.progress = 0

        val bannerPath = "banners/${UUID.randomUUID()}.jpg"
        val videoPath = "videos/${UUID.randomUUID()}.mp4"

        val bannerRef = storageReference.child(bannerPath)
        val videoRef = storageReference.child(videoPath)

        selectedImageUri?.let { imageUri ->
            bannerRef.putFile(imageUri)
                .addOnSuccessListener {
                    bannerRef.downloadUrl.addOnSuccessListener { bannerUri ->
                        selectedVideoUri?.let { videoUri ->
                            videoRef.putFile(videoUri)
                                .addOnSuccessListener {
                                    videoRef.downloadUrl.addOnSuccessListener { videoUri ->
                                        val movie = Movie(
                                            UUID.randomUUID().toString(),
                                            movieName,
                                            movieYear.toInt(),
                                            bannerUri.toString(),
                                            videoUri.toString(),
                                            director,
                                            actor,
                                            country,
                                            category,
                                            age.toInt(),
                                            description,
                                        )
                                        addMovieToFirebase(movie, context)
                                        progressBar.visibility = View.GONE
                                    }
                                }.addOnProgressListener { taskSnapshot ->
                                    val progress =
                                        (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                                    progressBar.progress = progress.toInt()
                                }
                                .addOnFailureListener { e ->
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(
                                        context,
                                        "Failed to upload video: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        context,
                        "Failed to upload image: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun addMovieToFirebase(movie: Movie, context: Context) {
        database.child("movies").child(movie.id).setValue(movie)
            .addOnSuccessListener {
                Toast.makeText(context, "Movie added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to add movie: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun uploadFileToFirebase(path: String, fileUri: Uri, context: Context) {
        val fileReference = storageReference.child(path)
        fileReference.putFile(fileUri)
            .addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                    Toast.makeText(context, "File Uploaded: $uri", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to upload file: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}