package com.dicoding.anarki.data.source.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PredictResponse(
	@field: SerializedName("file")
	@Expose
	val file: String? = null,

	@field: SerializedName("result")
	@Expose
	val result: String? = null,

//	@field: SerializedName("akurasi")
//	@Expose
//	val akurasi: Double? = null,
//
//	val message: String = "aha"
)

