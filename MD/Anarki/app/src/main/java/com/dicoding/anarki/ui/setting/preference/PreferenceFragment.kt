package com.dicoding.anarki.ui.setting.preference

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.dicoding.anarki.R
import com.dicoding.anarki.reminder.ReminderModel
import com.dicoding.anarki.reminder.ReminderPreference
import com.dicoding.anarki.reminder.ReminderReceiver

class PreferenceFragment : PreferenceFragmentCompat() {

    private lateinit var languagePreference: Preference
    private lateinit var reminderPreference: SwitchPreference
    private lateinit var reminderReceiver: ReminderReceiver
    private lateinit var reminderModel: ReminderModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        settingLanguage()
        settingReminder()
    }

    private fun settingLanguage() {
        val language = resources.getString(R.string.key_language)
        languagePreference = findPreference<Preference>(language) as Preference
        languagePreference.setOnPreferenceClickListener {

            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
            true
        }
    }

    private fun settingReminder() {
        reminderReceiver = ReminderReceiver()
        val reminder = resources.getString(R.string.notifications_new_message)
        reminderPreference = findPreference<SwitchPreference>(reminder) as SwitchPreference
        val reminderPreferenceClass = ReminderPreference(context)
        reminderPreference.isChecked = reminderPreferenceClass.getReminder().sReminder

        reminderPreference.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                reminderPreference.isChecked = !reminderPreference.isChecked

                if (reminderPreference.isChecked) {
                    saveStateReminder(true)
                    context?.let { reminderReceiver.setReminderRepeater(it, "09:00", "Anarki") }
                } else {
                    saveStateReminder(false)
                    context?.let { reminderReceiver.unSetReminder(it) }
                }
                true
            }
    }

    private fun saveStateReminder(state: Boolean) {
        val reminderPreferenceClass = ReminderPreference(context)
        reminderModel = ReminderModel()
        reminderModel.sReminder = state
        reminderPreferenceClass.setReminder(reminderModel)
    }
}