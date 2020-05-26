package com.example.basicmap.lib

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponse
import com.github.kittinunf.result.Result
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.delay
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class Met {

    //weather data:
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
    data class Data(val instant: Instant, val next_1_hours: Next1hours?, val next_6_hours: Next6hours?)
    data class Numb(val time: String, val data: Data)
    data class Meta(val updated_at: String, val units: Details)
    data class Properties(val meta: Meta, val timeseries: List<Numb>)
    data class Kall(val properties: Properties)

    //astronomical data: (formatting-"template" available here: https://api.met.no/weatherapi/sunrise/2.0/.json?lat=40.7127&lon=-74.0059&date=2020-04-23&offset=-05:00#
    data class Sunrise(val desc: String?, val time: String?)
    data class Sunset(val time: String?)
    data class Solarnoon(val time: String?, val elevation: String?)
    data class Solarmidnight(val time: String?, val elevation: String?)
    data class Moonphase(val time: String?, val value: String?)
    data class Moonshadow(val time: String?, val elevation: String?, val azimuth: String?)
    data class Moonposition(val azimuth: String?, val range: String?, val time: String?, val desc: String?, val elevation: String?, val phase: String?)
    data class Moonrise(val time: String?)
    data class Moonset(val time: String?)
    data class High_moon(val time: String?, val elevation: String?)
    data class Low_moon(val time: String?, val elevation: String?)

    data class location(val height: String?,
                        val time: List<time>,
                        val latitude: String?,
                        val longitude: String?)

    data class time(val sunrise: Sunrise?,
                    val moonposition: Moonposition?,
                    val date: String?,
                    val solarmidnight: Solarmidnight?,
                    val moonset: Moonset?,
                    val low_moon: Low_moon?,
                    val high_moon: High_moon?,
                    val solarnoon: Solarnoon?,
                    val moonrise: Moonrise?,
                    val moonphase: Moonphase?,
                    val sunset: Sunset?,
                    val moonshadow: Moonshadow?)

    data class AstroMeta(val licenseurl: String?)

    data class AstronomicalData(val location: location, val meta: AstroMeta)

    companion object {
        private var throttle = false
        private var queued = 0
    }



    suspend fun locationForecast(p: LatLng): Kall? {
        queued++
        if (throttle || queued > 19) {
            delay(1000)
            throttle = false
        }

        val baseUrl = "https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/2.0/.json"
        val fullUrl = "${baseUrl}?lat=${p.latitude}&lon=${p.longitude}"


        val gson = Gson()
        val query = Fuel.get(fullUrl)
        val (_, response, result) = try {
            // We always get Failure when using `responseString()` here for some reason.
            query.awaitStringResponse()
        } catch (e: Exception) {
            queued--
            return null
        }
        queued--
        if (response.statusCode == 429) {
            throttle = true
            return Met().locationForecast(p)
        }
        val res = result
        return gson.fromJson(res, Kall::class.java)
    }

    suspend fun receiveAstroData(p: LatLng, date: LocalDate): AstronomicalData? {
        queued++
        if (throttle || queued > 10) {
            delay(2000)
            throttle = false
        }

        /* meterologisk institutts instruksjoner for bruk av sunset/sunrise api:
        Parameters
        The following parameters are supported:

        lat (latitude), in decimal degrees, mandatory
        lon (longtitude), in decimal degrees, mandatory
        height altitude above ellipsoide, in km (default 0)
        date, given as YYYY-MM-DD, mandatory
        offset, timezone offset, on the format +HH:MM or -HH:MM mandatory
        days, number of days forward to include (default 1, max 15)

        Example URLs
        https://api.met.no/weatherapi/sunrise/2.0/.json?lat=40.7127&lon=-74.0059&date=2020-04-22&offset=-05:00 (New York, as JSON)

        Offset means how you adjust for timezone, +01:00 for blindern, oslo, norway


        ER MULIG JEG MÅ LAGE "MOTTAKERVARIABLE" FOR ALLE DATA sunrise-APIet ønsker å returnere. Dette vil jeg teste
        */

        val sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDate = sdf.format(date)

        //poenget med de 5 kodesnuttene over, er å gjøre formatet på datoen "spiselig" for meterologisk institutts API: YYYY-MM-DD


        //https://api.met.no/weatherapi/sunrise/2.0/.json?lat=40.7127&lon=-74.0059&date=2020-04-22&offset=-05:00 (New York, as JSON)
        val baseSunsetUrl = "https://api.met.no/weatherapi/sunrise/2.0/.json"
        val fullSunsetUrl = "${baseSunsetUrl}?lat=${p.latitude}&lon=${p.longitude}&date=${currentDate}&offset=+01:00"
        //sett inn sunset her; få igang et kall fra browser
        val gson = Gson()
        val (_, response, result) = Fuel.get(fullSunsetUrl).responseString()
        queued--
        when (result) {
            is Result.Failure -> {
                if (response.statusCode == 429) {
                    throttle = true
                    return Met().receiveAstroData(p, date)
                }
                return null
            }
            is Result.Success -> {
                val res = result.get()
//                Log.d("result sunset", res)
                return gson.fromJson(res, AstronomicalData::class.java)
            }
        }
    }
}
