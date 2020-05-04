package com.example.basicmap.ui.drones

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.basicmap.R
import kotlinx.android.synthetic.main.activity_registrer_drone.*

class RegistrerDrone : AppCompatActivity() {

    private var dronesViewModel = DronesViewModel()
    var droneList =  mutableListOf<Drone>()
    var droneBilde = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrer_drone)

        droneList = dronesViewModel.getDroneList().value!!
        supportActionBar?.title = "Legg til ny Drone"

        //Registrer
        leggTilKnapp.setOnClickListener {
            val melding: String
            val navn = navn.text.toString()
            val valgtVindStyrke = spinner.selectedItem.toString()
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
                val drone = Drone(navn, maksVindStyrke, vanntett, droneBilde.toString())
                droneList.add(drone)
                dronesViewModel.getDroneList().value = droneList
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
            builder.setPositiveButton("OK") { _: DialogInterface, _: Int -> }
            builder.show()
        }
        lastOppBildeKnapp.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                }
                else {
                    pickImageFromGallery()
                }
            }
            else {
                pickImageFromGallery()
            }
        }
    }
    //Last opp bilde
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }
    companion object {
        private val IMAGE_PICK_CODE = 1000;
        private val PERMISSION_CODE = 1001;
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            imageView2.setImageURI(data?.data)
            droneBilde = data?.data.toString()
        }
    }
}
