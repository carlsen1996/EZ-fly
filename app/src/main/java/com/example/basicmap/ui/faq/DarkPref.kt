package com.example.basicmap.ui.faq

import android.content.Context

class DarkPref(context : Context) {
    val PREFERENCE_NAME = "DarkPref"
    val PREFERENCE_DARKMODE = "DarkModePreference"
    val preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getDarkPref() : Boolean {
        return preference.getBoolean(PREFERENCE_DARKMODE, false)
    }

    fun setDarkPref(userPref : Boolean) {
        val editor = preference.edit()
        editor.putBoolean(PREFERENCE_DARKMODE,userPref)
        editor.apply()

    }

}