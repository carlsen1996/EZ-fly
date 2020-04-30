package com.example.basicmap.lib

import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class ZonesKtTest {

    @Test
    fun lufthavnSirkler() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonFilStringen = getJsonDataFromAsset(appContext, "lufthavnRawJson.json")
        val lufthavner = Gson().fromJson(jsonFilStringen, Array<LufthavnKlasse>::class.java)
        val sirkler = initNoFlyLufthavnSirkel(jsonFilStringen)

        assertEquals("Lufthavner har 3 koordinater", lufthavner.size*3, sirkler.size)
    }

}