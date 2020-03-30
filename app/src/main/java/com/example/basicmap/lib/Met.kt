package com.example.basicmap.lib

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson



class Met {
    data class WindDirection(val deg: String, val id: String, val name: String)
    data class DewPointTemperature(val unit: String, val id: String, val value: String)
    data class Humidity(val unit: String, val value: String)
    data class Temperature(val unit: String, val value: String, val id: String)
    data class HighClouds(val percent: String, val id: String)
    data class Cloudiness(val id: String, val percent: String)
    data class Pressure(val unit: String, val value: String, val id: String)
    data class LowClouds(val percent: String, val id: String)
    data class Fog(val percent: String, val id: String)
    data class WindGust(val mps: String, val id: String)
    data class MediumClouds(val id: String, val percent: String)
    data class WindSpeed(val mps: String, val id: String, val name: String, val beaufort: String)
    data class Symbol(val number: String, val id: String)
    data class Precipitation(val unit: String, val maxvalue: String, val value: String, val minvalue: String)
    data class TemperatureProbability(val value: String, val unit: String)
    data class WindProbability(val unit: String, val value: String)

    data class Location(val windDirection: WindDirection?,
                        val dewPointTemperature: DewPointTemperature?,
                        val humidity: Humidity?,
                        val temperature: Temperature?,
                        val highClouds: HighClouds?,
                        val cloudiness: Cloudiness?,
                        val pressure: Pressure?,
                        val lowClouds: LowClouds?,
                        val fog: Fog?,
                        val windGust: WindGust?,
                        val mediumClouds: MediumClouds?,
                        val windSpeed: WindSpeed?,
                        val symbol: Symbol?,
                        val precipitation: Precipitation?,
                        val temperatureProbability: TemperatureProbability?,
                        val windProbability: WindProbability?,
                        val latitude: String,
                        val longitude: String,
                        val altitude: String)
    data class Numb(val datatype: String, val to: String, val from: String, val location: Location)
    data class Product(val clas: String, val time: List<Numb>)
    data class Model(val to: String, val termin: String, val name: String, val runended: String, val from: String, val nextrun: String)
    data class Meta(val model: Model)
    data class Kall(val product: Product, val created: String, val meta: Meta)




    suspend fun locationForcast(p: LatLng): Kall {
        val baseUrl =  "https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/1.9/.json"
        val fullUrl = "${baseUrl}?lat=${p.latitude}&lon=${p.longitude}"

        val gson = Gson()
        val response = Fuel.get(fullUrl).awaitString()
        //Log.d("Url", fullUrl)
        val weather = gson.fromJson(response, Kall::class.java)
        //Log.d("temp verdi", weather.product.time[0].location.temperature?.value) //test som henter nåværende temp
        //Log.d("regn verdi", weather.product.time[1].location.precipitation?.value)//test som henter nåværende regn
        return weather
    }
}