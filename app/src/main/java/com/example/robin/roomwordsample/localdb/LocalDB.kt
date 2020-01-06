package com.example.robin.roomwordsample.localdb

import android.content.Context
import android.content.SharedPreferences
import com.example.robin.roomwordsample.Utils.Constants

class LocalDB(context: Context) {

    private var prefs: SharedPreferences =
        context.getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE)


    fun getTask(): String {
        return if (prefs.contains(Constants.task))
            prefs.getString(Constants.task, " ").toString()
        else " "
    }

    fun addTask(value: String) {
        prefs.edit().putString(Constants.task, value).apply()
    }


}