package com.example.movieapp

import Movie
import Comment
import CommentAdapter
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.movieapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.UUID



class fragment_description : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private var movie: Movie? = null
    private lateinit var imageViewBannerPreview: ImageView
    private lateinit var textViewTitle: TextView
    private lateinit var textViewDuration: TextView
    private lateinit var textViewYear: TextView
    private lateinit var textViewGenre: TextView
    private lateinit var textViewContent: TextView
    private lateinit var textViewDirector: TextView
    private lateinit var textViewActor: TextView

    private lateinit var btnBack: ImageButton
    private lateinit var btnWatch: Button

    private lateinit var editTextComment: EditText
    private lateinit var btnUpComment: Button

    private var commentList: ArrayList<Comment>? = null
    private var commentAdapter: CommentAdapter? = null
    private var gridView: GridView? = null
    //private lateinit var avatarImageView: ImageView
    //private var imageUri: Uri? = null
    private var userList: ArrayList<User>? = null
    private lateinit var textUserName: TextView
    private lateinit var textAvatar: TextView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_description, container, false)
        mAuth = FirebaseAuth.getInstance()
        btnBack=root.findViewById(R.id.back)
        database = FirebaseDatabase.getInstance().reference
        imageViewBannerPreview=root.findViewById(R.id.image_rectangle1)
        textViewTitle=root.findViewById(R.id.tua_de_phim)
        textViewActor=root.findViewById(R.id.text_dien_vien)
        textViewYear=root.findViewById(R.id.text_date)
        textViewGenre=root.findViewById(R.id.text_the_loai_2)
        textViewContent=root.findViewById(R.id.text_content)
        textViewDirector=root.findViewById(R.id.text_dao_dien)
        //btnBack=root.findViewById(R.id.ic_previous_ltr)
        btnWatch=root.findViewById((R.id.button_bat_dau_xem))

        btnUpComment=root.findViewById(R.id.button_comment)
        gridView = root.findViewById(R.id.comment_gridview)
        editTextComment=root.findViewById(R.id.edittext_new_comment)

        textUserName=root.findViewById(R.id.textViewName)
        textAvatar=root.findViewById(R.id.avatarURL)

        userList= ArrayList()
        commentList= ArrayList()
        movie = arguments?.getParcelable("movie")

        movie?.let {
            textViewTitle.setText(it.name)
            textViewYear.setText(it.releaseYear.toString())
            textViewDirector.setText(it.director)
            textViewGenre.setText(it.category)
            textViewActor.setText(it.actor)
            //editTextActor.setText(it.actor)
            //editTextAge.setText(it.age.toString())
            textViewContent.setText(it.description)
            Picasso.get().load(it.bannerURL).into(imageViewBannerPreview)
            //setupVideoPreview(it.videoUrl)
        }
        fetchCommentFromFirebase(movie?.id)

        btnBack.setOnClickListener {onBackClick()}
        btnWatch.setOnClickListener{onWatchClick()}
        btnUpComment.setOnClickListener{onCommentClick(editTextComment.text.toString(),movie?.id,mAuth.uid)}
        return root
    }



    private fun fetchCommentFromFirebase(id: String?) {
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


    private fun onBackClick () {
        findNavController().navigate(R.id.action_fragment_description_to_fragment_home_screen)

    }

    private fun onWatchClick (){
        val bundle = Bundle().apply {
            putParcelable("movie", movie)
        }
        findNavController().navigate(R.id.action_fragment_description_to_fragment_watch_film, bundle)
    }

    private fun onCommentClick (content: String?, idMovie: String?, uid: String?) {
        val id=UUID.randomUUID().toString()
        var avatarURL : String? = ""
        var name : String? = ""
        val userId =mAuth.currentUser?.uid
        val array:ArrayList<String>
        userId?.let {u->
            database.child("users").child(u).get().addOnCompleteListener{
                val user  = it.result.getValue(User::class.java)
                val comment = Comment(
                    id,
                    user?.name,
                    user?.avatarUrl,
                    content,
                    uid,
                    idMovie,
                )
                database.child("comment").child(id).setValue(comment)
            }
        }

        editTextComment.setText("");
    }



    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_description().apply {
                arguments = Bundle().apply {

                }
            }
    }

}