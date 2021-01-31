package com.dev_sheep.story_of_man_and_woman.view.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.FB_ChatMessage
import com.dev_sheep.story_of_man_and_woman.data.database.entity.FB_User
import com.dev_sheep.story_of_man_and_woman.view.adapter.MessageLatestAdapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_my_messages.*
import kotlinx.android.synthetic.main.adapter_latest_message_row.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.HashMap

class MyMessageActivity  : AppCompatActivity() {

    companion object {
        var currentUser: FB_User? = null
        val TAG = "LatestMessages"
        var readValue = ""
        val adapter = GroupAdapter<ViewHolder>()
        val myId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$myId")

    }

    private val memberViewModel: MemberViewModel by viewModel()
    private val latestMessagesMap = HashMap<String, FB_ChatMessage>()
    private var list : FB_ChatMessage? = null
    private var context:Context ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_messages)
        context = this
        recyclerview_latest_messages.adapter = adapter
//        recyclerview_latest_messages.addItemDecoration(
//            DividerItemDecoration(
//                this,
//                DividerItemDecoration.VERTICAL
//            )
//        )
        iv_back.setOnClickListener {
            onBackPressed()
        }

        listenForLatestMessages()

        fetchCurrentUser()

        verifyUserIsLoggedIn()

    }


    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        // hash sortByDescending 으로 key값이 아닌 value값의 날짜 내림차순 순으로 정렬
        latestMessagesMap.entries.sortedByDescending { it.value.date }.forEach {
//                sortedMap[it.value] = it.value
            var fb_chatmessage = it

            if(fb_chatmessage == null){
                pb_bar.visibility = View.VISIBLE
//                return
            }else {
                pb_bar.visibility = View.GONE

                adapter.add(MessageLatestAdapter(fb_chatmessage.value, memberViewModel, this))

                val itemTouchHelperCallback =
                    object :
                        ItemTouchHelper.SimpleCallback(
                            0,
                            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                        ) {
                        override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                        ): Boolean {

                            return false
                        }

                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            val toId =
                                viewHolder.itemView.tv_chat_id.text.toString() // recyclerview viewholder itme에서 FB_Message toId값 받아오기
                            showDeletePopup(
                                viewHolder.adapterPosition,
                                myId!!,
                                toId
                            ) // popup창에서 해당 아이템 삭제

                        }

                    }

                val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
                itemTouchHelper.attachToRecyclerView(recyclerview_latest_messages)
            }

        }

    }

    private fun listenForLatestMessages() {
//        orderByChild("date").startAt("20210120000000").endAt("20410120000000") 파이어베이스 날짜순으로 정렬하기 ,지금은 해쉬맵을 사용했기때문에 해쉬맵에서 솔팅해줌
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(FB_ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(FB_ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }



    private fun fetchCurrentUser() {

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(FB_User::class.java)
                Log.d("LatestMessages", "Current user ${currentUser?.username}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            Toast.makeText(
                applicationContext,
                "메세지 계정에 가입되지 않았습니다.",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(intent)
        }
    }

    private fun showDeletePopup(
        position: Int,
        fromId: String,
        toId: String
    ){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.alert_popup,null)
        val textView: TextView = view.findViewById(R.id.textView)
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        textView.text = "대화방에서 나가시겠습니까?"

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("대화방 삭제")
            .setPositiveButton("네"){
                dialog,which -> Toast.makeText(applicationContext,"삭제하기",Toast.LENGTH_SHORT).show()
                latestMessageRef.removeValue()
                reference.removeValue()
                adapter.removeGroup(position) // group library 스와이프 아이템 삭제
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("아니요", DialogInterface.OnClickListener { dialog, which ->
                adapter.notifyDataSetChanged()
            })
            .create()


        alertDialog.setView(view)
        alertDialog.show()

        val btn_color : Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        val btn_color_cancel : Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)

        if(btn_color != null){
            btn_color.setTextColor(resources.getColor(R.color.main_Accent))
        }
        if(btn_color_cancel != null){
            btn_color_cancel.setTextColor(resources.getColor(R.color.main_Accent))
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}