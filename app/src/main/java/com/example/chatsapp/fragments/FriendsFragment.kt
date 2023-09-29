package com.example.chatsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import com.example.chatsapp.databinding.FragmentFriendsBinding
import com.example.chatsapp.viewmodel.Viewmodel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class FriendsFragment : Fragment() {
    private lateinit var binding: FragmentFriendsBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel: Viewmodel by activityViewModels()
    private val database =
        FirebaseDatabase.getInstance("https://chatsapp-1010-default-rtdb.firebaseio.com/")
    private val ref = database.getReference("users")
    private val refchats = database.getReference("chats")
    private val refchatMessages = database.getReference("chatMessages")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        lifecycle.coroutineScope.launch {
            viewModel.friends_list()
            viewModel.nonfriend_searchbar()
            viewModel.currentu_uid.observe(viewLifecycleOwner) {
                viewModel.friends_list()
                viewModel.nonfriend_searchbar()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Add Friends"
        binding.apply {
            Addfriend.setOnClickListener {
                if (viewModel.users_list.value?.contains(binding.SearchView.query.toString()) == true && !viewModel.friends_list.value?.contains(
                        binding.SearchView.query.toString()
                    )!!
                ) {
                    ref.child(auth.currentUser!!.uid).child("friends").push()
                        .setValue(binding.SearchView.query.toString())
                    refchats.child(
                        name(
                            binding.SearchView.query.toString(),
                            viewModel.currentu_uname.value!!
                        )
                    ).child("members").push().setValue(viewModel.currentu_uname.value)
                    refchats.child(
                        name(
                            binding.SearchView.query.toString(),
                            viewModel.currentu_uname.value!!
                        )
                    ).child("members").push().setValue(binding.SearchView.query.toString())
                    refchats.child(
                        name(
                            binding.SearchView.query.toString(),
                            viewModel.currentu_uname.value!!
                        )
                    ).child("lastmessage").child("message").setValue(
                        "No Messages"
                    )
                    refchats.child(
                        name(
                            binding.SearchView.query.toString(),
                            viewModel.currentu_uname.value!!
                        )
                    ).child("lastmessage").child("sender").setValue(
                        " "
                    )
                    refchats.child(
                        name(
                            binding.SearchView.query.toString(),
                            viewModel.currentu_uname.value!!
                        )
                    ).child("lastmessage").child("time").setValue(
                        "0"
                    )
                    refchatMessages.child(
                        name(
                            binding.SearchView.query.toString(),
                            viewModel.currentu_uname.value!!
                        )
                    ).child("numberOfMessages").setValue(0)
                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (i in snapshot.children) {
                                if (i.child("uname").value.toString() == binding.SearchView.query.toString()) {
                                    ref.child(i.key.toString()).child("friends").push()
                                        .setValue(viewModel.currentu_uname.value)
                                }
                            }
                            viewModel.friends_list()
                            viewModel.nonfriend_searchbar()
                            binding.SearchView.setQuery("", true)
                            val listAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                                this@FriendsFragment.requireContext(),
                                android.R.layout.simple_list_item_1,
                                viewModel.users_list.value!!
                            )
                            binding.idLVFriends.adapter = listAdapter
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                } else {
                    Toast.makeText(requireContext(), "No User Found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.idLVFriends.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                binding.SearchView.setQuery(parent.getItemAtPosition(position).toString(), false)
                binding.SearchView.clearFocus()
            }

        var listAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this@FriendsFragment.requireContext(),
            android.R.layout.simple_list_item_1,
            viewModel.users_list.value!!
        )
        binding.idLVFriends.adapter = listAdapter

        binding.SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (viewModel.users_list.value!!.contains(query)) {
                    listAdapter.filter.filter(query)
                } else {
                    Toast.makeText(
                        this@FriendsFragment.requireContext(),
                        "No User Found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listAdapter.filter.filter(newText)
                return false
            }
        })

        viewModel.users_list.observe(viewLifecycleOwner) {
            listAdapter = ArrayAdapter<String>(
                this@FriendsFragment.requireContext(),
                android.R.layout.simple_list_item_1,
                it
            )
            binding.idLVFriends.adapter = listAdapter
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