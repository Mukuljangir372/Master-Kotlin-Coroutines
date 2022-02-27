package com.mukul.myapplication

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicReference

/**
 *
 */
class Runner<T>{

    /**
     * Mutex : A coroutine mutex ensures that only one coroutine run at one time.
     *
     * -> mutex.withLock { }
     * Mutex with lock means -
     * If there is any block is running inside that lock, on one can enter into withLock { },
     * until we return something inside that lock, lock will automatically released when we return
     * something.
     *
     * You can use this for queue your coroutines jobs.
     * Or
     *
     * Problem ->
     * A users clicks on button continuously, that will execute a block that short a large
     * number of list. Then what happens? What coroutine behaves in this case?
     * Coroutine jobs created again and again and launch at same time.
     *
     * Solution ->
     * Wait for previous job to complete before start new job
     *
     */

    private var activeJob: Job? = null

    fun getActiveJob() = activeJob

    /**
     * Cancel previous job and then launch a new job
     */

    suspend fun cancelPreviousThenRun(
        coroutineScope: CoroutineScope,
        block: suspend (CoroutineScope) -> T
    ) : T{
        val newJob = coroutineScope.async(start = CoroutineStart.LAZY) {
            block(this)
        }
        activeJob?.cancel()
        activeJob = newJob
        return newJob.await()
    }

    suspend fun cancelPreviousThenRun(block: suspend (CoroutineScope) -> T): T{
        return coroutineScope {
            val newJob = async(start = CoroutineStart.LAZY){
                block(this)
            }
            activeJob?.cancel()
            activeJob = newJob
            newJob.await()
        }
    }

    /**
     *
     * Wait for previous job to complete and then start next job
     *
     * val jobA = waitForPreviousThenRun { }
     * val jobB = waitForPreviousThenRun { }
     *
     * jobA has started. jobB will wait for jobA to complete and then jobB will start.
     *
     */

    private var mutex: Mutex = Mutex()

    suspend fun waitForPreviousThenRun(block: suspend () -> T) : T{
        mutex.withLock {
            return block()
        }
    }

    /**
     * val jobA = waitForPreviousThenCancel { }
     *
     * launching jobA again and again will not launch a new job until previous job has finished.
     * This does not queue the jobs.
     */

    private var previousActiveJob: Job? = null

    fun getPreviousActiveJob() = previousActiveJob

    private fun invokeOnCompletion() {
        previousActiveJob?.invokeOnCompletion {
            previousActiveJob = null
        }
    }

    suspend fun waitForPreviousThenCancel(coroutineScope: CoroutineScope,block: suspend () -> T): T{
        val newJob = coroutineScope.async(start = CoroutineStart.LAZY) {
            block()
        }
        if(previousActiveJob==null){
            previousActiveJob = newJob
            invokeOnCompletion()
        }else {
            newJob.cancel()
        }
        return newJob.await()
    }

    suspend fun waitForPreviousThenCancel(block: suspend () -> T): T{
        return coroutineScope {
            val newJob = async(start = CoroutineStart.LAZY) {
                block()
            }
            if(previousActiveJob==null){
                previousActiveJob = newJob
                invokeOnCompletion()
            }else {
                newJob.cancel()
            }
            newJob.await()
        }
    }

    fun cleanUp(){
        activeJob?.cancel()
        activeJob = null

        previousActiveJob?.cancel()
        previousActiveJob = null
    }

}
