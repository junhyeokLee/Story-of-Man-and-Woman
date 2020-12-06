package com.dev_sheep.story_of_man_and_woman.view.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileFragmentEdit
import com.google.android.material.button.MaterialButton


class ProfileEditDialog(
    position: Int, value:String,
    private val onClickSaveMemoListener: OnClickMemoSaveListener,
    private val onClickSaveAgeListener: OnClickAgeSaveListener,
    private val onClickSaveGenderListener: OnClickGenderSaveListener
) : DialogFragment() {

    lateinit var checked_10 : CheckBox
    lateinit var checked_20 : CheckBox
    lateinit var checked_30 : CheckBox
    lateinit var checked_40 : CheckBox
    lateinit var checked_50 : CheckBox
    lateinit var checked_man : CheckBox
    lateinit var checked_woman : CheckBox
    lateinit var btn_ok : MaterialButton
    lateinit var et_intro : TextView
    lateinit var layout_intro : LinearLayout
    lateinit var layout_gender : LinearLayout
    lateinit var layout_age : LinearLayout


    private var value = value
    private val position = position

    private var mOnclickSaveMemoListener = onClickSaveMemoListener
    private var mOnclickSaveGenderListener = onClickSaveGenderListener
    private var mOnclickSaveAgeListener = onClickSaveAgeListener

    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = View.inflate(context, R.layout.dialog_edit_profile, null)
        dialog?.setContentView(contentView)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        checked_10 = contentView.findViewById(R.id.checked_10)
        checked_20 = contentView.findViewById(R.id.checked_20)
        checked_30 = contentView.findViewById(R.id.checked_30)
        checked_40 = contentView.findViewById(R.id.checked_40)
        checked_50 = contentView.findViewById(R.id.checked_50)
        checked_man = contentView.findViewById(R.id.checked_man)
        checked_woman = contentView.findViewById(R.id.checked_woman)
        et_intro = contentView.findViewById(R.id.et_intro)
        btn_ok = contentView.findViewById(R.id.btn_ok)
        layout_intro = contentView.findViewById(R.id.layout_intro)
        layout_age = contentView.findViewById(R.id.layout_age)
        layout_gender = contentView.findViewById(R.id.layout_gender)



        initData()
        checkedListener()

    }

    fun initData(){
        if(position == ProfileFragmentEdit.REQUEST_INTRO){
            layout_intro.visibility = View.VISIBLE
            layout_age.visibility = View.GONE
            layout_gender.visibility = View.GONE

            et_intro.text = value

        }else if(position == ProfileFragmentEdit.REQUEST_GENDER){
            layout_intro.visibility = View.GONE
            layout_age.visibility = View.GONE
            layout_gender.visibility = View.VISIBLE

            if(value.equals("남")){
                checked_man.isChecked = true
                checked_woman.isChecked = false
            }else{
                checked_man.isChecked = false
                checked_woman.isChecked = true
            }

        }else if(position == ProfileFragmentEdit.REQUEST_AGE){
            layout_intro.visibility = View.GONE
            layout_age.visibility = View.VISIBLE
            layout_gender.visibility = View.GONE

            if(value.equals("10 대")){
                checked_10.isChecked = true
                checked_20.isChecked = false
                checked_30.isChecked = false
                checked_40.isChecked = false
                checked_50.isChecked = false
            }else if(value.equals("20 대")){
                checked_10.isChecked = false
                checked_20.isChecked = true
                checked_30.isChecked = false
                checked_40.isChecked = false
                checked_50.isChecked = false
            }else if(value.equals("30 대")){
                checked_10.isChecked = false
                checked_20.isChecked = false
                checked_30.isChecked = true
                checked_40.isChecked = false
                checked_50.isChecked = false
            }else if(value.equals("40 대")){
                checked_10.isChecked = false
                checked_20.isChecked = false
                checked_30.isChecked = false
                checked_40.isChecked = true
                checked_50.isChecked = false
            }else if(value.equals("50 대")){
                checked_10.isChecked = false
                checked_20.isChecked = false
                checked_30.isChecked = false
                checked_40.isChecked = false
                checked_50.isChecked = true
            }
        }
    }

    fun checkedListener(){

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

        checked_10.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checked_10.setTextColor(resources.getColor(R.color.white))
                checked_20.isChecked = false
                checked_30.isChecked = false
                checked_40.isChecked = false
                checked_50.isChecked = false
            }else{
                checked_10.setTextColor(resources.getColor(R.color.black))

            }
        }
        checked_20.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checked_20.setTextColor(resources.getColor(R.color.white))
                checked_10.isChecked = false
                checked_30.isChecked = false
                checked_40.isChecked = false
                checked_50.isChecked = false
            }else{
                checked_20.setTextColor(resources.getColor(R.color.black))
            }
        }
        checked_30.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checked_30.setTextColor(resources.getColor(R.color.white))
                checked_10.isChecked = false
                checked_20.isChecked = false
                checked_40.isChecked = false
                checked_50.isChecked = false
            }else{
                checked_30.setTextColor(resources.getColor(R.color.black))
            }
        }
        checked_40.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checked_40.setTextColor(resources.getColor(R.color.white))
                checked_10.isChecked = false
                checked_20.isChecked = false
                checked_30.isChecked = false
                checked_50.isChecked = false
            }else{
                checked_40.setTextColor(resources.getColor(R.color.black))
            }
        }
        checked_50.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checked_50.setTextColor(resources.getColor(R.color.white))
                checked_10.isChecked = false
                checked_20.isChecked = false
                checked_30.isChecked = false
                checked_40.isChecked = false
            }else{
                checked_50.setTextColor(resources.getColor(R.color.black))
            }
        }

        checked_man.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checked_man.setTextColor(resources.getColor(R.color.white))
                checked_woman.isChecked = false

            }else{
                checked_man.setTextColor(resources.getColor(R.color.black))
            }
        }
        checked_woman.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checked_woman.setTextColor(resources.getColor(R.color.white))
                checked_man.isChecked = false

            }else{
                checked_woman.setTextColor(resources.getColor(R.color.black))
            }
        }


        btn_ok.setOnClickListener {

            if(position == ProfileFragmentEdit.REQUEST_INTRO) {
                mOnclickSaveMemoListener.OnClickSaveMemo(et_intro)
            }
            else if(position == ProfileFragmentEdit.REQUEST_AGE){
                if(checked_10.isChecked == true) {
                    mOnclickSaveAgeListener.OnClickSaveAge(checked_10.text.toString())
                }else if(checked_20.isChecked == true){
                    mOnclickSaveAgeListener.OnClickSaveAge(checked_20.text.toString())

                }else if(checked_30.isChecked == true){
                    mOnclickSaveAgeListener.OnClickSaveAge(checked_30.text.toString())

                }else if(checked_40.isChecked == true){
                    mOnclickSaveAgeListener.OnClickSaveAge(checked_40.text.toString())

                }else if(checked_50.isChecked == true){
                    mOnclickSaveAgeListener.OnClickSaveAge(checked_50.text.toString())

                }
            }
            else if(position == ProfileFragmentEdit.REQUEST_GENDER) {
                if (checked_man.isChecked == true) {
                    mOnclickSaveGenderListener.OnClickSaveGender(checked_man.text.toString())
                }else if(checked_woman.isChecked == true){
                    mOnclickSaveGenderListener.OnClickSaveGender(checked_woman.text.toString())
                }
            }

            dismiss()
        }

    }


    interface OnClickMemoSaveListener{
        fun OnClickSaveMemo(tv: TextView)
    }
    interface OnClickGenderSaveListener{
        fun OnClickSaveGender(s: String)
    }
    interface OnClickAgeSaveListener{
        fun OnClickSaveAge(s: String)
    }

}