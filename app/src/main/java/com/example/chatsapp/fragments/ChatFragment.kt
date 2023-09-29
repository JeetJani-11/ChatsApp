package com.example.chatsapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.navArgs
import com.example.chatsapp.adapters.messages_adapter
import com.example.chatsapp.databinding.FragmentChatBinding
import com.example.chatsapp.viewmodel.Viewmodel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class ChatFragment : Fragment() {
    private lateinit var adapter: messages_adapter
    private lateinit var binding: FragmentChatBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel: Viewmodel by activityViewModels()
    private val navArgs: ChatFragmentArgs by navArgs()
    private val database =
        FirebaseDatabase.getInstance("https://chatsapp-1010-default-rtdb.firebaseio.com/")
    private val refchatMessages = database.getReference("chatMessages")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChatBinding.inflate(inflater, container, false)
        lifecycle.coroutineScope.launch {
            refchatMessages.child(name(navArgs.Name, viewModel.currentu_uname.value!!))
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        viewModel.messageList(navArgs.Name)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        adapter.submitList(listOf())
        viewModel.message_list_.value = listOf()
    }

    override fun onResume() {
        super.onResume()
        viewModel.messageList(navArgs.Name)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = messages_adapter(viewModel.currentu_uname.value!!)

        (activity as AppCompatActivity?)!!.supportActionBar!!.title = navArgs.Name

        binding.Messages.adapter = adapter

        lifecycle.coroutineScope.launch {
            viewModel.message_list.observe(this@ChatFragment.viewLifecycleOwner) { it ->
                if (it.isNotEmpty()) {
                    val submit = it.toList().sortedBy { it.time.toLong() }
                    binding.Messages.smoothScrollToPosition(submit.size - 1)
                    adapter.submitList(submit)
                }
            }
        }

        binding.SendMessage.setOnClickListener {
            lifecycle.coroutineScope.launch {
                var nom: Int
                refchatMessages.child(name(navArgs.Name, viewModel.currentu_uname.value!!))
                    .child("numberOfMessages")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            nom = snapshot.value.toString().toInt()
                            nom += 1
                            refchatMessages.child(
                                name(
                                    navArgs.Name,
                                    viewModel.currentu_uname.value!!
                                )
                            ).child(
                                name(
                                    navArgs.Name,
                                    viewModel.currentu_uname.value!!
                                ) + nom.toString()
                            ).child("message")
                                .setValue(binding.MessageInputTextField.text.toString())
                            refchatMessages.child(
                                name(
                                    navArgs.Name,
                                    viewModel.currentu_uname.value!!
                                )
                            ).child(
                                name(
                                    navArgs.Name,
                                    viewModel.currentu_uname.value!!
                                ) + nom.toString()
                            ).child("time").setValue(java.util.Date().time)
                            refchatMessages.child(
                                name(
                                    navArgs.Name,
                                    viewModel.currentu_uname.value!!
                                )
                            ).child(
                                name(
                                    navArgs.Name,
                                    viewModel.currentu_uname.value!!
                                ) + nom.toString()
                            ).child("sender").setValue(viewModel.currentu_uname.value)
                            refchatMessages.child(
                                name(
                                    navArgs.Name,
                                    viewModel.currentu_uname.value!!
                                )
                            ).child(
                                name(
                                    navArgs.Name,
                                    viewModel.currentu_uname.value!!
                                ) + nom.toString()
                            ).child("receiver").setValue(navArgs.Name)
                            refchatMessages.child(
                                name(
                                    navArgs.Name,
                                    viewModel.currentu_uname.value!!
                                )
                            ).child("numberOfMessages").setValue(nom)
                            binding.MessageInputTextField.setText("")
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
            binding.MessageInputTextField.clearFocus()
        }
    }

    fun name(s1: String, s2: String): String {
        return if (s1 > s2) {
            s2 + s1
        } else {
            s1 + s2
        }
    }
}