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
import java.util.Locale


class fragment_home_screen : Fragment(), AdapterView.OnItemClickListener {


    private var _binding: FragmentHomeScreenBinding? = null
    private var movieList: ArrayList<Movie>? = null
    private var filteredMovieList: ArrayList<Movie>? = null
    private var movieAdapter: MovieAdapter? = null
    private var gridView: GridView? = null
    private val binding get() = _binding!!
    private var searchEditText: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        val root: View = binding.root
        gridView = root.findViewById(R.id.phimHot)
        searchEditText = binding.timKiem
        movieList = ArrayList()

        fetchMoviesFromFirebase()
        gridView?.onItemClickListener = this
        searchEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                searchList(s.toString())
            }
        })

        return root
    }

    private fun searchList(text: String) {
        var bannerView = binding.imageView
        var textview = binding.textPhimHotNhat
        if (text.isNotEmpty()) {
            bannerView.visibility = View.GONE
            textview.visibility = View.GONE
            val layoutParams: ViewGroup.LayoutParams? = gridView?.layoutParams
            layoutParams?.height = convertDpToPixels(1000, requireContext()) //this is in pixels
            gridView?.layoutParams = layoutParams
        } else {
            bannerView.visibility = View.VISIBLE
            textview.visibility = View.VISIBLE
            val layoutParams: ViewGroup.LayoutParams? = gridView?.layoutParams
            layoutParams?.height = convertDpToPixels(400, requireContext()) //this is in pixels
            gridView?.layoutParams = layoutParams
        }
        val searchList = ArrayList<Movie>()
        for (movie in movieList!!) {
            if (movie.name?.lowercase()?.contains(text.lowercase()) == true) {
                searchList.add(movie)
            }
        }
        movieAdapter?.searchDataList(searchList)
    }

    fun convertDpToPixels(dp: Int, context: Context): Int {
        val resources: Resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun fetchMoviesFromFirebase() {
        val database = FirebaseDatabase.getInstance().reference
        database.child("movies").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                movieList?.clear()
                for (movieSnapshot in snapshot.children) {
                    val movie = movieSnapshot.getValue(Movie::class.java)
                    movie?.let { movieList?.add(it) }
                    updateUIWithMovies()
                }
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

    private fun filterMovies(query: String) {
        filteredMovieList?.clear()
        val lowerCaseQuery = query.lowercase(Locale.getDefault())
        for (movie in movieList.orEmpty()) {
            if (movie.name?.contains(query) == true) {
                filteredMovieList?.add(movie)
            }
        }
        updateUIWithMovies()
    }

    private fun updateUIWithMovies() {
        movieAdapter = movieList?.let { MovieAdapter(requireContext(), it) }
        gridView?.adapter = movieAdapter
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedMovie = movieList?.get(position)
        val bundle = Bundle().apply {
            putParcelable("movie", selectedMovie)
        }
        findNavController().navigate(
            R.id.action_fragment_home_screen_to_fragment_description,
            bundle
        )

    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_home_screen().apply {

            }
    }
}