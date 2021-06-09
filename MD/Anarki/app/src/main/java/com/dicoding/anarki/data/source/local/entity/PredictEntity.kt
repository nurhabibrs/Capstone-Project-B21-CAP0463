package com.dicoding.anarki.data.source.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "predict_result_table")
data class PredictEntity(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "file")
    val file: String,

    @ColumnInfo(name = "result")
    val result: String? = null,

    @ColumnInfo(name = "image")
    var image: String? = null,
)