package com.feasycom.feasyblue.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.annotation.CheckResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

@CheckResult
@ExperimentalCoroutinesApi
fun SharedPreferences.onPrefChange(): Flow<String> = callbackFlow<String> {
    val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            safeOffer(key)
        }
    registerOnSharedPreferenceChangeListener(listener)
    awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
}.conflate()

private fun Context.preference() =
    PreferenceManager.getDefaultSharedPreferences(this)

fun Context.getBoolean(key: String, default: Boolean = true) =
    preference().getBoolean(key, default)

fun Context.getStr(key: String, default: String = "") =
    preference().getString(key, default) ?: default

fun Context.getInt(key: String, default: Int = 0) =
    preference().getInt(key, default)


fun Context.getLong(key: String, default: Long = 0L) =
    preference().getLong(key, default)

fun Context.putBoolean(key: String, value: Boolean) {
    preference().edit().putBoolean(key, value).apply()
}

fun Context.putStr(key: String, value: String) {
    preference().edit().putString(key, value).apply()
}

fun Context.putInt(key: String, value: Int) {
    preference().edit().putInt(key, value).apply()
}

fun Context.putLong(key: String, value: Long) {
    preference().edit().putLong(key, value).apply()
}
