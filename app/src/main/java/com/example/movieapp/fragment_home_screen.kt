package com.example.movieapp

import Movie
import MovieAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast

import androidx.navigation.fragment.findNavController
import com.example.movieapp.databinding.FragmentHomeScreenBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class fragment_home_screen : Fragment(), AdapterView.OnItemClickListener {


    private var _binding: FragmentHomeScreenBinding? = null
    private var movieList: ArrayList<Movie>? = null
    private var movieAdapter: MovieAdapter? = null
    private var gridView: GridView? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        val root: View = binding.root
        gridView = root.findViewById(R.id.phimHot)
        movieList = ArrayList()
        fetchMoviesFromFirebase()
        gridView?.onItemClickListener = this
        return root
    }

    private fun fetchMoviesFromFirebase() {
        val database = FirebaseDatabase.getInstance().reference
        database.child("movies").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                movieList?.clear()
                for (movieSnapshot in snapshot.children) {
                    val movie = movieSnapshot.getValue(Movie::class.java)
                    movie?.let { movieList?.add(it) }
                }
                updateUIWithMovies()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context,
                    "Failed to fetch movies: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun updateUIWithMovies() {
        movieAdapter = movieList?.let { MovieAdapter(requireContext(), it) }
        gridView?.adapter = movieAdapter
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedMovie = movieList?.get(position)
        val bundle = android.os.Bundle().apply {
            putParcelable("movie", selectedMovie)
        }
        findNavController().navigate(com.example.movieapp.R.id.action_fragment_home_screen_to_fragment_description, bundle)

    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_home_screen().apply {

            }
    }
}