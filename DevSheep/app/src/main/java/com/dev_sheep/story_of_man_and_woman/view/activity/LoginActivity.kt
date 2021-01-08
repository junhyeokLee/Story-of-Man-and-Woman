package com.dev_sheep.story_of_man_and_woman.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btn_signup
import kotlinx.android.synthetic.main.activity_login.etv_email
import kotlinx.android.synthetic.main.activity_login.etv_password
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {
    val TAG = "LoginActivity"
    val REQUEST_SIGNUP = 0
    val REQUEST_PASSWORD_SEARCH = 1

    private val memberViewModel: MemberViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener {
            loginChecked(
                etv_email.text.toString(),
                etv_password.text.toString()
            )
        }

        btn_signup.setOnClickListener {
            val intent = Intent(getApplicationContext(), SignUpActivity::class.java)
            startActivityForResult(intent, REQUEST_SIGNUP)
            finish()
        }

        tv_search_password.setOnClickListener {
            val intent = Intent(getApplicationContext(), PasswordSearchActivity::class.java)
            startActivityForResult(intent, REQUEST_PASSWORD_SEARCH)
        }
    }

    private fun loginChecked(Email: String,Password: String){


        if(Email != "" && Password != ""){
            memberViewModel.getMemberCheck(
                Email,
                Password,
                this.applicationContext
            )

        }
        else{

            if(Email == "") {
                Toast.makeText(applicationContext, "이메일을 체크해주세요.", Toast.LENGTH_SHORT).show()
                return
            }
            else if(Password == ""){
                Toast.makeText(applicationContext, "패스워드를 체크해주세요.", Toast.LENGTH_SHORT).show()
                return
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == Activity.RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish()
            }
        }
    }

    override fun onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true)
    }


}