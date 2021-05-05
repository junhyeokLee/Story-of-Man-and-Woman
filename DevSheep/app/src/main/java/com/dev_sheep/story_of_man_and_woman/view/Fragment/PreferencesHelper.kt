package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.content.Context
import android.preference.PreferenceManager
import com.dev_sheep.story_of_man_and_woman.R

class PreferenceHelper(val context: Context) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    enum class BooleanPref(val prefId: Int, val defaultId: Int) {
        setting1(R.string.pref_boolean1, R.bool.pref_boolean1_default),
        setting2(R.string.pref_boolean2, R.bool.pref_boolean2_default),
        setting3(R.string.pref_boolean3, R.bool.pref_boolean3_default),
        setting4(R.string.pref_boolean4, R.bool.pref_boolean4_default),
        setting5(R.string.pref_boolean5, R.bool.pref_boolean5_default)
    }

    fun getBooleanPref(pref: BooleanPref) =
        prefs.getBoolean(context.getString(pref.prefId), context.resources.getBoolean(pref.defaultId))

    fun setBooleanPref(pref: BooleanPref, value: Boolean) =
        prefs.edit().putBoolean(context.getString(pref.prefId), value).commit()


}