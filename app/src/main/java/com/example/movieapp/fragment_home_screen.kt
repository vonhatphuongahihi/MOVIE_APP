package com.example.movieapp

import Movie
import MovieAdapter
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.movieapp.databinding.FragmentHomeScreenBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class fragment_home_screen : Fragment(), AdapterView.OnItemClickListener {

    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!
    private val movieList: ArrayList<Movie> = ArrayList()
    private val filteredMovieList: ArrayList<Movie> = ArrayList()
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var gridView: GridView
    private lateinit var searchEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        val root: View = binding.root
        gridView = root.findViewById(R.id.phimHot)
        searchEditText = binding.timKiem

        movieAdapter = MovieAdapter(requireContext(), filteredMovieList)
        gridView.adapter = movieAdapter
        gridView.onItemClickListener = this

        fetchMoviesFromFirebase()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterMovies(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                filterMovies(s.toString())
            }
        })

        return root
    }

    private fun filterMovies(query: String) {
        if (query.isNotEmpty()) {
            binding.imageView.visibility = View.GONE
            binding.textPhimHotNhat.visibility = View.GONE
            setGridViewHeight(1000) // Expanded height in pixels
        } else {
            binding.imageView.visibility = View.VISIBLE
            binding.textPhimHotNhat.visibility = View.VISIBLE
            setGridViewHeight(400) // Default height in pixels
        }

        val filteredList = movieList.filter {
            it.name?.contains(query, ignoreCase = true) == true
        }
        filteredMovieList.clear()
        filteredMovieList.addAll(filteredList)
        movieAdapter.notifyDataSetChanged()
    }

    private fun setGridViewHeight(heightInDp: Int) {
        val layoutParams: ViewGroup.LayoutParams? = gridView.layoutParams
        layoutParams?.height = convertDpToPixels(heightInDp, requireContext())
        gridView.layoutParams = layoutParams
    }

    private fun convertDpToPixels(dp: Int, context: Context): Int {
        val resources: Resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
        ).toInt()
    }

    private fun fetchMoviesFromFirebase() {
        val database = FirebaseDatabase.getInstance().reference
        database.child("movies").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                movieList.clear()
                for (movieSnapshot in snapshot.children) {
                    val movie = movieSnapshot.getValue(Movie::class.java)
                    movie?.let { movieList.add(it) }
                }
                filteredMovieList.clear()
                filteredMovieList.addAll(movieList)
                movieAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context, "Failed to fetch movies: ${error.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedMovie = filteredMovieList[position]
        val bundle = Bundle().apply {
            putParcelable("movie", selectedMovie)
        }
        binding.timKiem.setText("")
        findNavController().navigate(
            R.id.action_fragment_home_screen_to_fragment_description, bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}