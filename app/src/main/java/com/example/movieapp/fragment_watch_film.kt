package com.example.movieapp

import Movie
import Comment
import CommentAdapter
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.annotation.DrawableRes
import com.example.movieapp.data.model.User
import com.google.api.Context
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
    private lateinit var ImageViewfavorite: ImageView
    private var isFavorite: Boolean = false

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

        ImageViewfavorite=root.findViewById(R.id.favorite)



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
            checkFav(it.id,this.resources)
        }
        fetchCommentFromFirebase(movie?.id)
        ImageViewfavorite.setOnClickListener{onClickFav(mAuth.currentUser?.uid, movie?.id,this.resources)}
        //checkFav(movie?.id)
        /*if(isFavorite) {
            ImageViewfavorite.setImageDrawable(this.resources.getDrawable(R.drawable.favorite_icon))
        }*/

        //var temp=editTextComment.text.toString()
        //btnUpComment.setOnClickListener{onCommentClick(editTextComment.text.toString(),movie?.id,"Anonymous",mAuth?.uid)}

        return root

    }



    private fun onClickFav(userId: String?,movieId: String?,resource: Resources){
        userId?.let {
            database.child("users").child(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        user?.let {
                            var fav = it.fav
                            movieId?.let{u->
                                if (fav.contains(u)==false){
                                    ImageViewfavorite.setImageDrawable(resource.getDrawable(R.drawable.favorite_icon))
                                    fav=fav+u+","
                                }else{
                                    fav=fav.replace(u+",","")
                                    ImageViewfavorite.setImageDrawable(resource.getDrawable(R.drawable.favorite_watch_icon))

                                }
                            }

                            val FavUpdate = mapOf("fav" to fav)

                            database.child("users").child(it.userId).updateChildren(FavUpdate)
                            /*Toast.makeText(
                                context,
                                "${fav}",
                                Toast.LENGTH_SHORT
                            ).show()*/

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }
    private fun checkFav(movieId: String, resource: Resources){

        var userId=mAuth.currentUser?.uid

        userId?.let {
            database.child("users").child(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        user?.let {
                            var fav = it.fav
                            if (fav.contains(movieId)==true){
                                ImageViewfavorite.setImageDrawable(resource.getDrawable(R.drawable.favorite_icon))
                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }

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