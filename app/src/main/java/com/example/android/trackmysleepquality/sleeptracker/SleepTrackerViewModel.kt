/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application){

    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
    val navigateToSleepQuality:LiveData<SleepNight>
        get()=_navigateToSleepQuality

    fun doneNavigating(){
        _navigateToSleepQuality.value=null
    }

    private var _viewSnackBar = MutableLiveData<Boolean>()
    val showSnackbar : LiveData<Boolean>
        get() = _viewSnackBar

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val tonight=MutableLiveData<SleepNight?>()

    private val nights=database.getAllNights()

    val nightsString = Transformations.map(nights){nights->
        formatNights(nights,application.resources)
    }

    var onStartEnabled=Transformations.map(tonight){
        it == null
    }
    var onStopEnabled=Transformations.map(tonight){
        it != null
    }
    var onClearedEnabled=Transformations.map(nights){
        it.isNotEmpty()
    }
    init{
        initNight()
    }

    private fun initNight(){
        uiScope.launch {
            tonight.value = getTonight()
        }
    }
    private suspend fun getTonight():SleepNight?{
        return withContext(Dispatchers.IO){
            var night = database.getTonight()
            Log.e("view model",
                    "${night?.startTimeMilli} ${night?.endTimeMilli}")
            if(night?.startTimeMilli != night?.endTimeMilli){
                night = null
            }
            night
        }
    }
    fun onStartTracking(){
        uiScope.launch{
            val night = SleepNight()

            insert(night)

            tonight.value = getTonight()
        }
    }
    private suspend fun insert(night:SleepNight){
        withContext(Dispatchers.IO){
            database.insert(night)
        }
    }

    fun doneShowingSnackBar(){
        _viewSnackBar.value=false
    }

    fun onStopTracking(){
        uiScope.launch {
            // return@launch means to return from the launch method
            // not the lambda expression
            val night = tonight.value ?: return@launch
            night.endTimeMilli = System.currentTimeMillis()
            update(night)
            _navigateToSleepQuality.value = night
        }
    }
    private suspend fun update(sleepNight: SleepNight){
        withContext(Dispatchers.IO){
            database.update(sleepNight)
        }
    }

    fun onClear(){
        uiScope.launch {
            clear()
            _viewSnackBar.value = true
            tonight.value = null
        }
    }
    private suspend fun clear(){
        withContext(Dispatchers.IO){
            database.clear()
        }
    }
}

