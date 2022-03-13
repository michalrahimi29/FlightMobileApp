package com.example.flightmobileapp

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "url_table")
data class Url(
    @PrimaryKey
    @ColumnInfo(name = "time") @NonNull var time: Long,
    @ColumnInfo(name = "url") val url: String
)
