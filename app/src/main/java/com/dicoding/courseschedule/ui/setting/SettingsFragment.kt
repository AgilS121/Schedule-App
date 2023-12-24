package com.dicoding.courseschedule.ui.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.notification.DailyReminder
import com.dicoding.courseschedule.util.NightMode

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //TODO 10 : Update theme based on value in ListPreference
        val themeBase = findPreference<ListPreference>(getString(R.string.pref_key_dark))
        themeBase?.setOnPreferenceChangeListener { _, value ->
            val checkValue = value.toString().toUpperCase()
            val chooseMode = NightMode.values().find {
                it.name == checkValue
            } ?: NightMode.OFF
            updateTheme(chooseMode.value)
            true
        }
        //TODO 11 : Schedule and cancel notification in DailyReminder based on SwitchPreference
        val reminder = findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
        reminder?.setOnPreferenceChangeListener { _, value ->
            val isEnabled = value as Boolean
            if (isEnabled) {
                DailyReminder().setDailyReminder(requireContext())
            } else {
                DailyReminder().cancelAlarm(requireContext())
            }
            true
        }
    }

    private fun updateTheme(nightMode: Int): Boolean {
        AppCompatDelegate.setDefaultNightMode(nightMode)
        requireActivity().recreate()
        return true
    }
}