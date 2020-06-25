package com.dev_sheep.story_of_man_and_woman.view.dialog

import android.app.Dialog
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.view.MySytotyActivity
import com.dev_sheep.story_of_man_and_woman.view.YesOrNoActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WriteDialog : BottomSheetDialogFragment() {

    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = View.inflate(context, R.layout.dialog_write, null)

        val myStory: LinearLayout = contentView.findViewById(R.id.my_story_layout)
        val yesOrNo: LinearLayout = contentView.findViewById(R.id.yes_or_no_layout)

        myStory.setOnClickListener {
            val lintent = Intent(context, MySytotyActivity::class.java)
            startActivity(lintent)
            dismiss()
        }

        yesOrNo.setOnClickListener {
            val lintent = Intent(context, YesOrNoActivity::class.java)
            startActivity(lintent)
            dismiss()
        }

        dialog?.setContentView(contentView)
    }
}