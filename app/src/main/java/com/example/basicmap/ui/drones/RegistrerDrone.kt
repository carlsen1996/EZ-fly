package com.example.basicmap.ui.drones

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.basicmap.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_registrer_drone.*

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
                val drone = Drone(navn, maksVindStyrke, vanntett, "@mipmap/appicon_128")
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
        vindInfoKnapp.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Maks vindstyrke")
            builder.setMessage("Alle dronemodeller kommer med informasjon om den høyeste vindstyrken det er mulig for dronen og fly i. " +
                    "Sjekk manualen eller besøk produsentens nettside for å finne din drones maks vindstyrke.")
            builder.setPositiveButton("OK", { dialogInterface: DialogInterface, i: Int -> })
            builder.show()
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
