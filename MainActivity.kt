package com.mukul.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ActionMode
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import kotlinx.coroutines.*

/**
 * ZERO TO HERO IN KOTLIN COROUTINES
 * EVERYTHING IN ONE PLACE ABOUT COROUTINES
 *
 * What is coroutine?
 * Coroutine involves jobs that run on main thread that suspends the UI.
 *
 * What is CoroutineScope?
 * CoroutineScope keep track of coroutines that is created by launch or async
 *
 *
 * livecycleScope.launch{ } OR viewModelScope.launch { }
 * doesn't mean running any block of code in background thread. It only suspend the suspend functions inside any coroutine job.
 *
 * If you want to run any block of code in background thread, Use Dispatchers.IO in CoroutineScope()
 * OR for main thread, Use Dispatchers.Main
 *
 * ////////////////////////////////////////////////////////////////////////////////////////////////
 *
 * SAFETY BEFORE COROUTINE USE ->
 * 1. Don't launch a new coroutine everywhere
 * 2. While launching coroutine in activity or viewModel, always use lifecycleScope or viewModelScope
 * 3. Cancel OR join previous jobs before running new jobs
 * 4. Cancel all jobs if you are launching them not in lifecycleScope or viewModelScope, Do it before activity destroy or viewModel cleared
 *
 *
 *
 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /**
         * lifecycleAware coroutine scopes
         */
        lifecycleScope.launch {
            //launching a block of code on main thread that suspends UI
        }
        lifecycleScope.launchWhenStarted {
            //WHY?
            //sometimes we have to launch a coroutine when activity in started state and cancel it when activity stopped
            //Better to use when collecting flows
        }

        /**
         * WithContext()
         * It will launch a job on back thread (on on MainThread)
         * If you want to wait for results, then use it
         *
         */

        lifecycleScope.launch {
            //Dispatchers is a pool of threads
            val apiResult = withContext(Dispatchers.IO){
                //apiInterface.getUsers()
            }
            //handle result here
        }

        /**
         * Parallel Calls
         * Running tasks in parallel
         */

        lifecycleScope.launch {
            //launching two parallel jobs inside coroutineScope
            launch {
                //apiInterface.getUsers()
            }
            launch {
                //apiInterface.getChatList()
            }

        }

        /**
         * Running Tasks that are parallel and wait for results
         */
        lifecycleScope.launch {
            //jobA and jobB will run in parallel
            val jobA = async {

            }
            val jobB = async {

            }

            awaitAll(jobA,jobB)
            //jobA and jobB has finished here
            //jobA or JobB has results from suspend functions inside that async blocks


            /**
             * can you replace above code with this -
             */

            val jobs = listOf(
                async {  },
                async {  }
            )
            jobs.awaitAll()

        }

        /**
         * Running Tasks that are parallel and wait for finish
         */

        lifecycleScope.launch {
            //jobA and joB will run in parallel
            val jobA = launch {

            }
            val jobB = launch {

            }
            joinAll(jobA,jobB)
            //jobA and jobB has finished here
            //jobA OR joB don't have any results like above example

            /**
             * You can replace above code with this
             */
            val jobs = listOf(
                launch {  },
                launch {  }
            )
            jobs.joinAll()

        }


        /**
         * If you want to wait for job to finish and cancel it after finish
         */

        lifecycleScope.launch {
            val jobA = launch {

            }
            //This will cancel the job after completing the work
            jobA.cancelAndJoin()
        }


        /**
         * Go to Secrets.kt for second level
         */


    }

    /**
     * (MainViewModel.kt)
     *
     * REPLACEMENT OF
     * val _users = MutableLiveData<String>()
     * val users : LiveData<String> get() = _users
     *
     * fun getUsers() : LiveData<String> {
     *    viewModelScope.launch{
     *        val result = withContext(Dispatchers.IO){
     *             //repo.getUsers()
     *        }
     *
     *        _users.value = result
     *    }
     * }
     * ----------------------------------------------------------
     *
     * REPLACE ABOVE CODE WITH THIS -
     *
     * val users = liveData {
     *   val result = repo.getUsers()  //getUsers() is a suspend function
     *   emit(result)
     * }
     *
     */

    //Example of above explanation
    val users = liveData {
        emit("Mukul")
    }


    /**
     * PROBLEM ->
     * While working on long running tasks, you have to launch a coroutine to run that specific task
     * But what if you are not with lifecycle aware scopes or you don't have a queue of jobs that are
     * created or launched. You end up with memory leaks, data leaks, security issues and even unnecessary
     * network calls.
     *
     * SOLUTION ->
     * CoroutineScope keep tracks of all jobs that created or launched inside that scope.
     * Use only one scope in one specific area (it can be a class or object), and cancel all jobs
     * or empty that scope when you finished with your tasks. or when you don't need them anymore.
     */

    //EXAMPLE
    class LocationHelper{

        //Job + Dispatcher combined and form CoroutineContext that controls the behaviour of coroutines

        //for launching jobs on main thread
        private val scopeForMain = CoroutineScope(Job() + Dispatchers.Main)
        //OR
        //private val scopeForMain = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        //for launching jobs on back thread
        private val scopeForBack = CoroutineScope(Job() + Dispatchers.IO)
        //OR
        //private val scopeForBack = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        fun getRoutes() {
            scopeForBack.launch {
             //api.getRoutes()
            }
        }

        //call this func to cancel all jobs
        fun cleanUp(){
            //Cancel all coroutines works of that scope
            scopeForMain.cancel()
        }

        /**
         * READ THIS BEFORE USE ABOVE CODE
         *
         * val jobA = scopeForMain.launch { }
         * val jobB = scopeForMain.launch { }
         *
         * If jobA throws exception, then jobB will automatically cancelled.
         * As we are using Job while creating coroutine scope.
         * -> private val scopeForMain = CoroutineScope(Job() + Dispatchers.MAIN)
         *
         * If you don't want that jobB will not cancel automatically. Then use
         * -> private val scopeForMain = CoroutineScope(SupervisorJob() + Dispatchers.MAIN)
         *
         *
         *
         *
         */

    }





}
















