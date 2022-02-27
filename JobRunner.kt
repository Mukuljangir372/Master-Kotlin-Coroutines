package com.mukul.myapplication

import kotlinx.coroutines.*

class JobRunner{
    private var defaultBackScope : CoroutineScope? = null
    private var defaultMainScope : CoroutineScope? = null

    init {
        defaultBackScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        defaultMainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }

    fun onBack(block: suspend (CoroutineScope) -> Unit){
        defaultBackScope?.launch {
            block(this)
        }
    }

    fun onMain(block: suspend (CoroutineScope) -> Unit){
        defaultMainScope?.launch {
            block(this)
        }
    }

    fun cleanUP(){
        defaultBackScope?.cancel()
        defaultBackScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    fun hardCleanUp(){
        defaultBackScope?.cancel()
        defaultMainScope?.cancel()
        defaultBackScope = null
        defaultMainScope = null
    }

}