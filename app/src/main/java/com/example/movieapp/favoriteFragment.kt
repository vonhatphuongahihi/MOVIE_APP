package com.example.movieapp

import Movie
import MovieAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridLayout
import android.widget.GridView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.movieapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale


class favoriteFragment : Fragment(), AdapterView.OnItemClickListener {

    private lateinit var gridMovie: GridView
    private var movieList: ArrayList<Movie>? = null
    private var movieAdapter: MovieAdapter? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root=inflater.inflate(R.layout.fragment_favorite, container, false)
        database = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        movieList = ArrayList()

        gridMovie=root.findViewById(R.id.gridViewYeuThich)
        fetchMoviesFromFirebase(mAuth.currentUser?.uid)

        gridMovie?.onItemClickListener = this

        return root
    }


    private fun fetchMoviesFromFirebase(userId: String?) {
        database.child("movies").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                movieList?.clear()
                var movieSS=snapshot

                userId?.let {
                    database.child("users").child(it)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val user = snapshot.getValue(User::class.java)
                                user?.let {
                                    var fav = it.fav

                                    for (movieSnapshot in movieSS.children) {
                                        val movie = movieSnapshot.getValue(Movie::class.java)
                                        movie?.let {
                                            if(fav.contains(it.id)==true) {
                                                movieList?.add(it)
                                            }
                                        }
                                        updateUIWithMovies()
                                    }
                                    Toast.makeText(
                                        requireContext(),
                                        "Thêm phim vào danh sách yêu thích thành công",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(
                                    context,
                                    "Thêm phim vào danh sách yêu thích thất bại: ${error.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context,
                    "Thêm phim vào danh sách yêu thích thất bại: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }



    private fun updateUIWithMovies() {
        movieAdapter = movieList?.let { MovieAdapter(requireContext(), it) }
        gridMovie?.adapter = movieAdapter
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedMovie = movieList?.get(position)
        val bundle = Bundle().apply {
            putParcelable("movie", selectedMovie)
        }
        findNavController().navigate(
            R.id.action_favoriteFragment_to_fragment_description,
            bundle
        )

    }
    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            favoriteFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}