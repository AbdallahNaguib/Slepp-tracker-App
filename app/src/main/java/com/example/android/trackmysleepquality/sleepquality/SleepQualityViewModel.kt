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

package com.example.android.trackmysleepquality.sleepquality

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.*

class SleepQualityViewModel(
        val database: SleepDatabaseDao,
        val Id:Long,
        application: Application) : AndroidViewModel(application){
    private var viewModelJob = Job()


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _navigateUp = MutableLiveData<Boolean>()

    val navigateUp: LiveData<Boolean>
        get()=_navigateUp


    fun onUpdateQuality(quality:Int){
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val night = database.get(Id)
                night.sleepQuality = quality
                database.update(night)
            }
            _navigateUp.value = true
        }
    }

}