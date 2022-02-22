package com.mju.csmoa

import org.junit.Test


class RetrofitTest {
//
//    @Test
//    fun networkTest() {
//        val accessToken =
//            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjQsImlhdCI6MTYzNzEzMzgwMCwiZXhwIjoxNjM3MjIwMjAwfQ.g_PPkxLdenaoOvuStbVTMTGXc0HW37q1MSeNlZak27k"
//        val csBrands = mutableListOf<String>("gs25", "cu")
//        val eventTypes = mutableListOf<String>("1%2B1", "2%2B1")
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val response =
//                RetrofitManager.retrofitService?.getRecommendedEventItems(
//                    accessToken = accessToken,
//                    csBrands = csBrands, eventTypes = eventTypes, categories = null
//                )
//        }
//    }

    // 소수 판별하기 -> 이거 짜는 데도 왜 이렇게 못 짜지...
    @Test
    fun primeNumberTest() {
        val number = (1..100).toMutableList()

        number.forEach { num ->
//            println("num = $num")
            if (num == 1) {
                return@forEach
            }

            if (num == 2) {
                println(num)
                return@forEach
            } else if (num % 2 == 0) {
                return@forEach
            }

            for (i: Int in 3 until num step (2)) {
//                println(i)
                if (num % i == 0)
                    return@forEach
            }
            println(num)
//            println(num)

        }
    }

    @Test
    fun exceptionTest() {
        try {
            val temp: Temp? = null
            print(temp!!.alpha)
        } catch (ex: kotlin.Exception) {
            println("kotlin exception: ${ex.printStackTrace()}")
        } catch(ex: java.lang.Exception) {
            println("java exception: ${ex.printStackTrace()}")
        }
    }

    @Test
    fun stringTest() {

        var string = "Hello"
        println(string.hashCode())
        string = "world"
        println(string.hashCode())

        val stringBuild = StringBuffer()
        stringBuild.append("Hello")
        println(stringBuild.hashCode())
        stringBuild.append("World")
        println(stringBuild.hashCode())
        println(stringBuild)
    }

    data class Temp(val alpha: Int)
}