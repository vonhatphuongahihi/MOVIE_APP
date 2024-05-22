package com.example.movieapp

import Movie
import Comment
import CommentAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID


class fragment_watch_film : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var textTitle : TextView
    private lateinit var textSubtitle : TextView
    private lateinit var videoView: VideoView
    private lateinit var progressBar: ProgressBar
    private var movie: Movie? = null

    //private lateinit var editTextComment: EditText
    //private lateinit var btnUpComment: Button

    private var commentList: ArrayList<Comment>? = null
    private var commentAdapter: CommentAdapter? = null
    private var gridView: GridView? = null

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

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        textTitle=root.findViewById(R.id.text_ten_phim)
        textSubtitle=root.findViewById(R.id.text_thong_tin_phim)
        videoView = root.findViewById(R.id.phim)
        progressBar=root.findViewById(R.id.progress_bar)

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        //btnUpComment=root.findViewById(R.id.button_comment)
        gridView = root.findViewById(R.id.comment_gridview)
        //editTextComment=root.findViewById(R.id.edittext_new_comment)

        commentList= ArrayList()
        movie = arguments?.getParcelable("movie")

        movie?.let {
            textTitle.setText(it.name)
            textSubtitle.setText(it.releaseYear.toString()+"|"+it.director)
            setupVideoPreview(it.videoUrl)
        }
        fetchCommentFromFirebase(movie?.id)

        //var temp=editTextComment.text.toString()
        //btnUpComment.setOnClickListener{onCommentClick(editTextComment.text.toString(),movie?.id,"Anonymous",mAuth?.uid)}

        return root

    }


    private fun fetchCommentFromFirebase(id: String?) {
        val database = FirebaseDatabase.getInstance().reference


        database.child("comment").orderByChild("movieId").equalTo(id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    commentList?.clear()
                    for (commentSnapshot in snapshot.children) {
                        val comment  = commentSnapshot.getValue(Comment::class.java)

                        comment?.let {
                            //if(it.movieId.equals(id))
                            commentList?.add(it)

                        }

                    }
                    updateUIWithComments()
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

    private fun updateUIWithComments() {
        commentAdapter = commentList?.let { CommentAdapter(this.requireActivity(), it) }
        gridView?.adapter = commentAdapter
    }
    private fun onCommentClick (content: String?, idMovie: String?, name: String?, uid: String?) {
        val id= UUID.randomUUID().toString()
        val comment = Comment(
            id,
            name,
            content,
            uid,
            idMovie,
        )
        database.child("comment").child(id).setValue(comment)
        //editTextComment.setText("");
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