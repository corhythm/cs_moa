package com.mju.csmoa.common.util

// 문자열이 JSON 형태인지
fun String?.isJsonObject(): Boolean =
    this?.startsWith("{") == true && this.endsWith("}")

// 문자열이 Json Array 형태인지
fun String?.isJsonArray(): Boolean =
    this?.startsWith("[") == true && this.endsWith("]")