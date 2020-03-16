

package com.example.android.trackmysleepquality.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SleepDatabaseDao{
    @Insert
    fun insert(sleepNight: SleepNight)

    @Update
    fun update(sleepNight: SleepNight)

    @Query("select * from sleep_night_table where nightId = :key")
    fun get(key:Long):SleepNight

    @Query("delete from sleep_night_table")
    fun clear()

    @Query("select * from sleep_night_table order by nightId desc")
    fun getAllNights():LiveData<List<SleepNight>>

    @Query("select * from sleep_night_table order by nightId desc limit 1")
    fun getTonight():SleepNight?
}
