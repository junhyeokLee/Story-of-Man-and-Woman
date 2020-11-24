package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alfianyusufabdullah.chatyuk.presentation.chatroom.adapter.HolderMesageFrom
import com.alfianyusufabdullah.chatyuk.presentation.chatroom.adapter.HolderMessageTo
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.FB_ChatMessage

/**
 * Created by jonesrandom on 12/26/17.
 *
 * @site www.androidexample.web.id
 * @github @alfianyusufabdullah
 */

class MessageAdapter(context: Context, private val listFBChat: List<FB_ChatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val FROM = 1
        const val TO = 2
    }

//    val user = ChatPreferences.initPreferences(context).userInfo.nick_name

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FROM -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_from_message, parent, false)
                HolderMesageFrom(v)
            }

            TO -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_to_message, parent, false)
                HolderMessageTo(v)
            }

            else -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_from_message, parent, false)
                HolderMesageFrom(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
//            is HolderMesageFrom -> holder.bindChatContent(listChat[position])
//            is HolderMessageTo -> holder.bindChatContent(listChat[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
//        return if (listChat[position].user == user) TO else FROM
        return 1
    }

    override fun getItemCount(): Int {
        return listFBChat.size
    }
}
