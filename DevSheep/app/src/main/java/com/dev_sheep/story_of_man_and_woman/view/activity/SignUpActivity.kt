package com.dev_sheep.story_of_man_and_woman.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.data.remote.api.MemberService
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "LoginActivity"
    private val REQUEST_SIGNUP = 0
    private val memberViewModel: MemberViewModel by viewModel()
    private var selected_Gender : String = "";
    private var selected_Age : String = "";
    private var loginEmail: String? = null
    private  var loginPassword:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_sign_up)

        val auto = getSharedPreferences("autoLogin", AppCompatActivity.MODE_PRIVATE)
        //처음에는 SharedPreferences에 아무런 정보도 없으므로 값을 저장할 키들을 생성한다.
        // getString의 첫 번째 인자는 저장될 키, 두 번쨰 인자는 값입니다.
        // 첨엔 값이 없으므로 키값은 원하는 것으로 하시고 값을 null을 줍니다.

        var loginEmail = auto.getString("inputEmail", null)
        var loginPassword = auto.getString("inputPassword", null)


        if (loginEmail != null && loginPassword != null) {
            Toast.makeText(this, loginEmail + "님 반갑습니다.", Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email",loginEmail)
            startActivity(intent)
            finish()
        }
        // Profile 설정을 하지않았을때 프로필 설정페이지로 이동
//        else if(loginEmail != null && loginPassword != null && gender == null){
//            Toast.makeText(this, loginEmail + "님 반갑습니다.", Toast.LENGTH_SHORT)
//                .show()
//            val intent = Intent(this, SignUpStartActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

        btn_signup.setOnClickListener(this)
        tv_login_activity.setOnClickListener(this)

    }

    protected override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
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




    private fun signUpChecked(
        Email: String,
        Password: String,
        Password_confirm: String,
        Nick_name: String
    ){


        if(Email != "" && Password != "" && Password_confirm != "" && Nick_name != "" ){

            if(isEmail(Email) == false){
                Toast.makeText(applicationContext, "이메일 형식을 확인해주세요.", Toast.LENGTH_SHORT).show()
                return
            }
            if(validatePassword(Password) == false){
                Toast.makeText(applicationContext, "패스워드 형식을 확인해주세요.", Toast.LENGTH_SHORT).show()
                return
            }
            if(validateNickName(Nick_name) == false){
                Toast.makeText(applicationContext, "닉네임 형식을 확인해주세요.", Toast.LENGTH_SHORT).show()
                return

            }
            if(Password != Password_confirm){
                Toast.makeText(applicationContext, "비밀번호 확인 체크해주세요.", Toast.LENGTH_SHORT).show()
                return
            }

            memberViewModel.insertMember(
                Email,
                Password,
                Nick_name,
                this.applicationContext
            )
        }
        else{

            if(Email == "") {
                Toast.makeText(applicationContext, "이메일을 체크해주세요.", Toast.LENGTH_SHORT).show()
                return
            }
            else if(Password == ""){
                Toast.makeText(applicationContext, "비밀번호를 체크해주세요.", Toast.LENGTH_SHORT).show()
                return
            }
            else if(Password_confirm == ""){
                Toast.makeText(applicationContext, "비밀번호 확인 체크해주세요.", Toast.LENGTH_SHORT).show()
                return
            }
            else if(Password != Password_confirm){
                Toast.makeText(applicationContext, "비밀번호 확인 체크해주세요.", Toast.LENGTH_SHORT).show()
                return
            }
            else if(Nick_name == ""){
                Toast.makeText(applicationContext, "닉네임을 체크해주세요.", Toast.LENGTH_SHORT).show()
                return
            }

        }
    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_signup -> {

                signUpChecked(
                    etv_email.text.toString(),
                    etv_password.text.toString(),
                    etv_password_confirm.text.toString(),
                    etv_nick_name.text.toString()
                )
            }
            R.id.tv_login_activity -> {
                val intent = Intent(getApplicationContext(), LoginActivity::class.java)
                startActivityForResult(intent, REQUEST_SIGNUP)
                finish()
            }

            else -> {
            }
        }
    }

    // 이메일 정규식
    fun isEmail(email: String?): Boolean {
        var returnValue = false
        val regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$"
        val p: Pattern = Pattern.compile(regex)
        val m: Matcher = p.matcher(email)
        if (m.matches()) {
            returnValue = true
        }
        return returnValue
    }

    // 비밀번호 정규식 영어/숫자/특수문자
    val VALID_PASSWOLD_REGEX_ALPHA_NUM: Pattern  = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{7,24}$"); // 7자리~16자리까지

    // 닉네임 정규식 한글/영어/숫자
    val VALID_NICKNAME_REGEX_ALPHA_NUM: Pattern = Pattern.compile("^[가-힣a-zA-Z0-9]{2,24}\$"); // 4자리~24자리

    fun validatePassword(pwStr: String): Boolean{
        var matcher : Matcher = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(pwStr);

        return matcher.matches();

    }

    fun validateNickName(pwStr: String) : Boolean{
        var matcher : Matcher = VALID_NICKNAME_REGEX_ALPHA_NUM.matcher(pwStr);

        return matcher.matches();
    }




}