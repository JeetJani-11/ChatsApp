package com.example.chatsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.chatsapp.R
import com.example.chatsapp.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth
    private val database =
        FirebaseDatabase.getInstance("https://chatsapp-1010-default-rtdb.firebaseio.com/")
    private val ref = database.getReference("users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Sign Up"
        binding.CreateAccount.setOnClickListener {
            if ((binding.UsernameEdittext.text != null) && (binding.EmailEdittext.text != null) && (binding.PasswordEdittext.text != null) && (binding.CPasswordEdittext.text != null)) {
                if (binding.PasswordEdittext.text.toString() == binding.CPasswordEdittext.text.toString()) {
                    auth.createUserWithEmailAndPassword(
                        binding.EmailEdittext.text.toString(),
                        binding.PasswordEdittext.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val user = auth.currentUser
                            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                                var flag = true
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (u in snapshot.children) {
                                        if (u.child("uname").value.toString() == binding.UsernameEdittext.text.toString()) {
                                            flag = false
                                            break
                                        }
                                    }
                                    if (flag) {
                                        ref.child(user!!.uid).child("uname")
                                            .setValue(binding.UsernameEdittext.text.toString())
                                        findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Username already exist.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }
                            })

                        } else {
                            Toast.makeText(
                                requireContext(),
                                it.exception?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } else {
                    Toast.makeText(requireContext(), "Password does not match.", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireContext(), "All fields must be filled.", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        binding.LoginRedirect.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }
}