package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.LoginActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.MainActivity


class PreferenceFragment : Fragment(),View.OnClickListener {

    private lateinit var iv_back : ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_preference, null)
        iv_back = view.findViewById(R.id.iv_back)
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.settings, PrefsFragment())
            ?.commit()

        iv_back.setOnClickListener(this)

        return view

    }

    class PrefsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preferences_ui)

//            findPreference(getString(R.string.pref_show_values)).onPreferenceClickListener =
//                showValuesListener

        }


        override fun onPreferenceTreeClick(preference: Preference?): Boolean {
                val key = preference?.key
                if(key.equals("logout")){
                    showLogOutPopup()
                }
//                else if(key.equals("delete")){
//                    Toast.makeText(activity!!, "계정삭제함", Toast.LENGTH_SHORT)
//                        .show()
//                }
            return false
        }


        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            when (key) {
                getString(R.string.pref_string1) -> {
                    val pref = findPreference(key) as EditTextPreference
                    Toast.makeText(activity!!, "String changed to ${pref.text}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        private fun showLogOutPopup(){
            val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.alert_popup, null)
            val textView: TextView = view.findViewById(R.id.textView)

            textView.text = "로그아웃 하시겠습니까?"

            val alertDialog = AlertDialog.Builder(activity!!)
                .setTitle("로그아웃")
                .setPositiveButton("네") { dialog, which ->
                    val editor: SharedPreferences.Editor =  activity?.getSharedPreferences("autoLogin",AppCompatActivity.MODE_PRIVATE)!!.edit()
                    editor.clear()
                    editor.commit()

                    val lintent = Intent(context, LoginActivity::class.java)
                    (context as Activity).startActivity(lintent)
                    (context as Activity).overridePendingTransition(
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out
                    )
                }

                .setNegativeButton("아니요", DialogInterface.OnClickListener { dialog, which ->

                })
                .create()

            // remaining_time - 00:00~~10:00 ,  elapsed - 10:00 ~~ 00:00
            // p1PlayTime = 00:00~ 시작, elapsed

            alertDialog.setView(view)
            alertDialog.show()

            val btn_color : Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            val btn_color_cancel : Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)

            if(btn_color != null){
                btn_color.setTextColor(resources.getColor(R.color.main_Accent))
            }
            if(btn_color_cancel != null){
                btn_color_cancel.setTextColor(resources.getColor(R.color.main_Accent))
            }

        }

        private fun showDeletePopup(){
            val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.alert_popup, null)
            val textView: TextView = view.findViewById(R.id.textView)

            textView.text = "회원탈퇴 하시겠습니까?"

            val alertDialog = AlertDialog.Builder(activity!!)
                .setTitle("회원탈퇴")
                .setPositiveButton("네") { dialog, which ->
//                    Toast.makeText(activity?.applicationContext, "회원탈퇴", Toast.LENGTH_SHORT).show()
                    val editor: SharedPreferences.Editor =  activity?.getSharedPreferences("autoLogin",AppCompatActivity.MODE_PRIVATE)!!.edit()
                    editor.clear()
                    editor.commit()




                    val lintent = Intent(context, LoginActivity::class.java)
                    (context as Activity).startActivity(lintent)
                    (context as Activity).overridePendingTransition(
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out
                    )
                }

                .setNegativeButton("아니요", DialogInterface.OnClickListener { dialog, which ->

                })
                .create()

            // remaining_time - 00:00~~10:00 ,  elapsed - 10:00 ~~ 00:00
            // p1PlayTime = 00:00~ 시작, elapsed

            alertDialog.setView(view)
            alertDialog.show()

            val btn_color : Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            val btn_color_cancel : Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)

            if(btn_color != null){
                btn_color.setTextColor(resources.getColor(R.color.main_Accent))
            }
            if(btn_color_cancel != null){
                btn_color_cancel.setTextColor(resources.getColor(R.color.main_Accent))
            }

        }

        private val showValuesListener = Preference.OnPreferenceClickListener { _ ->
            val prefHelper = PreferenceHelper(activity!!)
            AlertDialog.Builder(activity!!)
                .setTitle("환경설정")
                .setMessage(
                    String.format(
                        getString(R.string.app_name),
                        prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.setting1),
                        prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.setting2),
                        prefHelper.getStringPref(PreferenceHelper.StringPref.setting1),
                        prefHelper.getStringPref(PreferenceHelper.StringPref.setting2),
                        prefHelper.getIntPref(PreferenceHelper.IntPref.setting1),
                        prefHelper.getIntPref(PreferenceHelper.IntPref.setting2)
                    )
                )
                .setPositiveButton("닫기") { _, _ -> }
                .show()
            true
        }


    }



    override fun onClick(v: View?) {

        when(v?.id){
            R.id.iv_back -> {
                activity?.onBackPressed()
            }

        }

    }

}