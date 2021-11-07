package com.mju.csmoa

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.text.DecimalFormat

class CoroutineTest {

    @ObsoleteCoroutinesApi
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun test() = runBlocking {
        exampleSuspend()
    }

    suspend fun exampleSuspend() {

        println("test start")
        val job3 = CoroutineScope(Dispatchers.IO).async {
            println("job3 start")
            (1..100).sortedByDescending { it }
        }

        val job1 = CoroutineScope(Dispatchers.Main).launch {
            println("job1 start")

            val job3Result = job3.await()
            job3Result.forEach { num -> println(num) }

            println("job1 finished")
        }

        val job2 = CoroutineScope(Dispatchers.Main).launch {
            println("job2 start and finished")
        }

        println("뭔일")
        delay(1500)
    }

    @Test
    fun decimalTest() {

        class Kit {
            val num: Int = 1
        }


        val kit: Kit? = null

        val decimalFormat = DecimalFormat("#,###")
//        val test = decimalFormat.format(kit?.let{  }?.num)
//
//        println(test)
    }
}