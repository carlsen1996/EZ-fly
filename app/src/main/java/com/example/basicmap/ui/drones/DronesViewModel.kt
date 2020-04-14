package com.example.basicmap.ui.drones

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DronesViewModel : ViewModel() {
    companion object {
        var droneList = MutableLiveData<MutableList<Drone>>()
    }

    fun getDroneList(): MutableLiveData<MutableList<Drone>> {
        return droneList
    }
}

