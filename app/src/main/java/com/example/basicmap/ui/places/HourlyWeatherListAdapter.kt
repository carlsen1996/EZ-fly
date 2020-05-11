package com.example.basicmap.ui.places

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.example.basicmap.R
import com.example.basicmap.lib.Met
import com.example.basicmap.lib.degToCompass
import kotlinx.android.synthetic.main.hourly_weather.view.*
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class HourlyWeatherListAdapter(val context: Context, val hours: List<Met.Numb>): RecyclerView.Adapter<HourlyWeatherListAdapter.HourViewHolder>() {
    inner class HourViewHolder(val item: View): RecyclerView.ViewHolder(item) {

        fun setData(data: Met.Numb) {

            val time = LocalTime.from(
                DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC")).parse(data.time)
            )
            item.whatHourItIsTextView.text = time.toString()

            val tempNow =
                data.data.instant.details.air_temperature?.toDouble()?.roundToInt().toString()
            val precipProb = data.data.next_6_hours?.details?.probability_of_precipitation
            val wind = data.data.instant.details.wind_speed
            val windDirection = data.data.instant.details.wind_from_direction
            val compassDeg = windDirection?.toDouble()?.let { degToCompass(it) }
            val windGust = data.data.instant.details.wind_speed_of_gust

            item.tempValue.text = "${tempNow}°C"
            item.windValue.text = "${wind}"
            item.windDesc.text = "m/s ${compassDeg}"
            val customGustString =
                SpannableStringBuilder().append("Vindkast: ").bold { append("$windGust") }
                    .append(" m/s")
            item.windGustValue.text = customGustString
            val weatherIconName = data.data.next_6_hours?.summary?.symbol_code
            val id = context.resources.getIdentifier(weatherIconName, "mipmap", context.packageName)
            item.weatherImageView.setImageResource(id)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hourly_weather, parent, false)
        return HourViewHolder(view)
    }

    override fun getItemCount() = hours.size


    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        val data = hours[position]
        holder.setData(data)
    }
}