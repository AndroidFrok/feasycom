package com.feasycom.feasyblue.util

import androidx.annotation.RestrictTo
import kotlinx.coroutines.channels.SendChannel

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun <E> SendChannel<E>.safeOffer(value: E): Boolean {
    return runCatching { offer(value) }.getOrDefault(false)
}