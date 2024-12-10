package com.bangkit.capstone.util

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.bangkit.capstone.R
import java.text.SimpleDateFormat
import java.util.*

class WeatherWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        private const val PREFS_NAME = "weather_prefs"
        private const val PREF_TEMPERATURE = "temperature"
        private const val PREF_WEATHER_CONDITION = "weather_condition"
        private const val PREF_WEATHER_PREDICTION = "weather_prediction"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val temperatureStr = sharedPreferences.getString(PREF_TEMPERATURE, "N/A")
            val weatherCondition = sharedPreferences.getString(PREF_WEATHER_CONDITION, "N/A")
            val weatherPrediction = sharedPreferences.getString(PREF_WEATHER_PREDICTION, "N/A")

            val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

            val views = RemoteViews(context.packageName, R.layout.weather_widget)

            val temperature = temperatureStr?.toDouble()?.toInt()
            val temperatureText = if (temperature != null) "$temperature°C" else "N/A"

            val predictionText = when (weatherPrediction) {
                "Aman" -> "Tidak ada ancaman banjir."
                "Waspada" -> "Harap memantau lokasi banjir terdekat."
                "Bahaya" -> "Segera pantau lokasi banjir terdekat."
                else -> "Tidak ada informasi"
            }

            views.setTextViewText(R.id.tvTemperature, temperatureText)

            views.setTextViewText(R.id.tvWeatherCondition, descriptionToIndonesian(weatherCondition ?: ""))
            views.setTextViewText(R.id.tvDate, date)
            views.setTextViewText(R.id.tv_prediction, predictionText)

            val weatherIconRes = getWeatherIconRes(weatherCondition ?: "")
            views.setImageViewResource(R.id.iv_weather_icon, weatherIconRes)

            val predictionIconRes = when (weatherPrediction) {
                "Aman" -> R.drawable.ic_prediction_safe
                "Waspada" -> R.drawable.ic_prediction_warning
                "Bahaya" -> R.drawable.ic_prediction_danger
                else -> R.drawable.ic_prediction_safe
            }
            views.setImageViewResource(R.id.iv_prediction, predictionIconRes)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun descriptionToIndonesian(description: String): String {
            return when (description.lowercase(Locale.getDefault())) {
                "clear sky" -> "Cerah"
                "few clouds", "scattered clouds", "broken clouds", "overcast clouds" -> "Berawan"
                "shower rain", "rain", "light rain", "moderate rain", "very heavy rain", "extreme rain" -> "Hujan"
                "thunderstorm", "thunderstorm with light rain", "thunderstorm with heavy rain" -> "Badai Petir"
                else -> "Tidak diketahui"
            }
        }

        private fun getWeatherIconRes(description: String): Int {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val isDaytime = currentHour in 6..18

            return when (description.lowercase(Locale.getDefault())) {
                "clear sky" -> if (isDaytime) R.drawable.ic_weather_day_clearsky else R.drawable.ic_weather_night_clearsky
                "few clouds", "scattered clouds" -> if (isDaytime) R.drawable.ic_weather_day_fewclouds else R.drawable.ic_weather_night_fewclouds
                "broken clouds", "overcast clouds" -> if (isDaytime) R.drawable.ic_weather_day_brokenclouds else R.drawable.ic_weather_night_brokenclouds
                "shower rain", "rain", "light rain", "moderate rain", "very heavy rain", "extreme rain" -> R.drawable.ic_weather_daynight_rain
                "thunderstorm", "thunderstorm with light rain", "thunderstorm with heavy rain" -> R.drawable.ic_weather_daynight_tstorms
                else -> if (isDaytime) R.drawable.ic_weather_day_fewclouds else R.drawable.ic_weather_night_fewclouds
            }
        }
    }
}
