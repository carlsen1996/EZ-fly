package com.example.basicmap.lib

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class Met() {
    private var weather = mutableListOf<Met>()


    fun netCall(lat: Double, long: Double) {
        val baseUrl =  "https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/1.9/.json?lat="
        val fullUrl = baseUrl.plus(lat).plus("&lon=").plus(long)


        GlobalScope.launch {
            val gson = Gson()
            val response = Fuel.get(fullUrl).awaitString()
            Log.d("hei", fullUrl)
            weather = gson.fromJson(response, Array<Met>::class.java).toMutableList()
        }
    }
}