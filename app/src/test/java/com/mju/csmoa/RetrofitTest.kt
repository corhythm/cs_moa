package com.mju.csmoa

import com.mju.csmoa.retrofit.RetrofitManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Test

class RetrofitTest {

    @Test
    fun networkTest() {
        val accessToken =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjQsImlhdCI6MTYzNzEzMzgwMCwiZXhwIjoxNjM3MjIwMjAwfQ.g_PPkxLdenaoOvuStbVTMTGXc0HW37q1MSeNlZak27k"
        val csBrands = mutableListOf<String>("gs25", "cu")
        val eventTypes = mutableListOf<String>("1%2B1", "2%2B1")

        CoroutineScope(Dispatchers.IO).launch {
            val response =
                RetrofitManager.retrofitService?.getRecommendedEventItems(
                    accessToken = accessToken,
                    csBrands = csBrands, eventTypes = eventTypes, categories = null
                )
        }

    }
}