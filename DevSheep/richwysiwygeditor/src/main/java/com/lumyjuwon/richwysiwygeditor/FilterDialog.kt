package com.lumyjuwon.richwysiwygeditor

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.fragment.app.DialogFragment


class FilterDialog : DialogFragment() {


    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = View.inflate(context, R.layout.dialog_filter, null)
        dialog?.setContentView(contentView)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        checkedListener()

    }

    fun checkedListener(){


    }


}