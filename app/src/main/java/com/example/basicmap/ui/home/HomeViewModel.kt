package com.example.basicmap.ui.home

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.*
import com.example.basicmap.lib.Met
import com.example.basicmap.ui.places.LivePlace
import com.example.basicmap.ui.places.Place
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import java.io.IOException

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        var livePlace: LivePlace? = null
        var initialized = false
    }

    init {
        // This is a bit ugly, but needed to avoid having to reconnect observers
        if (!initialized) {
            initialized = true
            livePlace = LivePlace(getApplication())
        }
    }

    var cameraPosition: CameraPosition? = null

    fun getPlace(): LivePlace {
        return livePlace!!
    }
}