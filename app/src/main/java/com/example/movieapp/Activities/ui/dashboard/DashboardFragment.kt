package com.example.movieapp.Activities.ui.dashboard

import Movie
import MovieAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentDashboardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardFragment : Fragment(), AdapterView.OnItemClickListener {

    private var _binding: FragmentDashboardBinding? = null

    private var movieList: ArrayList<Movie>? = null
    private var movieAdapter: MovieAdapter? = null
    private var gridView: GridView? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        gridView = root.findViewById(R.id.gridView)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedMovie = movieList?.get(position)
        val bundle = Bundle().apply {
            putParcelable("movie", selectedMovie)
        }
        findNavController().navigate(R.id.action_navigation_dashboard_to_editMovieScreen, bundle)

    }

}