package com.example.basicmap.ui.places

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.example.basicmap.lib.Met

class LivePlace(application: Context) {
    val place: MutableLiveData<Place> =
        MutableLiveData()

    val favorite: MutableLiveData<Boolean> =
        MutableLiveData()

    val weather: LiveData<Met.Kall?> = Transformations.switchMap(place) {
        liveData {
            val w = Met().locationForecast(it.position)
            emit(w)
        }
    }

    val address: LiveData<String> = Transformations.switchMap(place) {
        liveData {
            if (it.address != "") {
                emit(it.address)
                return@liveData
            }

            val p = it.position
            val geoc = Geocoder(application)
            try {
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

    val astronomicalData: LiveData<Met.AstronomicalData?> = Transformations.switchMap(place) {
        liveData {
            val astro =
                Met().receiveAstroData(it.position)
            emit(astro)
        }
    }
}