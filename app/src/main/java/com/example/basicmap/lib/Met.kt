package com.example.basicmap.lib

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson



class Met {
    data class Details(val air_pressure_at_sea_level: String?,
                       val air_temperature: String?,
                       val cloud_area_fraction: String?,
                       val cloud_area_fraction_high: String?,
                       val cloud_area_fraction_low: String?,
                       val cloud_area_fraction_medium: String?,
                       val dew_point_temperature: String?,
                       val fog_area_fraction: String?,
                       val relative_humidity: String?,
                       val ultraviolet_index_clear_sky: String?,
                       val wind_from_direction: String?,
                       val wind_speed: String?,
                       val wind_speed_of_gust: String?,
                       val precipitation_amount: String?,
                       val precipitation_amount_max: String?,
                       val precipitation_amount_min: String?,
                       val probability_of_precipitation: String?,
                       val probability_of_thunder: String?,
                       val air_temperature_max: String?,
                       val air_temperature_min: String?)
    data class Instant(val details: Details)
    data class Summary(val symbol_code: String)
    data class Next1hours(val summary: Summary, val details: Details)
    data class Next6hours(val summary: Summary, val details: Details)
    data class Data(val instant: Instant, val next_1_hours: Next1hours, val next_6_hours: Next6hours)
    data class Numb(val time: String, val data: Data)
    data class Meta(val updated_at: String, val units: Details)
    data class Properties(val meta: Meta, val timeseries: List<Numb>)
    data class Kall(val properties: Properties)


    suspend fun locationForecast(p: LatLng): Kall {
        val baseUrl =  "https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/2.0/.json"
        val fullUrl = "${baseUrl}?lat=${p.latitude}&lon=${p.longitude}"

        val gson = Gson()
        val response = Fuel.get(fullUrl).awaitString()
        val weather = gson.fromJson(response, Kall::class.java)
        //Log.d("temp verdi", weather.properties.timeseries[0].data.instant.details.air_temperature) //test som henter nåværende temp
        //Log.d("regn verdi", weather.properties.timeseries[0].data.next_1_hours.details.precipitation_amount)//test som henter nåværende regn
        return weather
    }
}
