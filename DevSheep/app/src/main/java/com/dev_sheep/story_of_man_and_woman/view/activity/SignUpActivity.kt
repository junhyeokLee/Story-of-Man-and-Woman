package com.dev_sheep.story_of_man_and_woman.view.activity

import android.app.Activity
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

        checkedListener()
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
            selected_Gender = checkBoxMan.text.toString()
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
            selected_Gender = checkBoxWoman.text.toString()
        } else {
            checkBoxWoman.setTextColor(getResources().getColor(R.color.black))
            selected_Gender = ""
        }
        return selected_Gender
    }

    private fun signUpChecked(
        Email: String,
        Password: String,
        Nick_name: String,
        Gender: String,
        Age: String
    ){


        if(Email != "" && Password != "" && Nick_name != "" && Gender != "" && Age != ""){

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

            memberViewModel.insertMember(
                Email,
                Password,
                Nick_name,
                Gender,
                Age,
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
            else if(Nick_name == ""){
                Toast.makeText(applicationContext, "닉네임을 체크해주세요.", Toast.LENGTH_SHORT).show()
                return
            }
            else if(Gender == ""){
                Toast.makeText(applicationContext, "성별을 체크해주세요.", Toast.LENGTH_SHORT).show()
                return
            }
            else if(Age == ""){
                Toast.makeText(applicationContext, "나이를 체크해주세요.", Toast.LENGTH_SHORT).show()
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
                    etv_nick_name.text.toString(),
                    selected_Gender, selected_Age
                )



            }
            R.id.tv_login_activity -> {
                val intent = Intent(getApplicationContext(), LoginActivity::class.java)
                startActivityForResult(intent, REQUEST_SIGNUP)
                finish()
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
    val VALID_PASSWOLD_REGEX_ALPHA_NUM: Pattern  = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$"); // 4자리~16자리까지

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