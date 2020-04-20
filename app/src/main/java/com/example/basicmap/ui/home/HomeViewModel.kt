package com.example.basicmap.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.basicmap.lib.Met
import com.example.basicmap.ui.places.Place
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

class HomeViewModel(private val state: SavedStateHandle) : ViewModel() {
    val position: MutableLiveData<LatLng> = MutableLiveData()
    val address: MutableLiveData<String> = MutableLiveData("")
    val weather: MutableLiveData<Met.Kall> = MutableLiveData()

    var cameraPosition: CameraPosition? = null
}