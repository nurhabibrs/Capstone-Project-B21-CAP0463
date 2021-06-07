package com.dicoding.anarki.data.source.local.entity

import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.File

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

//    val akurasi: Double? = null,
//    val message: String = "aha"
)