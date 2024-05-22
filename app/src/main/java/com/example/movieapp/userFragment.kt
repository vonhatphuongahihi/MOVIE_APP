package com.example.movieapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.movieapp.Activities.Login
import com.example.movieapp.data.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class userFragment : Fragment() {
    private lateinit var avatarImageView: ImageView
    private lateinit var nameText: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        avatarImageView = view.findViewById(R.id.userImageAvatar)
        nameText = view.findViewById(R.id.userNameTextField)

        // Edit Profile Button
        val btnEditProfile = view.findViewById<LinearLayout>(R.id.chinh_sua_thong_tin)
        btnEditProfile.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_userFragment_to_editProfileFragment)
        }

        // Dark Mode Button
        val btnDarkMode = view.findViewById<LinearLayout>(R.id.che_do_toi)
        btnDarkMode.setOnClickListener {
            Snackbar.make(view, "Bật chế độ tối", Snackbar.LENGTH_SHORT).show()
        }

        // Privacy Button
        val btnPrivate = view.findViewById<LinearLayout>(R.id.quyen_rieng_tu)
        btnPrivate.setOnClickListener {
            Snackbar.make(view, "Quyền riêng tư", Snackbar.LENGTH_SHORT).show()
        }

        // Watch History Button
        val btnHistory = view.findViewById<LinearLayout>(R.id.lich_su_xem)
        btnHistory.setOnClickListener {
            Snackbar.make(view, "Lịch sử xem", Snackbar.LENGTH_SHORT).show()
        }

        // Logout Button
        val btnSignOut = view.findViewById<LinearLayout>(R.id.dang_xuat)
        btnSignOut.setOnClickListener { logout() }

        loadUserProfile()
        return view
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid
        userId?.let {
            database.child("users").child(it).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let { u ->
                        nameText.text = u.name

                        if (!u.avatarUrl.isNullOrEmpty()) {
                            Glide.with(requireContext()).load(u.avatarUrl)
                                .into(avatarImageView)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        context,
                        "Failed to load user profile: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(requireContext(), Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}
