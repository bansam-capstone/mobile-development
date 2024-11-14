package com.bangkit.capstone.data.remote.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class LocationResponse(

	@field:SerializedName("rain")
	val rain: Double? = null,

	@field:SerializedName("risk_level")
	val riskLevel: String? = null,

	@field:SerializedName("condition_type")
	val conditionType: String? = null,

	@field:SerializedName("temperature")
	val temperature: Double? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("humidity")
	val humidity: Double? = null,

	@field:SerializedName("location")
	val location: String? = null,

	@field:SerializedName("wind_direction")
	val windDirection: Int? = null,

	@field:SerializedName("wind_speed")
	val windSpeed: Double? = null,

	@field:SerializedName("cloudiness")
	val cloudiness: Int? = null,

	@field:SerializedName("pressure")
	val pressure: Double? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
) : Parcelable
