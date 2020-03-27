package com.example.basicmap.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class HomeViewModel : ViewModel() {
    val position: MutableLiveData<LatLng> = MutableLiveData()
}