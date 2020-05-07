package com.example.basicmap.lib

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.core.view.get
import com.example.basicmap.R
import kotlinx.android.synthetic.main.weather.view.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
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

    val idToDay = mapOf(
        R.id.today to now.dayOfWeek,
        R.id.tommorow to now.plusDays(1).dayOfWeek,
        R.id.third to now.plusDays(2).dayOfWeek,
        R.id.fourth to now.plusDays(3).dayOfWeek,
        R.id.fifth to now.plusDays(4).dayOfWeek,
        R.id.sixth to now.plusDays(5).dayOfWeek,
        R.id.seventh to now.plusDays(6).dayOfWeek
    )

    val dayToId = mapOf(
         now.dayOfWeek to R.id.today,
         now.plusDays(1).dayOfWeek to R.id.tommorow,
         now.plusDays(2).dayOfWeek to R.id.third,
         now.plusDays(3).dayOfWeek to R.id.fourth,
         now.plusDays(4).dayOfWeek to R.id.fifth,
         now.plusDays(5).dayOfWeek to R.id.sixth,
         now.plusDays(6).dayOfWeek to R.id.seventh
    )

    dayToId.forEach {
        val day = it.key
        container.findViewById<RadioButton>(it.value).text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }
    container.dayBar.setOnCheckedChangeListener { group, checkedId ->
        if (checkedId < 0)
            return@setOnCheckedChangeListener

        for (data in days[idToDay.get(checkedId)]!!) {
            val time = data.time
            val datetime = LocalDateTime.from(utc.parse(time))

            if (datetime.dayOfWeek == now.dayOfWeek || datetime.hour == 12) {
                Log.d("now", datetime.toString())
                val tempNow = data.data.instant.details.air_temperature?.toDouble()?.roundToInt().toString()
                container.precipitationView.text = "NEDBØR\n${data.data.next_6_hours?.details?.probability_of_precipitation ?: ""}%" //regn eller nedbør riktig her?
                container.visibilityView.text = "TÅKE\n${data.data.instant.details.fog_area_fraction}%"
                container.kpindexView.text = "KP\n3"
                container.tempValue.text = "${tempNow}°C"

                val weatherIconName = data.data.next_6_hours?.summary?.symbol_code
                val id = context.resources.getIdentifier(weatherIconName, "mipmap", context.packageName)
                container.weatherImageView.setImageResource(id)
                break
            }
        }
    }

    container.dayBar.clearCheck()
    container.dayBar.check(dayToId.get(now.dayOfWeek) ?: -1)
}