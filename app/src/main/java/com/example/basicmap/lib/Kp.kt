package com.example.basicmap.lib

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.time.temporal.TemporalAmount

data class KpTime(val instant: Instant, val kp: Int)

class Kp {
    companion object {
        var data: List<KpTime> = listOf()
        var last: Instant? = null
    }

    suspend fun getKP(): List<KpTime> {
        val now = Instant.now()
        if (last?.plusSeconds(3600)?.isAfter(now) ?: false) {
            return data
        }
        last = now

        val url = "https://services.swpc.noaa.gov/products/noaa-planetary-k-index-forecast.json"
        val response = try {
            Fuel.get(url).awaitString()
        } catch (e: Exception) {
            return data
        }

        val raw = Gson().fromJson(response, List::class.java) as List<List<String>>

        val utc = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("UTC"))
        // First element is the header
        data = raw.drop(1).map {
            val instant = Instant.from(utc.withZone(ZoneId.of("UTC")).parse(it[0]))
            KpTime(instant, it[1].toInt())
        }

        return data
    }
}