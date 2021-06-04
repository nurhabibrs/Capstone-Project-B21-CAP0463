package com.dicoding.anarki.data.source.local.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.File

@Entity(tableName = "predict_result_table")
data class PredictEntity(
    @PrimaryKey
    @NonNull
    val id: String,
    val file: String,
    val result: String? = null,
//    val akurasi: Double? = null,
//    val message: String = "aha"
)