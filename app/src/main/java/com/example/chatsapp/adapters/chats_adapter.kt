package com.example.chatsapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsapp.DataObject.Chat
import com.example.chatsapp.databinding.ChatItemBinding

class chats_adapter(curu_uname : String ,onclick : (String) -> Unit ) : ListAdapter<Chat, chats_adapter.chatsViewHolder>(DiffCallback) {
    var on = onclick
    var curu_uname = curu_uname
    class chatsViewHolder(private var binding: ChatItemBinding , uname: String) : RecyclerView.ViewHolder(binding.root) {
        var u = uname
        fun bind(item : Chat){
            Log.e("Whats Happening" , item.toString())
            if(item.time == "0"){
                Log.e("brrrrrrrrrrr" , "Bruhhhhhhhhhh")
                binding.name.text = item.name
                binding.LastMessage.text = "No Messages"
            } else if(item.sender == u ){
                binding.name.text = item.name
                binding.LastMessage.text = "You : ${item.lastMessage}"
            }else{
                binding.name.text = item.name
                binding.LastMessage.text = " ${item.sender} : ${item.lastMessage}"
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): chatsViewHolder {
        return chatsViewHolder(ChatItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false)  , curu_uname )
    }

    override fun onBindViewHolder(holder: chatsViewHolder, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
        holder.itemView.setOnClickListener {
            on(getItem(holder.adapterPosition).name)
        }
    }
    companion object DiffCallback : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }

}















