package com.dev_sheep.story_of_man_and_woman.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import kotlinx.android.synthetic.main.activity_sign_up_start.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpStartActivity : AppCompatActivity(), View.OnClickListener {

    private var selected_Gender : String = "";
    private var selected_Age : String = "";
    private val memberViewModel: MemberViewModel by viewModel()
    lateinit var loginEmail:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.activity_sign_up_start)

        val auto = getSharedPreferences("autoLogin", AppCompatActivity.MODE_PRIVATE)
         loginEmail = auto.getString("inputEmail", null)!!

        checkedListener()
        btn_signup.setOnClickListener(this)

    }
    override fun onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true)
    }


    private fun checkedListener() {
        checked_man.setOnClickListener(this)
        checked_woman.setOnClickListener(this)
        checked_10.setOnClickListener(this)
        checked_20.setOnClickListener(this)
        checked_30.setOnClickListener(this)
        checked_40.setOnClickListener(this)
        checked_50.setOnClickListener(this)
    }

    private fun checkedGenderMan(checkBoxMan: CheckBox, checkBoxWoman: CheckBox) : String{
        if (checkBoxMan.isChecked == true) {
            checkBoxMan.setTextColor(getResources().getColor(R.color.white))
            checkBoxWoman.isChecked = false
            checkBoxWoman.setTextColor(getResources().getColor(R.color.black))
            selected_Gender = "남"
        } else {
            checkBoxMan.setTextColor(getResources().getColor(R.color.black))
            selected_Gender = ""
        }
        return selected_Gender
    }
    private fun checkedGenderWoman(checkBoxWoman: CheckBox, checkBoxMan: CheckBox) : String{
        if (checkBoxWoman.isChecked == true) {
            checkBoxWoman.setTextColor(getResources().getColor(R.color.white))
            checkBoxMan.isChecked = false
            checkBoxMan.setTextColor(getResources().getColor(R.color.black))
            selected_Gender = "여"
        } else {
            checkBoxWoman.setTextColor(getResources().getColor(R.color.black))
            selected_Gender = ""
        }
        return selected_Gender
    }

    private fun signUpChecked(
        Email:String,
        Gender: String,
        Age: String
    ){


        if(Gender != "" && Age != ""){

            memberViewModel.insertMemberProfile(Email,Gender, Age, this.applicationContext)
        }
        else{

             if(Gender == ""){
                Toast.makeText(applicationContext, "성별을 체크해주세요.", Toast.LENGTH_SHORT).show()
                return
            }
            else if(Age == ""){
                Toast.makeText(applicationContext, "나이를 체크해주세요.", Toast.LENGTH_SHORT).show()
                return
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_signup -> {

                signUpChecked(loginEmail, selected_Gender, selected_Age)
            }

            R.id.checked_man -> {
                checkedGenderMan(checked_man, checked_woman)
            }
            R.id.checked_woman -> {
                checkedGenderWoman(checked_woman, checked_man)
            }
            R.id.checked_10 -> if (checked_10.isChecked == true) {
                checked_10.setTextColor(getResources().getColor(R.color.white))
                checked_20.isChecked = false
                checked_20.setTextColor(getResources().getColor(R.color.black))
                checked_30.isChecked = false
                checked_30.setTextColor(getResources().getColor(R.color.black))
                checked_40.isChecked = false
                checked_40.setTextColor(getResources().getColor(R.color.black))
                checked_50.isChecked = false
                checked_50.setTextColor(getResources().getColor(R.color.black))
                selected_Age = checked_10.text.toString()
            } else {
                checked_10.setTextColor(getResources().getColor(R.color.black))
                selected_Age = "";
            }
            R.id.checked_20 -> if (checked_20.isChecked == true) {
                checked_20.setTextColor(getResources().getColor(R.color.white))
                checked_10.isChecked = false
                checked_10.setTextColor(getResources().getColor(R.color.black))
                checked_30.isChecked = false
                checked_30.setTextColor(getResources().getColor(R.color.black))
                checked_40.isChecked = false
                checked_40.setTextColor(getResources().getColor(R.color.black))
                checked_50.isChecked = false
                checked_50.setTextColor(getResources().getColor(R.color.black))
                selected_Age = checked_20.text.toString()
            } else {
                checked_20.setTextColor(getResources().getColor(R.color.black))
                selected_Age = "";
            }
            R.id.checked_30 -> if (checked_30.isChecked == true) {
                checked_30.setTextColor(getResources().getColor(R.color.white))
                checked_10.isChecked = false
                checked_10.setTextColor(getResources().getColor(R.color.black))
                checked_20.isChecked = false
                checked_20.setTextColor(getResources().getColor(R.color.black))
                checked_40.isChecked = false
                checked_40.setTextColor(getResources().getColor(R.color.black))
                checked_50.isChecked = false
                checked_50.setTextColor(getResources().getColor(R.color.black))
                selected_Age = checked_30.text.toString()
            } else {
                checked_30.setTextColor(getResources().getColor(R.color.black))
                selected_Age = "";
            }
            R.id.checked_40 -> if (checked_40.isChecked == true) {
                checked_40.setTextColor(getResources().getColor(R.color.white))
                checked_10.isChecked = false
                checked_10.setTextColor(getResources().getColor(R.color.black))
                checked_20.isChecked = false
                checked_20.setTextColor(getResources().getColor(R.color.black))
                checked_30.isChecked = false
                checked_30.setTextColor(getResources().getColor(R.color.black))
                checked_50.isChecked = false
                checked_50.setTextColor(getResources().getColor(R.color.black))
                selected_Age = checked_40.text.toString()
            } else {
                checked_40.setTextColor(getResources().getColor(R.color.black))
                selected_Age = "";
            }
            R.id.checked_50 -> if (checked_50.isChecked == true) {
                checked_50.setTextColor(getResources().getColor(R.color.white))
                checked_10.isChecked = false
                checked_10.setTextColor(getResources().getColor(R.color.black))
                checked_20.isChecked = false
                checked_20.setTextColor(getResources().getColor(R.color.black))
                checked_30.isChecked = false
                checked_30.setTextColor(getResources().getColor(R.color.black))
                checked_40.isChecked = false
                checked_40.setTextColor(getResources().getColor(R.color.black))
                selected_Age = checked_50.text.toString()
            } else {
                checked_50.setTextColor(getResources().getColor(R.color.black))
                selected_Age = "";
            }
            else -> {
            }
        }
    }


}