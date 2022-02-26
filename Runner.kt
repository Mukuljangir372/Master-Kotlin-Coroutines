package com.mukul.myapplication

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicReference

/**
 *
 */
class Runner<T>{

    private var activeJob: Job? = null

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

    fun getActiveJob() = activeJob

}
