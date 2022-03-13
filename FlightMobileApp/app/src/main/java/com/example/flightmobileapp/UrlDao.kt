package com.example.flightmobileapp

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UrlDao {
    @Query("SELECT * FROM url_table ORDER BY time DESC LIMIT 5")
    fun getAll(): LiveData<List<Url>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(url: Url)

    @Query("DELETE FROM url_table")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteUrl(url: Url)
}