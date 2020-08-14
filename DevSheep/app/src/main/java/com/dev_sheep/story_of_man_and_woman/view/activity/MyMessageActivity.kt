package com.dev_sheep.story_of_man_and_woman.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dev_sheep.story_of_man_and_woman.R
import kotlinx.android.synthetic.main.activity_my_messages.*

class MyMessageActivity  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_messages)

        tv_name.setOnClickListener {
            startActivity( Intent(this, MessageActivity::class.java))
        }
    }
}