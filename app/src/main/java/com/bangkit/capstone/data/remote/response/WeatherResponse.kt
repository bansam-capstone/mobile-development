package com.bangkit.capstone.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherResponse(

	@field:SerializedName("Response")
	val response: List<WeatherResponseItem>? = null
) : Parcelable

@Parcelize
data class WeatherResponseItem(

	@field:SerializedName("rain")
	val rain: Int? = null,

	@field:SerializedName("visibility")
	val visibility: Int? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("condition_type")
	val conditionType: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("wind_direction")
	val windDirection: Int? = null,

	@field:SerializedName("pressure")
	val pressure: Int? = null,

	@field:SerializedName("cloudiness")
	val cloudiness: Int? = null,

	@field:SerializedName("snow")
	val snow: Int? = null,

	@field:SerializedName("temperature")
	val temperature: Double? = null,

	@field:SerializedName("humidity")
	val humidity: Int? = null,

	@field:SerializedName("wind_speed")
	val windSpeed: Double? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
) : Parcelable
