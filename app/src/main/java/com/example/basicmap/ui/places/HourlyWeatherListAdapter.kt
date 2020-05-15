package com.example.basicmap.ui.places

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.example.basicmap.R
import com.example.basicmap.lib.Met
import com.example.basicmap.lib.degToCompass
import com.example.basicmap.ui.drones.Drone
import com.example.basicmap.ui.drones.DronesViewModel
import kotlinx.android.synthetic.main.hourly_weather.view.*
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class HourlyWeatherListAdapter(val context: Context, val hours: List<Met.Numb>): RecyclerView.Adapter<HourlyWeatherListAdapter.HourViewHolder>() {

    val utc = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC"))
    val hourMins = DateTimeFormatter.ofPattern("HH:mm")

    private var dronesViewModel = DronesViewModel()

    inner class HourViewHolder(val item: View): RecyclerView.ViewHolder(item) {


        fun setData(data: Met.Numb) {

            val time = ZonedDateTime.ofInstant(
                Instant.from(utc.parse(data.time)), ZoneId.systemDefault()
            )
            item.whatHourItIsTextView.text = time.format(hourMins)

            val tempNow =
                data.data.instant.details.air_temperature?.toDouble()?.roundToInt().toString()
            val precipProb = data.data.next_6_hours?.details?.probability_of_precipitation
            val wind = data.data.instant.details.wind_speed
            val windDirection = data.data.instant.details.wind_from_direction
            val compassDeg = windDirection?.toDouble()?.let { degToCompass(it) }
            val windGust = data.data.instant.details.wind_speed_of_gust
            val tempMax = data.data.next_6_hours?.details?.air_temperature_max
            val tempMin = data.data.next_6_hours?.details?.air_temperature_min

            item.tempValue.text = "${tempNow}°C"
            item.windValue.text = "${wind}"
            item.windDesc.text = "m/s ${compassDeg}"
            if (tempMax == null || tempMin == null) {
                item.minTempValue.text = ""
                item.maxTempValue.text = ""
            }
            else {
                item.maxTempValue.text = context.getString(R.string.HWLA_max)+ "${tempMax}"
                item.minTempValue.text = context.getString(R.string.HWLA_min)+ "${tempMin}"
                //item.maxTempValue.text = "Max: ${tempMax}"
                //item.minTempValue.text = "Min: ${tempMin}"
            }



            val customGustString =
                SpannableStringBuilder().append("Vindkast: ").bold { append("$windGust") }
                    .append(" m/s")
            if (windGust == null) {
                item.windGustValue.text = ""
            }
            else {
                item.windGustValue.text = customGustString
            }

            val weatherIconName = data.data.next_1_hours?.summary?.symbol_code
                ?: data.data.next_6_hours?.summary?.symbol_code ?: ""
            val id = context.resources.getIdentifier(weatherIconName, "mipmap", context.packageName)
            item.weatherImageView.setImageResource(id)

            //Set warning colors
            var windThreat = 0
            var gustThreat = 0
            if (wind != null) {
                if(wind.toFloat() < 5.0) {
                    item.windValue.setTextColor(Color.GREEN)
                    item.windDesc.setTextColor(Color.GREEN)
                }
                else if(wind.toFloat() >= 5.0 && wind.toFloat() < 11.0) {
                    windThreat = 1
                    item.windValue.setTextColor(Color.YELLOW)
                    item.windDesc.setTextColor(Color.YELLOW)
                }
                else {
                    windThreat = 2
                    item.windValue.setTextColor(Color.RED)
                    item.windDesc.setTextColor(Color.RED)
                }
            }
            if(windGust != null) {
                if(windGust.toFloat() < 5.0) {
                    item.windGustValue.setTextColor(Color.GREEN)
                }
                else if (windGust.toFloat() >= 5.0 && windGust.toFloat() < 11.0) {
                    gustThreat = 1
                    item.windGustValue.setTextColor(Color.YELLOW)
                }
                else {
                    gustThreat = 2
                    item.windGustValue.setTextColor(Color.RED)
                }
            }
            //Warning message
            item.windContainer.setOnClickListener {
                val warningInfo = AlertDialog.Builder(context)
                var vindTekst = ""
                var gustTekst = ""
                var droneTekst = ""
                warningInfo.setTitle("Fare nivå")
                warningInfo.setPositiveButton("Ok") { _, _ -> }

                when(windThreat) {
                    0 -> vindTekst = "Vindstyrke nivå: GRØNT \n-Trygge vindforhold \n"
                    1 -> vindTekst = "Vindstyrke nivå: GULT \n-Uforutsigbare forhold. Kan være utrygt for enkelte droner \n"
                    2 -> vindTekst = "Vindstyrke nivå: RØDT \n-Ikke anbefalt å fly \n"
                }
                if(gustThreat > 0 && windThreat < 2) {
                    gustTekst = "OBS! Fare for vindkast som kan gi utrygge vindforhold \n"
                }
                
                if(!dronesViewModel.getDroneList().value!!.isEmpty()) {
                    droneTekst = "Egnede droner: \n"
                    for(drone in dronesViewModel.getDroneList().value!!) {
                        if(drone.maksVindStyrke.toFloat() > wind?.toFloat()!!) {
                            droneTekst += " - " + drone.navn + "\n"
                        }
                    }
                }
                if(droneTekst == "Egnede droner: \n") {
                    droneTekst = "Ingen av dine registrerte droner er egnet i disse vindforholdene"
                }

                if(gustTekst == "" && droneTekst == "") {
                    warningInfo.setMessage(vindTekst)
                }
                else if(gustTekst == "") {
                    warningInfo.setMessage(vindTekst + "\n" + droneTekst)
                }
                else if(droneTekst == "") {
                    warningInfo.setMessage(vindTekst + "\n" + gustTekst)
                }
                else {
                    warningInfo.setMessage(vindTekst + "\n" + gustTekst + "\n" + droneTekst)
                }
                warningInfo.show()
            }
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