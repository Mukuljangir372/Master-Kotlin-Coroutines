package com.mukul.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TestingActivity : AppCompatActivity() {

    private val defaultCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)

        var resultOfThis = Runner<List<Int>>()

        lateinit var continueResult: Continuation<List<Int>>

        findViewById<MaterialButton>(R.id.startBtn).setOnClickListener {


            val job = Job()
            defaultCoroutineScope.launch {
                val suspendCoroutine = suspendCoroutine<List<Int>> { continuation ->
                    continueResult = continuation

                }
            }




            lifecycleScope.launch {




                //THIS CAUSE MAJOR PROBLEM
//                val result = async {
//                    getUsers()
//                }

//                val resultA = resultOfThis.cancelPreviousThenRun{
//                    logThis("inside launcherA")
//                    getUsers()
//                }

//                val resultB = resultOfThis.cancelPreviousThenRun{
//                    logThis("inside launcherB")
//                    getUsers()
//                }

            }

        }
        findViewById<MaterialButton>(R.id.stopBtn).setOnClickListener {
            resultOfThis.getActiveJob()!!.cancel()
//            defaultCoroutineScope.cancel()
        }
        findViewById<MaterialButton>(R.id.cleanUpBtn).setOnClickListener {

            lifecycleScope.launch {
                logThis("launchingB")
//                val resultA = result.cancelPreviousThenRun {
//                    logThis("inside launcherB")
//                    getUsers()
//                }
            }
        }

    }

    private suspend fun getUsers(): List<Int>{
        var list = mutableListOf<Int>()
        for(i in 1..20){
            delay(1000)
            list.add(i)
            logThis("value = $i")
        }
        return list
    }

    private fun logThis(msg: String){
        Log.e("TestingActivity",msg)
    }
}