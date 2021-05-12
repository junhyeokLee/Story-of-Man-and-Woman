package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
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
import androidx.fragment.app.FragmentTransaction
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.view.activity.LoginActivity
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.androidx.viewmodel.ext.android.viewModel


class PreferenceFragment : Fragment(),View.OnClickListener {

    private lateinit var iv_back : ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_preference, null)
        iv_back = view.findViewById(R.id.iv_back)
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.settings, PrefsFragment())?.commit()
        iv_back.setOnClickListener(this)
        return view
    }

    class PrefsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {
        lateinit var my_m_seq : String
        private val memberViewModel: MemberViewModel by viewModel()
        private val feedViewModel: FeedViewModel by viewModel()
        var TOPICK_Subscriber = "subscriber"
        var TOPICK_FeedLike = "feedlike"
        var TOPICK_FeedComment = "feedcomment"
        var TOPICK_FeedReComment = "feedrecomment"
        val firebaseMessaging = FirebaseMessaging.getInstance()


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preferences_ui)
//            findPreference(getString(R.string.pref_show_values)).onPreferenceClickListener =
//                showValuesListener

            // 구독알림
            val pushSubcriberNotification = findPreference("boolean2")
            pushSubcriberNotification.setOnPreferenceChangeListener { preference, newValue ->
                if(newValue == false){
                    firebaseMessaging.unsubscribeFromTopic(TOPICK_Subscriber)
                }else{
                    firebaseMessaging.subscribeToTopic(TOPICK_Subscriber)
                }
                true
            }

            val pushFeedLikeNotification = findPreference("boolean3")
            pushFeedLikeNotification.setOnPreferenceChangeListener { preference, newValue ->
                if(newValue == false){
                    firebaseMessaging.unsubscribeFromTopic(TOPICK_FeedLike)
                }else{
                    firebaseMessaging.subscribeToTopic(TOPICK_FeedLike)
                }
                true
            }

            val pushFeedCommentNotification = findPreference("boolean4")
            pushFeedCommentNotification.setOnPreferenceChangeListener { preference, newValue ->
                if(newValue == false){
                    firebaseMessaging.unsubscribeFromTopic(TOPICK_FeedComment)
                }else{
                    firebaseMessaging.subscribeToTopic(TOPICK_FeedComment)
                }
                true
            }

            val pushFeedReCommentNotification = findPreference("boolean5")
            pushFeedReCommentNotification.setOnPreferenceChangeListener { preference, newValue ->
                if(newValue == false){
                    firebaseMessaging.unsubscribeFromTopic(TOPICK_FeedReComment)
                }else{
                    firebaseMessaging.subscribeToTopic(TOPICK_FeedReComment)
                }
                true
            }


        }
        override fun onPreferenceTreeClick(preference: Preference?): Boolean {

            val preferences: SharedPreferences = context!!.getSharedPreferences("m_seq", Context.MODE_PRIVATE)
                 my_m_seq = preferences.getString("inputMseq", "")

                val key = preference?.key
                if(key.equals("boolean1") == true){
                    firebaseMessaging.subscribeToTopic("notice")
                }else{
                    firebaseMessaging.unsubscribeFromTopic("notice")
                }
                if(key.equals("boolean2") == true){
                    firebaseMessaging.subscribeToTopic("subscriber")
                }else{
                    firebaseMessaging.unsubscribeFromTopic("subscriber")
                }
                if(key.equals("boolean3") == true){
                    firebaseMessaging.subscribeToTopic("feedlike")
                }else{
                    firebaseMessaging.unsubscribeFromTopic("feedlike")
                }
                if(key.equals("boolean4") == true){
                    firebaseMessaging.subscribeToTopic("feedcomment")
                }else{
                    firebaseMessaging.unsubscribeFromTopic("feedcomment")
                }
                if(key.equals("boolean5") == true){
                    firebaseMessaging.subscribeToTopic("feedrecomment")
                }else{
                    firebaseMessaging.unsubscribeFromTopic("feedrecomment")
                }

                if(key.equals("logout")){
                    showLogOutPopup()
                }
                 if(key.equals("delete")){
                    showDeletePopup()
                }
                 if(key.equals("profile")){
                    ProfileCommit(my_m_seq)
                 }
                 if(key.equals("email_supprot")){
//                    Email_support()
                    sendEmail()
                }
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
        }

        private fun showLogOutPopup(){
            val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.alert_popup, null)
            val textView: TextView = view.findViewById(R.id.textView)

            textView.text = "로그아웃 하시겠습니까?"

            val alertDialog = AlertDialog.Builder(activity!!)
                .setTitle("로그아웃")
                .setPositiveButton("네") { dialog, which ->
                    val editor: SharedPreferences.Editor =  activity?.getSharedPreferences(
                        "autoLogin",
                        AppCompatActivity.MODE_PRIVATE
                    )!!.edit()
                    editor.clear()
                    editor.commit()
                    FirebaseAuth.getInstance().signOut()

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
                btn_color.setTextColor(resources.getColor(R.color.main_Accent3))
            }
            if(btn_color_cancel != null){
                btn_color_cancel.setTextColor(resources.getColor(R.color.main_Accent3))
            }

        }

        // 구글Firebase계정과 서버계정을 함께 지워함
        private fun showDeletePopup(){
            val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.alert_popup, null)
            val textView: TextView = view.findViewById(R.id.textView)
            textView.text = "계정을 삭제 하시겠습니까?"
            val alertDialog = AlertDialog.Builder(activity!!)
                .setTitle("계정삭제")
                .setPositiveButton("네") { dialog, which ->
                    val editor: SharedPreferences.Editor =  activity?.getSharedPreferences(
                        "autoLogin",
                        AppCompatActivity.MODE_PRIVATE
                    )!!.edit()
                    editor.clear()
                    editor.commit()
                    memberViewModel.deleteMember(my_m_seq)
                    memberViewModel.deleteFollowMember(my_m_seq)
                    feedViewModel.deleteFeedMember(my_m_seq)
                    feedViewModel.deleteFeedMemberComment(my_m_seq)
                    feedViewModel.deleteFeedMemberBookMark(my_m_seq)
                    feedViewModel.deleteFeedMemberNotification(my_m_seq)
                    FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Toast.makeText(context, "아이디 삭제가 완료되었습니다", Toast.LENGTH_LONG).show()
                            //로그아웃처리
                            FirebaseAuth.getInstance().signOut()
                        }
                    }

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
                btn_color.setTextColor(resources.getColor(R.color.main_Accent3))
            }
            if(btn_color_cancel != null){
                btn_color_cancel.setTextColor(resources.getColor(R.color.main_Accent3))
            }

        }

        private fun ProfileCommit(my_m_seq: String){

            val ProfileFragmentEdit = ProfileFragmentEdit(my_m_seq);
            var ProfileFragmnet = (context as AppCompatActivity).supportFragmentManager
            var fragmentTransaction: FragmentTransaction = ProfileFragmnet.beginTransaction()
            fragmentTransaction.setReorderingAllowed(true)
            fragmentTransaction.setCustomAnimations(
                R.anim.fragment_fade_in,
                R.anim.fragment_fade_out
            )
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.frameLayout, ProfileFragmentEdit);
            fragmentTransaction.commit()

        }

        fun sendEmail() {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto: junhyeoklee616@gmail.com")
//            intent.putExtra(Intent.EXTRA_EMAIL, "junhyeoklee616@gmail.com")
//            intent.putExtra(Intent.EXTRA_SUBJECT, "사연남녀 문의사항 :")
            startActivity(intent)
        }

//        private val showValuesListener = Preference.OnPreferenceClickListener { _ ->
//            val prefHelper = PreferenceHelper(activity!!)
//            AlertDialog.Builder(activity!!)
//                .setTitle("환경설정")
//                .setMessage(
//                    String.format(
//                        getString(R.string.app_name),
//                        prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.setting1),
//                        prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.setting2),
//                        prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.setting3),
//                        prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.setting4),
//                        prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.setting5)
//                    )
//                )
//                .setPositiveButton("닫기") { _, _ -> }
//                .show()
//            true
//        }

    }

    override fun onClick(v: View?) {

        when(v?.id){
            R.id.iv_back -> {
                activity?.onBackPressed()
            }

        }

    }

}