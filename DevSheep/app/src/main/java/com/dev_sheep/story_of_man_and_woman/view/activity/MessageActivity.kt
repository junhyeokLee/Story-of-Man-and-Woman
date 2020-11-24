package com.dev_sheep.story_of_man_and_woman.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.FB_ChatMessage
import com.dev_sheep.story_of_man_and_woman.data.database.entity.FB_User
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileUsersFragment
import com.dev_sheep.story_of_man_and_woman.view.adapter.MessageFromItem
import com.dev_sheep.story_of_man_and_woman.view.adapter.MessageToItem
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_message.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MessageActivity : AppCompatActivity(){

    companion object {
        val TAG = "ChatLog"
        var currentUser: FB_User? = null
    }

    val adapter = GroupAdapter<ViewHolder>()
    lateinit var toUser:FB_User
    //    private var listChat: MutableList<Chat> = ArrayList()
    private val memberViewModel: MemberViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        chat.adapter = adapter

        toUser = intent.getParcelableExtra(ProfileUsersFragment.USER_ID)
        iv_back.setOnClickListener {
            onBackPressed()
        }
        tv_user_nickname.text = toUser.username

        fetchCurrentUser()
        listenForMessages()

        with(chat) {
            hasFixedSize()
            layoutManager = LinearLayoutManager(context).apply { stackFromEnd = true }
        }
        btnSend.setOnClickListener {

            performSendMessage()

        }

//        chatRoomPresenter.attachView(this)
//        chatRoomPresenter.getMessages()

    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")


        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(FB_ChatMessage::class.java)

                val key = p0.key
                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {

                        var readValue = ""
//                        chatMessage.readUsers.put(toId,true)
//여기까지함
//                        var map= mutableMapOf<String,Any>()
//                        map.put(key!!, chatMessage);
//                        map[key+"/readUsers"] = true
//                        FirebaseDatabase.getInstance()
//                            .getReference("/user-messages/$fromId/$toId")
//                            .updateChildren(map)
//                            .addOnCompleteListener {
//                                adapter.notifyDataSetChanged()
//                                chat.scrollToPosition(adapter.itemCount - 1)
//                                readValue = "true"
//                            }



                        val currentUser = currentUser ?: return
                        adapter.add(
                            MessageFromItem(
                                chatMessage.text,
                                currentUser,
                                readValue,
                                memberViewModel
                            )
                        )
                    } else {
                        adapter.add(
                            MessageToItem(
                                chatMessage.text,
                                toUser!!,
                                memberViewModel
                            )
                        )
                    }
                }


                chat.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }

    private fun performSendMessage() {
        // how do we actually send a message to firebase...
        val text = etMessage.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<FB_User>(ProfileUsersFragment.USER_ID) // userID 유저프로필에서 가져오기
        val toId = user.uid

        if (fromId == null) return



//    val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = FB_ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000,false)



        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                etMessage.text.clear()
                chat.scrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(FB_User::class.java)
                Log.d("Messages", "Current user ${currentUser?.username}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId ?: 0 == R.id.filter){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Sign Out")
            builder.setMessage("Apakah kamu ingin keluar?")
            builder.setPositiveButton("YES") { _, _ ->
                val auth = FirebaseAuth.getInstance()
                auth.signOut()

                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            builder.setNegativeButton("NO", null)
            builder.create().show()
        }
        return super.onOptionsItemSelected(item)
    }

}