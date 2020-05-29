package com.example.basicmap.ui.places

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.*
import com.example.basicmap.lib.Kp
import com.example.basicmap.lib.KpTime
import com.example.basicmap.lib.Met
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZonedDateTime


class LivePlace(application: Context) {
    val place: MutableLiveData<Place> =
        MutableLiveData()

    val favorite: MutableLiveData<Boolean> =
        MutableLiveData()

    val weather: MediatorLiveData<Met.Kall?> = MediatorLiveData()

    val address: LiveData<String> = Transformations.switchMap(place) {
        liveData {
            if (it.address != "") {
                emit(it.address)
                return@liveData
            }

            val p = it.position
            val geoc = Geocoder(application)
            try {
                // Not sure why this is warning, as things inside the liveData constructor is run
                // in a background thread. Eg. a big delay here doesn't cause block the UI thread.
                val locations = geoc.getFromLocation(p.latitude, p.longitude, 1)
                // The following splits the string in order to remove unnecessary information. getAdressLine
                // returns very full info, for example
                // Frivoldveien 74, 4877, Grimstad, Norway.
                // Country name is a given, and therefore reduntant,
                // since our "marker" is limited to Norway (we use APIs for Norwegian weather only, and data
                // on restricted zones only for Norway
                val closestLocationAddress = locations[0].getAddressLine(0)
                val stringArray = closestLocationAddress?.split(",")?.toTypedArray()
                val addressToBeDisplayed = stringArray?.get(0) + "," + stringArray?.get(1)

                // Then textview is populated with address, postal code and city/place/location name
                emit(addressToBeDisplayed)
            } catch (e: Exception) {
                emit("")
            }
        }
    }

    val day: MutableLiveData<LocalDate> by lazy {
        MutableLiveData(LocalDate.now())
    }

    val astronomicalData: MediatorLiveData<Met.AstronomicalData> = MediatorLiveData()

    val kp: LiveData<List<KpTime>> by lazy {
        liveData {
            emit(Kp().getKP())
        }
    }

    // Update the weather every hour
    val clock: LiveData<Instant> = Transformations.switchMap(place) {
        liveData {
            while (true) {
                delay(3600000)
                emit(Instant.now())
            }
        }
    }

    suspend private fun getSunrise(p: LatLng) {
        withContext(Dispatchers.IO) {
            val astro = Met().receiveAstroData(p, day.value!!)
            withContext(Dispatchers.Main) {
                astronomicalData.value = astro
            }
        }
    }

    suspend private fun getWeather(p: LatLng) {
        withContext(Dispatchers.IO) {
            val w = Met().locationForecast(p)
            withContext(Dispatchers.Main) {
                weather.value = w
            }
        }
    }

    init {
        // Sunset/sunrise needs to be fetched for each day and position
        // We can assume the day is always set
        astronomicalData.addSource(place) {
            GlobalScope.launch {
                getSunrise(it.position)
            }
        }
        astronomicalData.addSource(day) {
            val place = place.value
            if (place == null)
                return@addSource
            GlobalScope.launch {
                getSunrise(place.position)
            }
        }

        weather.addSource(place) {
            if (it == null)
                return@addSource
            GlobalScope.launch {
                getWeather(it.position)
            }
        }

        weather.addSource(clock) {
            val place = place.value
            Log.d("clock", "foo")
            if (place == null)
                return@addSource
            GlobalScope.launch {
                getWeather(place.position)
            }
        }

    }
}