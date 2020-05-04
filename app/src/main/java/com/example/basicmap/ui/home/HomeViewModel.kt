package com.example.basicmap.ui.home

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.*
import com.example.basicmap.lib.Met
import com.example.basicmap.ui.places.Place
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import java.io.IOException

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        val place: MutableLiveData<Place> = MutableLiveData()
    }

    /*
        Transformations express dependencies between livedata.

        Whenever place changes it will transformed into an address by the code below.

        Same goes for weather.
     */
    val address: LiveData<String> = Transformations.switchMap(place) {
        liveData {
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
    val weather: LiveData<Met.Kall> = Transformations.switchMap(place) {
        liveData {
            val w = Met().locationForecast(it.position)
            emit(w)
        }
    }

    val astronomicalData: LiveData<Met.AstronomicalData?> = Transformations.switchMap(place) {
        liveData {
            val astro = Met().receiveAstroData(it.position)
            emit(astro)
        }
    }

    var cameraPosition: CameraPosition? = null

    fun getPlace() = place
}