package com.tstudioz.fax.fme.util

import android.content.SharedPreferences

enum class SPKey {

    LOGGED_IN,
    FIRST_TIME,
    SHOWN_WEEK,
    LAST_FETCHED,
    KEY,
    EVENTS_GLOW
}

fun SharedPreferences.contains(key: SPKey): Boolean {
    return this.contains(key.name)
}