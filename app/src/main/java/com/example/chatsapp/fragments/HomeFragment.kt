package com.example.chatsapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.example.chatsapp.R
import com.example.chatsapp.adapters.chats_adapter
import com.example.chatsapp.databinding.FragmentHomeBinding
import com.example.chatsapp.viewmodel.Viewmodel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private val viewmodel: Viewmodel by activityViewModels()
    private val database =
        FirebaseDatabase.getInstance("https://chatsapp-1010-default-rtdb.firebaseio.com/")
    private val refchats = database.getReference("chats")

    override fun onCreate(savedInstanceState: Bundle?) {
        val content: View = requireActivity().findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (viewmodel.alright()) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        lifecycle.coroutineScope.launch {
            refchats.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    viewmodel.friends_list()
                    viewmodel.nonfriend_searchbar()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
            viewmodel.currentu_uid.observe(viewLifecycleOwner) {
                viewmodel.friends_list()
                viewmodel.nonfriend_searchbar()
            }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "ChatsApp"

        val adapter = viewmodel.currentu_uname.value?.let { it ->
            chats_adapter(it) {
                val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(it)
                this.findNavController().navigate(action)
            }
        }

        binding.Chats.adapter = adapter

        lifecycle.coroutineScope.launch {
            viewmodel.friends_list()
        }

        viewmodel.chat_list.observe(this@HomeFragment.viewLifecycleOwner) {
            val submit = it.toList().sortedByDescending { i -> i.time.toLong() }
            adapter?.submitList(submit)
        }

        binding.apply {
            AddFriend.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_friendsFragment)
            }
            Logout.setOnClickListener {
                Firebase.auth.signOut()
                findNavController().navigate(R.id.action_homeFragment_to_login_Or_SignUp_Fragment)
            }
        }

    }
}
