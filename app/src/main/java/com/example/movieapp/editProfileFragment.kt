package com.example.movieapp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.movieapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar


class editProfileFragment : Fragment() {
    private lateinit var avatarImageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var maleRadioButton: RadioButton
    private lateinit var femaleRadioButton: RadioButton
    private lateinit var birthdateEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var saveButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var genderRadioGroup: RadioGroup
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
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

        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        avatarImageView = view.findViewById(R.id.avatarImageView)
        val changeAvatarButton = view.findViewById<Button>(R.id.changeAvatarButton)
        nameEditText = view.findViewById(R.id.nameEditText)
        maleRadioButton = view.findViewById(R.id.maleRadioButton)
        femaleRadioButton = view.findViewById(R.id.femaleRadioButton)
        birthdateEditText = view.findViewById(R.id.birthdateEditText)
        genderRadioGroup = view.findViewById(R.id.genderRadioGroup)
        emailEditText = view.findViewById(R.id.emailEditText)
        saveButton = view.findViewById(R.id.saveChangesButton)
        val backButton = view.findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        changeAvatarButton.setOnClickListener { openImagePicker() }
        saveButton.setOnClickListener { saveChanges() }
        birthdateEditText.setOnClickListener { showDatePickerDialog() }
        loadUserProfile()

        return view
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                birthdateEditText.setText(selectedDate)
            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun openImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            avatarImageView.setImageURI(imageUri)
        }
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid
        userId?.let {
            database.child("users").child(it).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let { u ->
                        nameEditText.setText(u.name)
                        emailEditText.setText(u.email)
                        birthdateEditText.setText(u.birthdate)
                        if (u.gender == "male") {
                            maleRadioButton.isChecked = true
                        } else if (u.gender == "female") {
                            femaleRadioButton.isChecked = true
                        }
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

    private fun saveChanges() {
        val name = nameEditText.text.toString()
        val email = emailEditText.text.toString()
        val birthdate = birthdateEditText.text.toString()
        val gender = when {
            maleRadioButton.isChecked -> "male"
            femaleRadioButton.isChecked -> "female"
            else -> ""
        }

        val userId = auth.currentUser?.uid
        userId?.let {
            val userUpdates = mapOf(
                "name" to name,
                "email" to email,
                "birthdate" to birthdate,
                "gender" to gender
            )

            database.child("users").child(it).updateChildren(userUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (imageUri != null) {
                            uploadAvatar(userId)
                        } else {
                            Toast.makeText(
                                context,
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun uploadAvatar(userId: String) {
        val avatarRef = storage.reference.child("avatars/$userId.jpg")
        imageUri?.let { uri ->
            avatarRef.putFile(uri).addOnSuccessListener {
                avatarRef.downloadUrl.addOnSuccessListener { url ->
                    database.child("users").child(userId).child("avatarUrl")
                        .setValue(url.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Profile updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to update profile",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }

}