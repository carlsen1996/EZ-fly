package com.example.basicmap.ui.places

import androidx.lifecycle.*

class PlacesViewModel : ViewModel() {
    companion object {
        var places: MutableLiveData<MutableList<LivePlace>> = MutableLiveData()
    }

    fun getPlaces() = places


    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
}

