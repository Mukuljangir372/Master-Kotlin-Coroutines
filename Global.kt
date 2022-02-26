package com.mukul.myapplication

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Real World Example ->
 * Let's say, you are running a long operation or task,
 * and if you don't want, task will cancel outside any activity or fragment.
 * Generally, People use WorkManger to handle with these type of tasks like
 * uploading a file to server.
 * But There is another approach or way that you can run long running operations
 * that limited to your application (only continue to run when you application or app
 * in running state)
 */

class MyApplication: Application() {


    val globalCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun uploadFile(){
        globalCoroutineScope.launch {
            //upload to server
        }
    }

    /**
     * READ THIS BEFORE USE ABOVE CODE
     *
     * val jobA = globalCoroutineScope.launch { }
     * val jobB = globalCoroutineScope.launch { }
     *
     * If jobA throws exception, then jobB will automatically cancelled.
     * As we are using Job while creating coroutine scope.
     * -> private val globalCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
     *
     * If you don't want that jobB will not cancel automatically. Then use
     * -> private val globalCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
     *
     *
     *
     *
     */
}












