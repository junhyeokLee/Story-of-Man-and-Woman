package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.dev_sheep.story_of_man_and_woman.R

class PrefsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences_ui)
        findPreference(getString(R.string.pref_show_values)).onPreferenceClickListener = showValuesListener
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

    private val showValuesListener = Preference.OnPreferenceClickListener { _ ->
        val prefHelper = PreferenceHelper(activity!!)
        AlertDialog.Builder(activity!!)
            .setTitle("환경설정")
            .setMessage(String.format(getString(R.string.app_name),
                prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.setting1),
                prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.setting2),
                prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.setting3),
                prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.setting4),
                prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.setting5)
            ))
            .setPositiveButton("닫기") { _, _ -> }
            .show()
        true
    }
}
