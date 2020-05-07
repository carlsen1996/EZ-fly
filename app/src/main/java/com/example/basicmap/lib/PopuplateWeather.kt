package com.example.basicmap.lib

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import androidx.core.text.bold
import com.example.basicmap.R
import kotlinx.android.synthetic.main.popup.*
import kotlinx.android.synthetic.main.weather.view.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


fun populateWeather(context: Context, container: View, weather: Met.Kall) {
    val utc = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC"))
    val timeseries = weather.properties.timeseries

    val days = mapOf(
        DayOfWeek.MONDAY to mutableListOf<Met.Numb>(),
        DayOfWeek.TUESDAY to mutableListOf<Met.Numb>(),
        DayOfWeek.WEDNESDAY to mutableListOf<Met.Numb>(),
        DayOfWeek.THURSDAY to mutableListOf<Met.Numb>(),
        DayOfWeek.FRIDAY to mutableListOf<Met.Numb>(),
        DayOfWeek.SATURDAY to mutableListOf<Met.Numb>(),
        DayOfWeek.SUNDAY to mutableListOf<Met.Numb>()
    )
    val now = LocalDate.now()
    for (data in timeseries) {
        val time = data.time
        val date = LocalDate.from(utc.parse(time))

        if (date.isAfter(now.plusDays(6)))
            break

        days[date.dayOfWeek]?.add(data)
    }

    val idToDays = mapOf(
        R.id.monday to DayOfWeek.MONDAY,
        R.id.tuesday to DayOfWeek.TUESDAY,
        R.id.wednesday to DayOfWeek.WEDNESDAY,
        R.id.thursday to DayOfWeek.THURSDAY,
        R.id.friday to DayOfWeek.FRIDAY,
        R.id.saturday to DayOfWeek.SATURDAY,
        R.id.sunday to DayOfWeek.SUNDAY
    )
    container.dayBar.setOnCheckedChangeListener { group, checkedId ->
        if (checkedId < 0)
            return@setOnCheckedChangeListener

        for (data in days[idToDays.get(checkedId)]!!) {
            val time = data.time
            val datetime = LocalDateTime.from(utc.parse(time))

            if (datetime.dayOfWeek == now.dayOfWeek || datetime.hour == 12) {
                Log.d("now", datetime.toString())
                val tempNow = data.data.instant.details.air_temperature?.toDouble()?.roundToInt().toString()
                var precipProb = weather.properties.timeseries[0].data.instant.details.probability_of_precipitation
                var wind = weather.properties.timeseries[0].data.instant.details.wind_speed
                var windDirection = weather.properties.timeseries[0].data.instant.details.wind_from_direction
                var compassDeg = windDirection?.toDouble()?.let { degToCompass(it) }
                var windGust = weather.properties.timeseries[0].data.instant.details.wind_speed_of_gust

                container.tempValue.text = "${tempNow}°C"
                container.precipValue.text = "${precipProb}"
                container.windValue.text = "${wind}"
                container.windDesc.text = "m/s ${compassDeg}"
                var customGustString = SpannableStringBuilder().append("Vindkast: ").bold {append("$windGust")}.append(" m/s")
                container.windGustValue.text = customGustString
                val weatherIconName = data.data.next_6_hours?.summary?.symbol_code
                val id = context.resources.getIdentifier(weatherIconName, "mipmap", context.packageName)
                container.weatherImageView.setImageResource(id)
                break
            }
        }
    }

    container.dayBar.clearCheck()
    when(now.dayOfWeek) {
        DayOfWeek.MONDAY -> container.dayBar.check(R.id.monday)
        DayOfWeek.TUESDAY -> container.dayBar.check(R.id.tuesday)
        DayOfWeek.WEDNESDAY -> container.dayBar.check(R.id.wednesday)
        DayOfWeek.THURSDAY -> container.dayBar.check(R.id.thursday)
        DayOfWeek.FRIDAY -> container.dayBar.check(R.id.friday)
        DayOfWeek.SATURDAY -> container.dayBar.check(R.id.saturday)
        DayOfWeek.SUNDAY -> container.dayBar.check(R.id.sunday)
        null -> container.dayBar.clearCheck()
    }

}

private fun degToCompass(a : Double) : String {
    var b =((a/22.5)+.5).toInt()
    var c = arrayOf("N","NNE","NE","ENE","E","ESE", "SE", "SSE","S","SSW","SW","WSW","W","WNW","NW","NNW")
    return c[(b % 16)]
}