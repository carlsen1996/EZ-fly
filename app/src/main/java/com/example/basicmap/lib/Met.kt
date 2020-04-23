package com.example.basicmap.lib

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*


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
    data class Data(val instant: Instant, val next_1_hours: Next1hours, val next_6_hours: Next6hours)
    data class Numb(val time: String, val data: Data)
    data class Meta(val updated_at: String, val units: Details)
    data class Properties(val meta: Meta, val timeseries: List<Numb>)
    data class Kall(val properties: Properties)

    //astronomical data: (formatting-"template" available here: https://api.met.no/weatherapi/sunrise/2.0/.json?lat=40.7127&lon=-74.0059&date=2020-04-23&offset=-05:00#
    //ENDRE TIL STOR BOKSTAV PÅ DISSE OGSÅ
    data class Sunrise(val desc: String?, val time: String?)
    data class sunset(val time: String?)
    data class solarnoon(val time: String?, val elevation: String?)
    data class solarmidnight(val time: String?, val elevation: String?)
    data class moonphase(val time: String?, val value: String?)
    data class moonshadow(val time: String?, val elevation: String?, val azimuth: String?)
    data class moonposition(val azimuth: String?, val range: String?, val time: String?, val desc: String?, val elevation: String?, val phase: String?)
    data class moonrise(val time: String?)
    data class moonset(val time: String?)
    data class high_moon(val time: String?, val elevation: String?)
    data class low_moon(val time: String?, val elevation: String?)
    data class polardayend(val time: String?)
    data class polardaystart(val time: String?)
    data class polarnightend(val time: String?)
    data class polarnightstart(val time: String?)

    data class location(val height: String?,
                        val time: List<time>,
                        val latitude: String?,
                        val longitude: String?)

    data class time(val sunrise: Sunrise?,
                    val moonposition: moonposition?,
                    val date: String?,
                    val solarmidnight: solarmidnight?,
                    val moonset: moonset?,
                    val low_moon: low_moon?,
                    val high_moon: high_moon?,
                    val solarnoon: solarnoon?,
                    val moonrise: moonrise?,
                    val moonphase: moonphase?,
                    val sunset: sunset?,
                    val moonshadow: moonshadow?)

    data class AstroMeta(val licenseurl: String?)

    data class AstronomicalData(val location: location, val meta: AstroMeta)


    suspend fun locationForecast(p: LatLng): Kall {
        val baseUrl =  "https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/2.0/.json"
        val fullUrl = "${baseUrl}?lat=${p.latitude}&lon=${p.longitude}"



        val gson = Gson()
        val response = Fuel.get(fullUrl).awaitString()
        Log.d("Url", fullUrl)
        val weather = gson.fromJson(response, Kall::class.java)
        //Log.d("temp verdi", weather.properties.timeseries[0].data.instant.details.air_temperature) //test som henter nåværende temp
        //Log.d("regn verdi", weather.properties.timeseries[0].data.next_1_hours.details.precipitation_amount)//test som henter nåværende regn

        return weather
    }

    suspend fun receiveAstroData(p: LatLng): AstronomicalData {

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


        val sdf = SimpleDateFormat("YYYY-MM-DD")
        val currentDate = sdf.format(Date())

        //poenget med de 5 kodesnuttene over, er å gjøre formatet på datoen "spiselig" for meterologisk institutts API: YYYY-MM-DD


        //https://api.met.no/weatherapi/sunrise/2.0/.json?lat=40.7127&lon=-74.0059&date=2020-04-22&offset=-05:00 (New York, as JSON)
        val baseSunsetUrl = "https://api.met.no/weatherapi/sunrise/2.0/.json"
        val fullSunsetUrl = "${baseSunsetUrl}?lat=${p.latitude}&lon=${p.longitude}&date=${currentDate}&offset=+01:00"
        //sett inn sunset her; få igang et kall fra browser
        val gson = Gson()
        val response = Fuel.get(fullSunsetUrl).awaitString()
        Log.d("Url", fullSunsetUrl + "TESTING ASTRODATA")
        val astronomicalData = gson.fromJson(response, AstronomicalData::class.java)

        return astronomicalData
    }
}
