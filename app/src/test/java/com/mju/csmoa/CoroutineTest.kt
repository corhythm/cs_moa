package com.mju.csmoa

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

    class Ipad(val name: String, val version: Int)

    @Test
    fun classBasicMethodTest() {
        val ipadAir = Ipad("pro", 5)
        val ipadPro = Ipad("pro", 5)

        println("ipadAir.hashCode = ${ipadAir.hashCode()}, ipadPro.hashCode = ${ipadPro.hashCode()}")

        println(ipadAir == ipadPro)
    }

    @Test
    fun testJob() = runBlocking {
        val job = Job()
        CoroutineScope(Dispatchers.Default + job).launch {
            CoroutineScope(Dispatchers.Default).launch {
                println("Job one scope start")
                for (index in 0..20) {
                    if (isActive) {
                        println("Job one scope index $index")
                        delay(1)
                    } else {
                        break
                    }
                }
                println("Job one scope for end")
            }
            val jobTwo = launch {
                println("Job two scope for start")
                for (index in 0..10) {
                    if (isActive) {
                        println("Job two scope index $index")
                        delay(1)
                    } else {
                        break
                    }
                }
                println("Job two scope for end")
            }
            jobTwo.join()
        }
        job.cancel()
        delay(30) // 30ms test only.
    }

    @Test
    fun testJob2() = runBlocking {
        val job = Job()
        CoroutineScope(Dispatchers.Default + job).launch {
            CoroutineScope(Dispatchers.Default + job).launch { // 여기에 job을 함께 초기화 한다.
                println("Job one scope start")
                for (index in 0..20) {
                    if (isActive) {
                        println("Job one scope index $index")
                        delay(1)
                    } else {
                        break
                    }
                }
                println("Job one scope for end")
            }
            val jobTwo = launch {
                println("Job two scope for start")
                for (index in 0..10) {
                    if (isActive) {
                        println("Job two scope index $index")
                        delay(1)
                    } else {
                        break
                    }
                }
                println("Job two scope for end")
            }
            jobTwo.join()
        }
        delay(1)
        job.cancel()
    }

    @Test
    fun jobTest(): Unit = runBlocking(Dispatchers.Main) {

        repeat(3) { i -> // launch a few children jobs
            launch {
                delay((i + 1) * 200L) // variable delay 200ms, 400ms, 600ms
                println("Coroutine $i is done")
            }
        }
        println("wow")

    }

    @Test
    fun plusAndMinusTest() {

    }


}