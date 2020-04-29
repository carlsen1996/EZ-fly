package com.example.basicmap.lib

import androidx.test.platform.app.InstrumentationRegistry
import com.example.basicmap.ui.drones.Drone
import org.junit.Assert.*
import org.junit.Test

class SharedPrefsKtTest {
    @Test
    fun saveLoad() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val drones = listOf(
            Drone("foo", 3, false, ""),
            Drone("bar", 7, true, "/foo/bar.png")
        )
        saveDrones(context, drones)
        val drones2 = loadDrones(context)
        assertNotNull(drones2)
        assertArrayEquals("", drones.toTypedArray(), drones2!!.toTypedArray())
    }
}