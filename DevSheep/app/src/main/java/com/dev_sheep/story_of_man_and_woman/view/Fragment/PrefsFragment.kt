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
        setupSeekbars()
    }

    private fun setupSeekbars() {
        (findPreference(getString(R.string.pref_int1)) as SeekBarPreference).apply {
            seekBarIncrement = resources.getInteger(R.integer.pref_int1_step)
            min = resources.getInteger(R.integer.pref_int1_min)
            max = resources.getInteger(R.integer.pref_int1_max)
        }

        (findPreference(getString(R.string.pref_int2)) as SeekBarPreference).apply {
            seekBarIncrement = resources.getInteger(R.integer.pref_int2_step)
            min = resources.getInteger(R.integer.pref_int2_min)
            max = resources.getInteger(R.integer.pref_int2_max)
        }
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
                Toast.makeText(activity!!, "String changed to ${pref.text}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val showValuesListener = Preference.OnPreferenceClickListener { _ ->
        val prefHelper = PreferenceHelper(activity!!)
        AlertDialog.Builder(activity!!)
            .setTitle("환경설")
            .setMessage(String.format(getString(R.string.app_name),
                prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.setting1),
                prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.setting2),
                prefHelper.getStringPref(PreferenceHelper.StringPref.setting1),
                prefHelper.getStringPref(PreferenceHelper.StringPref.setting2),
                prefHelper.getIntPref(PreferenceHelper.IntPref.setting1),
                prefHelper.getIntPref(PreferenceHelper.IntPref.setting2)
            ))
            .setPositiveButton("닫기") { _, _ -> }
            .show()
        true
    }
}
