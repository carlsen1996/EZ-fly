package com.example.basicmap.ui.home

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.*
import com.example.basicmap.lib.Met
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import java.io.IOException

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val position: MutableLiveData<LatLng> = MutableLiveData()
    val address: LiveData<String> = Transformations.switchMap(position) {
        liveData {
            val geoc = Geocoder(application)
            try {
                val locations = geoc.getFromLocation(it.latitude, it.longitude, 1)
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
    val weather: LiveData<Met.Kall> = Transformations.switchMap(position) {
        liveData {
            val w = Met().locationForecast(it)
            emit(w)
        }
    }

    var cameraPosition: CameraPosition? = null
}