package com.example.basicmap.lib

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class MetTest {
    @Test
    fun forecast() {
        runBlocking {
            val weather = Met().locationForecast(LatLng(50.0, 50.0))
            assertNotNull(weather?.properties?.timeseries?.get(0))
        }
    }

}