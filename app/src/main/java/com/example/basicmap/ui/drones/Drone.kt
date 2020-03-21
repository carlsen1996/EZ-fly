package com.example.basicmap.ui.drones

import java.io.Serializable

class Drone(navn: String, maksVindStyrke: Int, vanntett: Boolean): Serializable {
    val navn = navn
    val maksVindStyrke = maksVindStyrke
    val vanntett = vanntett
}