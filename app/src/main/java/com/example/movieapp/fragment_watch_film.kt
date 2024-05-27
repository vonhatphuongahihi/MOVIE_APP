package com.example.movieapp

import Movie
import Comment
import CommentAdapter
import android.content.Context
import android.content.pm.ActivityInfo
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
import androidx.core.content.ContextCompat
import com.example.movieapp.data.model.User
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.MediaItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

class fragment_watch_film : Fragment() {
    var isFullScreen = false
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var textTitle: TextView
    private lateinit var textSubtitle: TextView
    private lateinit var ImageViewfavorite: ImageView
    private var isFavorite: Boolean = false
    private var videoUrl: String? = null
    private var movie: Movie? = null

    private var commentList: ArrayList<Comment>? = null
    private var commentAdapter: CommentAdapter? = null
    private var gridView: GridView? = null
    private lateinit var simpleExoPlayer: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            videoUrl = it.getString("videoUrl") // Lấy URL video từ các đối số
            movie = it.getParcelable("movie")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_watch_film, container, false)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        textTitle = root.findViewById(R.id.text_ten_phim)
        textSubtitle = root.findViewById(R.id.text_thong_tin_phim)
        ImageViewfavorite = root.findViewById(R.id.favorite)
        gridView = root.findViewById(R.id.comment_gridview)
        commentList = ArrayList()
        movie = arguments?.getParcelable("movie")

        movie?.let {
            textTitle.text = it.name
            textSubtitle.text = "${it.releaseYear} | ${it.director}"
            checkFav(it.id, this.resources)
        }

        fetchCommentFromFirebase(movie?.id)
        ImageViewfavorite.setOnClickListener { onClickFav(mAuth.currentUser?.uid, movie?.id, this.resources) }

        setupPlayer(root)

        return root
    }

    private fun setupPlayer(view: View) {
        val playerView = view.findViewById<PlayerView>(R.id.player)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val bt_fullscreen = view.findViewById<ImageView>(R.id.bt_fullscreen)

        bt_fullscreen.setOnClickListener {
            if (!isFullScreen) {
                bt_fullscreen.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.baseline_fullscreen_24)
                )
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                bt_fullscreen.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.baseline_fullscreen_exit_24)
                )
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            isFullScreen = !isFullScreen
        }

        simpleExoPlayer = SimpleExoPlayer.Builder(requireContext())
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(5000)
            .build()

        playerView.player = simpleExoPlayer
        playerView.keepScreenOn = true

        simpleExoPlayer.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.visibility = View.VISIBLE
                } else if (playbackState == Player.STATE_READY) {
                    progressBar.visibility = View.GONE
                }
            }
        })

        videoUrl?.let { url ->
            val videoSource = Uri.parse(url)
            val mediaItem = MediaItem.fromUri(videoSource)
            simpleExoPlayer.setMediaItem(mediaItem)
            simpleExoPlayer.prepare()
            restorePosition(simpleExoPlayer)  // Restore video position
            simpleExoPlayer.play()
        } ?: run {
            Toast.makeText(context, "Video URL is missing", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        saveCurrentPosition(simpleExoPlayer)
        simpleExoPlayer.playWhenReady = false
    }

    override fun onStop() {
        super.onStop()
        simpleExoPlayer.release()
    }

    private fun saveCurrentPosition(simpleExoPlayer: SimpleExoPlayer) {
        val playerPosition = simpleExoPlayer.currentPosition
        val sharedPreferences = requireContext().getSharedPreferences("video_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("current_position_${movie?.id}", playerPosition)  // Save with movie ID
        editor.apply()
    }

    private fun restorePosition(simpleExoPlayer: SimpleExoPlayer) {
        val sharedPreferences = requireContext().getSharedPreferences("video_prefs", Context.MODE_PRIVATE)
        val playerPosition = sharedPreferences.getLong("current_position_${movie?.id}", 0)  // Restore with movie ID
        simpleExoPlayer.seekTo(playerPosition)
    }

    private fun onClickFav(userId: String?, movieId: String?, resource: Resources) {
        userId?.let {
            database.child("users").child(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        user?.let {
                            var fav = it.fav
                            movieId?.let { u ->
                                if (!fav.contains(u)) {
                                    ImageViewfavorite.setImageDrawable(resource.getDrawable(R.drawable.favorite_icon))
                                    fav += "$u,"
                                    Toast.makeText(
                                        context,
                                        "Đã thêm vào danh sách phim yêu thích",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    fav = fav.replace("$u,", "")
                                    ImageViewfavorite.setImageDrawable(resource.getDrawable(R.drawable.favorite_watch_icon))
                                    Toast.makeText(
                                        context,
                                        "Đã loại khỏi danh sách phim yêu thích",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            val FavUpdate = mapOf("fav" to fav)
                            database.child("users").child(it.userId).updateChildren(FavUpdate)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    private fun checkFav(movieId: String, resource: Resources) {
        val userId = mAuth.currentUser?.uid

        userId?.let {
            database.child("users").child(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        user?.let {
                            val fav = it.fav
                            if (fav.contains(movieId)) {
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
                        val comment = commentSnapshot.getValue(Comment::class.java)
                        comment?.let {
                            commentList?.add(it)
                        }
                    }
                    updateUIWithComments()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        context,
                        "Failed to fetch comments: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun updateUIWithComments() {
        commentAdapter = commentList?.let { CommentAdapter(this.requireActivity(), it) }
        gridView?.adapter = commentAdapter
    }

    private fun onCommentClick(content: String?, idMovie: String?, name: String?, uid: String?) {
        val id = UUID.randomUUID().toString()
        val comment = Comment(
            id,
            name,
            content,
            uid,
            idMovie,
        )
        database.child("comment").child(id).setValue(comment)
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
