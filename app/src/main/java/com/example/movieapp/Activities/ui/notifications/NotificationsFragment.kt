package com.example.movieapp.Activities.ui.notifications

import UserMangementAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.data.model.User
import com.example.movieapp.databinding.FragmentNotificationsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationsFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserMangementAdapter
    private lateinit var userList: MutableList<User>
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        // Thiết lập Firebase
        database = FirebaseDatabase.getInstance().reference.child("users")

        // Thiết lập RecyclerView và Adapter
        recyclerView = binding.recyclerViewUsers
        recyclerView.layoutManager = LinearLayoutManager(context)
        userList = mutableListOf()
        userAdapter = UserMangementAdapter(userList)
        recyclerView.adapter = userAdapter

        // Tải dữ liệu người dùng
        fetchUsers()
        return root
    }

    private fun fetchUsers() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}