package com.example.chatsapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatsapp.DataObject.Chat
import com.example.chatsapp.DataObject.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class Viewmodel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val database =
        FirebaseDatabase.getInstance("https://chatsapp-1010-default-rtdb.firebaseio.com/")
    private val ref = database.getReference("users")
    private val ref_chats = database.getReference("chats")
    private val ref_chatMessages = database.getReference("chatMessages")

    private val currentu_uid_ = MutableLiveData(auth.currentUser?.uid ?: " ")
    val currentu_uid: LiveData<String> = currentu_uid_

    val currentu_uname_ = MutableLiveData("")
    val currentu_uname: LiveData<String> = currentu_uname_

    private val friends_list_: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val friends_list: LiveData<MutableList<String>> = friends_list_

    private val chats_list_: MutableLiveData<MutableList<Chat>> = MutableLiveData(mutableListOf())
    val chats_list: LiveData<MutableList<Chat>> = chats_list_
    val chat_list = MutableLiveData(listOf<Chat>())

    private val users_list_ = MutableLiveData(mutableListOf(""))
    val users_list: LiveData<MutableList<String>> = users_list_

    val message_list_: MutableLiveData<List<Message>> = MutableLiveData(listOf())
    val message_list: LiveData<List<Message>> = message_list_

    private val message_mlist_: MutableLiveData<MutableList<Message>> = MutableLiveData(mutableListOf())
    val message_mlist: LiveData<MutableList<Message>> = message_mlist_

    fun friends_list() {
        viewModelScope.launch {
            ref.child(auth.currentUser!!.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    friends_list_.value!!.clear()

                    for (i in snapshot.child("friends").children) {
                        friends_list_.value?.add(i.value.toString())
                    }
                    currentu_uname_.value = snapshot.child("uname").value.toString()
                    ref_chats.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var flag: Boolean
                            chats_list_.value?.clear()
                            for (i in snapshot.children) {
                                flag = false
                                i.child("members").children.forEach {
                                    if (currentu_uname.value == it.value.toString()) {
                                        flag = true
                                    }
                                }
                                if (flag) {
                                    i.child("members").children.forEach {
                                        if (currentu_uname.value != it.value.toString()) {
                                            chats_list_.value?.add(
                                                Chat(
                                                    it.value.toString(),
                                                    i.child("lastmessage")
                                                        .child("message").value.toString(),
                                                    i.child("lastmessage")
                                                        .child("sender").value.toString(),
                                                    i.child("lastmessage")
                                                        .child("time").value.toString()

                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            chat_list.value = chats_list_.value
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


        }
    }

    fun nonfriend_searchbar() {
        viewModelScope.launch {
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    users_list_.value!!.clear()
                    for (i in snapshot.children) {
                        if ((i.child("uname").value.toString() != currentu_uname_.value) && !(friends_list_.value?.contains(
                                i.child("uname").value.toString()
                            ))!!
                        ) {
                            users_list_.value?.add(i.child("uname").value.toString())

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    fun messageList(friendname: String) {
        viewModelScope.launch {
            ref_chatMessages.child(name(friendname, currentu_uname.value!!))
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        message_mlist_.value?.clear()

                        for (i in snapshot.children) {
                            if (i.key == "numberOfMessages") {
                                continue
                            }

                            message_mlist_.value!!.add(
                                Message(
                                    i.child("message").value.toString(),
                                    i.child("time").value.toString(),
                                    i.child("sender").value.toString(),
                                    i.child("receiver").value.toString()
                                )
                            )
                        }

                        message_list_.value = message_mlist_.value
                        if ((message_mlist.value?.size ?: 0) > 0) {

                            ref_chats.child(name(friendname, currentu_uname.value!!))
                                .child("lastmessage").child("message").setValue(
                                message_mlist.value?.sortedBy { it.time.toLong() }
                                    ?.get(message_mlist.value!!.size - 1)?.message
                            )
                            ref_chats.child(name(friendname, currentu_uname.value!!))
                                .child("lastmessage").child("sender").setValue(
                                message_mlist.value?.sortedBy { it.time.toLong() }
                                    ?.get(message_mlist.value!!.size - 1)?.sender
                            )
                            ref_chats.child(name(friendname, currentu_uname.value!!))
                                .child("lastmessage").child("time").setValue(
                                message_mlist.value?.sortedBy { it.time.toLong() }
                                    ?.get(message_mlist.value!!.size - 1)?.time
                            ).addOnCompleteListener {
                                friends_list()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })


        }
    }

    fun name(s1: String, s2: String): String {
        return if (s1 > s2) {
            s2 + s1
        } else {
            s1 + s2
        }
    }

    fun alright(): Boolean {
        if (chats_list.value.isNullOrEmpty()) {
            return false
        }
        return (chats_list.value?.size ?: false) == (friends_list.value?.size ?: false)
    }


}