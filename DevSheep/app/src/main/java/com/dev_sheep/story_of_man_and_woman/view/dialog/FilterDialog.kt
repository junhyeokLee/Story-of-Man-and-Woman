package com.dev_sheep.story_of_man_and_woman.view.dialog

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.fragment.app.DialogFragment
import com.dev_sheep.story_of_man_and_woman.R
import com.google.android.material.button.MaterialButton


class FilterDialog : DialogFragment() {

    lateinit var checked_10 : CheckBox
    lateinit var checked_20 : CheckBox
    lateinit var checked_30 : CheckBox
    lateinit var checked_40 : CheckBox
    lateinit var checked_50 : CheckBox
    lateinit var checked_man : CheckBox
    lateinit var checked_woman : CheckBox
    lateinit var checked_man_woman : CheckBox
    lateinit var btn_ok : MaterialButton

    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = View.inflate(context, R.layout.dialog_filter, null)
        dialog?.setContentView(contentView)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        checked_10 = contentView.findViewById(R.id.checked_10)
        checked_20 = contentView.findViewById(R.id.checked_20)
        checked_30 = contentView.findViewById(R.id.checked_30)
        checked_40 = contentView.findViewById(R.id.checked_40)
        checked_50 = contentView.findViewById(R.id.checked_50)
        checked_man = contentView.findViewById(R.id.checked_man)
        checked_woman = contentView.findViewById(R.id.checked_woman)
        checked_man_woman = contentView.findViewById(R.id.checked_man_woman)
        btn_ok = contentView.findViewById(R.id.btn_ok)


        checkedListener()

    }

    fun checkedListener(){

        checked_10.isChecked = loadCheckBox(checked_10.text.toString())
        checked_20.isChecked = loadCheckBox(checked_20.text.toString())
        checked_30.isChecked = loadCheckBox(checked_30.text.toString())
        checked_40.isChecked = loadCheckBox(checked_40.text.toString())
        checked_50.isChecked = loadCheckBox(checked_50.text.toString())
        checked_man.isChecked = loadCheckBox(checked_man.text.toString())
        checked_woman.isChecked = loadCheckBox(checked_woman.text.toString())
        checked_man_woman.isChecked = loadCheckBox(checked_man_woman.text.toString())


        if(checked_10.isChecked == true){
            checked_10.setTextColor(resources.getColor(R.color.white))
        }
        if(checked_20.isChecked == true){
            checked_20.setTextColor(resources.getColor(R.color.white))
        }
        if(checked_30.isChecked == true){
            checked_30.setTextColor(resources.getColor(R.color.white))
        }
        if(checked_40.isChecked == true){
            checked_40.setTextColor(resources.getColor(R.color.white))
        }
        if(checked_50.isChecked == true){
            checked_50.setTextColor(resources.getColor(R.color.white))
        }

        if(checked_man.isChecked == true){
            checked_man.setTextColor(resources.getColor(R.color.white))
        }
        if(checked_woman.isChecked == true){
            checked_woman.setTextColor(resources.getColor(R.color.white))
        }
        if(checked_man_woman.isChecked == true){
            checked_man_woman.setTextColor(resources.getColor(R.color.white))
        }

        checked_10.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checked_10.setTextColor(resources.getColor(R.color.white))
            }else{
                checked_10.setTextColor(resources.getColor(R.color.black))

            }
        }
        checked_20.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checked_20.setTextColor(resources.getColor(R.color.white))

            }else{
                checked_20.setTextColor(resources.getColor(R.color.black))
            }
        }
        checked_30.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checked_30.setTextColor(resources.getColor(R.color.white))

            }else{
                checked_30.setTextColor(resources.getColor(R.color.black))
            }
        }
        checked_40.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checked_40.setTextColor(resources.getColor(R.color.white))

            }else{
                checked_40.setTextColor(resources.getColor(R.color.black))
            }
        }
        checked_50.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checked_50.setTextColor(resources.getColor(R.color.white))

            }else{
                checked_50.setTextColor(resources.getColor(R.color.black))
            }
        }



        checked_man.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checked_man.setTextColor(resources.getColor(R.color.white))
                checked_woman.isChecked = false
                checked_man_woman.isChecked = false
            }else{
                checked_man.setTextColor(resources.getColor(R.color.black))
            }
        }
        checked_woman.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checked_woman.setTextColor(resources.getColor(R.color.white))
                checked_man.isChecked = false
                checked_man_woman.isChecked = false
            }else{
                checked_woman.setTextColor(resources.getColor(R.color.black))
            }
        }
        checked_man_woman.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checked_man_woman.setTextColor(resources.getColor(R.color.white))
                checked_woman.isChecked = false
                checked_man.isChecked = false
            }else{
                checked_man_woman.setTextColor(resources.getColor(R.color.black))
            }
        }

        btn_ok.setOnClickListener {
            val checked = it


            saveCheckbox(checked_10.isChecked, checked_10.text.toString())
            saveCheckbox(checked_20.isChecked, checked_20.text.toString())
            saveCheckbox(checked_30.isChecked, checked_30.text.toString())
            saveCheckbox(checked_40.isChecked, checked_40.text.toString())
            saveCheckbox(checked_50.isChecked, checked_50.text.toString())

            saveCheckbox(checked_man_woman.isChecked, checked_man_woman.text.toString())
            saveCheckbox(checked_man.isChecked, checked_man.text.toString())
            saveCheckbox(checked_woman.isChecked, checked_woman.text.toString())

            dismiss()
        }

    }


    fun saveCheckbox(isChecked: Boolean, key: String){

        val sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        val editor = sharedpreferences.edit()
        editor.putBoolean(key, isChecked)
        editor.apply()
    }

    fun loadCheckBox(key: String):Boolean{
        val sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedpreferences.getBoolean(key, false)
    }

}