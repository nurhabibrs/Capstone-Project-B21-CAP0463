package com.dicoding.anarki.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PredictResponse(
	@field: SerializedName("file")
	@Expose
	val image: String? = null,

	@field: SerializedName("pecandu")
	@Expose
	val pecandu: Boolean? = null,

	@field: SerializedName("akurasi")
	@Expose
	val akurasi: Double? = null,

	val message: String = "aha"
)

