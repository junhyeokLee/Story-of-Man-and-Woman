package com.dev_sheep.story_of_man_and_woman.view.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.view.activity.MystoryActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.SelectTagActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WriteDialog : BottomSheetDialogFragment() {

    private val TYPE_PUBLIC = "public"
    private val TYPE_SUBSCRIBER = "subscriber"
    private val TYPE_PRIVATE = "private"


    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = View.inflate(context, R.layout.dialog_write, null)

        val myStory: LinearLayout = contentView.findViewById(R.id.my_story_layout)
        val scretStory: LinearLayout = contentView.findViewById(R.id.private_layout)
        val subscribeStory: LinearLayout = contentView.findViewById(R.id.subscribe_layout)

        myStory.setOnClickListener {
            val lintent = Intent(context, SelectTagActivity::class.java)
            lintent.putExtra("type",TYPE_PUBLIC)
            startActivity(lintent)
            (context as Activity).overridePendingTransition(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
            dismiss()
        }

        subscribeStory.setOnClickListener {
            val lintent = Intent(context, SelectTagActivity::class.java)
            lintent.putExtra("type",TYPE_SUBSCRIBER)
            startActivity(lintent)
            (context as Activity).overridePendingTransition(R.anim.fragment_fade_in, R.anim.fragment_fade_out)

            dismiss()
        }

        scretStory.setOnClickListener {
            val lintent = Intent(context, SelectTagActivity::class.java)
            lintent.putExtra("type",TYPE_PRIVATE)
            startActivity(lintent)
            (context as Activity).overridePendingTransition(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
            dismiss()
        }

        dialog?.setContentView(contentView)
    }
}