package com.example.chatsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.chatsapp.R
import com.example.chatsapp.databinding.FragmentLoginOrSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class Login_Or_SignUp_Fragment : Fragment() {
    private lateinit var binding: FragmentLoginOrSignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Welcome"
        auth = Firebase.auth
        if (auth.currentUser != null) {
            findNavController().navigate(R.id.action_login_Or_SignUp_Fragment_to_homeFragment)
        }
        binding = FragmentLoginOrSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            Login.setOnClickListener { findNavController().navigate(R.id.action_login_Or_SignUp_Fragment_to_loginFragment) }
            Signup.setOnClickListener { findNavController().navigate(R.id.action_login_Or_SignUp_Fragment_to_signUpFragment) }
        }
    }
}