package com.dev_sheep.story_of_man_and_woman.view.dialog

import android.app.Dialog
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.view.activity.MystoryActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.SecretStoryActivity
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

        myStory.setOnClickListener {
            val lintent = Intent(context, SelectTagActivity::class.java)
            lintent.putExtra("type",TYPE_PUBLIC)
            startActivity(lintent)
            dismiss()
        }

        scretStory.setOnClickListener {
            val lintent = Intent(context, SelectTagActivity::class.java)
            lintent.putExtra("type",TYPE_PRIVATE)
            startActivity(lintent)
            dismiss()
        }

        dialog?.setContentView(contentView)
    }
}