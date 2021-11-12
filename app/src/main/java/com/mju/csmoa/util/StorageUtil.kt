package com.mju.csmoa.util

import android.os.Build

inline fun <T> sdk29AndUp(onSdk29: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // 29
        onSdk29()
    } else null
}