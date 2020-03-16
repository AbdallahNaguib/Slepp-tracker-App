
package com.example.android.trackmysleepquality.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_night_table")
data class SleepNight(
        @PrimaryKey(autoGenerate = true)
        var nightId:Long = 0L,

        val startTimeMilli:Long = System.currentTimeMillis(),

        var endTimeMilli:Long = startTimeMilli,

        var sleepQuality:Int = -1
)