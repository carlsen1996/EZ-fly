package com.example.basicmap.lib

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.example.basicmap.ui.drones.Drone
import com.example.basicmap.ui.places.Place
import com.google.gson.GsonBuilder

fun saveDrones(context: Context, drones: List<Drone>?): String? {
    val sharedPref: SharedPreferences =
        context.getSharedPreferences("sharedPref", AppCompatActivity.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPref.edit()
    val gson = GsonBuilder().create()
    val json = gson.toJson(drones)
    editor.putString("droneList", json)
    editor.apply()
    return json
}

fun loadDrones(context: Context): MutableList<Drone>? {
    val sharedPref: SharedPreferences =
        context.getSharedPreferences("sharedPref", AppCompatActivity.MODE_PRIVATE)
    val gson = GsonBuilder().create()
    val json = sharedPref.getString("droneList", null)
    val drones = gson.fromJson(json, Array<Drone>::class.java) ?: return null
    return drones.toMutableList()
}

fun loadPlaces(context: Context): MutableList<Place>? {
    val sharedPrefPlaces: SharedPreferences = context.getSharedPreferences("sharedPrefPlaces", AppCompatActivity.MODE_PRIVATE)
    val gson = GsonBuilder().create()
    val json = sharedPrefPlaces.getString("placesList", null)
    val places = gson.fromJson(json, Array<Place>::class.java) ?: return null
    return places.toMutableList()
}

fun savePlaces(context: Context, places: List<Place>): String? {
    val sharedPref: SharedPreferences = context.getSharedPreferences("sharedPrefPlaces", AppCompatActivity.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPref.edit()
    val gson = GsonBuilder().create()
    val json = gson.toJson(places)
    editor.putString("placesList", json)
    editor.apply()
    return json
}
