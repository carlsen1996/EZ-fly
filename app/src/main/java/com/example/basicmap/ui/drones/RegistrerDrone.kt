package com.example.basicmap.ui.drones

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.basicmap.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_registrer_drone.*
import java.io.Serializable
import java.lang.reflect.Type

class RegistrerDrone : AppCompatActivity() {

    var droneList =  mutableListOf<Drone>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrer_drone)

        getSupportActionBar()?.setTitle("Legg til ny Drone")
        loadData()
        leggTilKnapp.setOnClickListener {
            val melding: String
            val navn = navn.getText().toString()
            val valgtVindStyrke = spinner.getSelectedItem().toString()
            var maksVindStyrke = 0
            val vanntett = checkBox.isChecked
            when(valgtVindStyrke) {
                "2 m/s" -> maksVindStyrke = 2
                "8 m/s" -> maksVindStyrke = 8
                "12 m/s" -> maksVindStyrke = 12
                "17 m/s" -> maksVindStyrke = 17
            }
            if(navn.isEmpty()) {
                melding = "Velg navn på dronen din"
                val toast = Toast.makeText(this@RegistrerDrone, melding, Toast.LENGTH_LONG)
                toast.show()
            }
            else {
                val drone = Drone(navn, maksVindStyrke, vanntett)
                droneList.add(drone)
                saveData()
                for(i in droneList) {
                    Log.v("navn", i.navn)
                }

                melding = "Drone lagt til"
                val toast = Toast.makeText(this@RegistrerDrone, melding, Toast.LENGTH_LONG)
                toast.show()
                finish()
            }
        }
    }
    private fun saveData() {
        val sharedPref: SharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val gson = GsonBuilder().create()
        val json = gson.toJson(droneList)
        editor.putString("droneList", json)
        editor.commit()
    }

    inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)
    private fun loadData() {
        val sharedPref: SharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE)
        val gson = GsonBuilder().create()
        val json = sharedPref.getString("droneList", null)
        val drones = gson.fromJson(json, Array<Drone>::class.java)
        if(drones == null) {
            return
        }
        droneList = drones.toMutableList()
    }
    private fun clearSharedPref() {
        val sharedPref: SharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.commit()
    }
}
