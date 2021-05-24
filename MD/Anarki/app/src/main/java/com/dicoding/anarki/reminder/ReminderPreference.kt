package com.dicoding.anarki.reminder

import android.content.Context

class ReminderPreference(context: Context?) {
    companion object {
        const val  PREFS_REMINDER = "reminder_pref"
        private const val REMINDER = "sReminder"
    }

    private val preference = context?.getSharedPreferences(PREFS_REMINDER, Context.MODE_PRIVATE)

    fun setReminder(reminderModel: ReminderModel) {
        val editor = preference?.edit()
        editor?.putBoolean(REMINDER, reminderModel.sReminder)
        editor?.apply()
    }

    fun getReminder(): ReminderModel {
        val model = ReminderModel()
        model.sReminder = preference?.getBoolean(REMINDER, false)!!
        return model
    }
}