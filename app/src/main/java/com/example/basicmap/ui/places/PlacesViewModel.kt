package com.example.basicmap.ui.places

import android.location.Geocoder
import androidx.lifecycle.*
import com.example.basicmap.lib.Met
import com.google.android.gms.maps.model.CameraPosition

class PlacesViewModel : ViewModel() {
    companion object {
        var places = MutableLiveData(mutableListOf<Place>())
    }

    fun getPlaces() = places


    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
}

class LivePlace {
    val place: MutableLiveData<Place> = MutableLiveData()

    val weather: LiveData<Met.Kall?> = Transformations.switchMap(place) {
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
}