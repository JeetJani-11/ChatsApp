package com.example.chatsapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsapp.DataObject.Message
import com.example.chatsapp.databinding.ChatItemBinding
import com.example.chatsapp.databinding.MessageItemBinding
import com.example.chatsapp.viewmodel.Viewmodel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class messages_adapter(var currentUname : String) : ListAdapter< Message, messages_adapter.messagesViewHolder>(DiffCallback){

    class messagesViewHolder(private var binding: MessageItemBinding, var currentUname: String) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : Message){
            Log.e("can we ?", item.sender.toString())
            if(item.message == null){
                return
            }
            val dateFormat = SimpleDateFormat("h:mm a, d MMMM", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(item.time.toLong()))
            if(item.sender ==  currentUname){
                binding.Reciver.visibility = View.INVISIBLE
                binding.Sender.visibility = View.VISIBLE
                binding.SenderMessage.text = item.message
                binding.SenderTimestamp.text = formattedDate

            }else{
                binding.Sender.visibility = View.INVISIBLE
                binding.Reciver.visibility = View.VISIBLE
                binding.ReciverMessage.text = item.message
                binding.ReciverTimestamp.text = formattedDate
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): messages_adapter.messagesViewHolder {
        return messages_adapter.messagesViewHolder(
            MessageItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ) , parent , false
            ) , currentUname
        )
    }

    override fun onBindViewHolder(holder: messagesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView.requestLayout()
            }
        })
    }
    companion object DiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.time == newItem.time
        }
    }
}