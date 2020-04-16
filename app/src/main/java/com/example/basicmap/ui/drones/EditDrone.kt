package com.example.basicmap.ui.drones

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.basicmap.R
import kotlinx.android.synthetic.main.activity_edit_drone.*
import kotlinx.android.synthetic.main.activity_edit_drone.checkBox
import kotlinx.android.synthetic.main.activity_edit_drone.navn
import kotlinx.android.synthetic.main.activity_edit_drone.spinner
import kotlinx.android.synthetic.main.activity_edit_drone.vindInfoKnapp
import kotlinx.android.synthetic.main.activity_registrer_drone.*

class EditDrone : AppCompatActivity() {

    private var dronesViewModel = DronesViewModel()
    var droneList =  mutableListOf<Drone>()
    var droneBilde = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_drone)
        supportActionBar?.title = "Rediger"

        droneList = dronesViewModel.getDroneList().value!!.toMutableList()

        val pos = getIntent().getIntExtra("pos", 0)
        val drone = droneList.elementAt(pos)
        droneBilde = drone.imgSrc

        navn.setText(drone.navn)
        when(drone.maksVindStyrke) {
            2 -> spinner.setSelection(0)
            8 -> spinner.setSelection(1)
            12 -> spinner.setSelection(2)
            17 -> spinner.setSelection(3)
        }
        checkBox.isChecked = drone.vanntett
        if(drone.imgSrc == "") {
            Glide.with(this)
                .load(R.drawable.drone_img_asst)
                .into(imageView3)
        }
        else {
            Glide.with(this)
                .load(drone.imgSrc)
                .into(imageView3)
        }

        //Endre drone informasjon
        endreKnapp.setOnClickListener {
            if(navn.text.isNullOrEmpty()) {
                val toast = Toast.makeText(this@EditDrone, "Velg navn på dronen din", Toast.LENGTH_LONG)
                toast.show()
            }
            else {
                drone.navn = navn.getText().toString()
                when(spinner.selectedItem.toString()) {
                    "2 m/s" -> drone.maksVindStyrke = 2
                    "8 m/s" -> drone.maksVindStyrke = 8
                    "12 m/s" -> drone.maksVindStyrke = 12
                    "17 m/s" -> drone.maksVindStyrke = 17
                }
                drone.vanntett = checkBox.isChecked
                drone.imgSrc = droneBilde
                dronesViewModel.getDroneList().value = droneList

                finish()
            }
        }
        //Slett Drone
        slettKnapp.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Slett Drone")
            builder.setMessage("Er du sikker på at du vil slette \n" + drone.navn + " ?")
            builder.setPositiveButton("Ja") { _, _ ->
                droneList.remove(drone)
                dronesViewModel.getDroneList().value = droneList

                finish()
            }
            builder.setNegativeButton("Nei") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
        vindInfoKnapp.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Maks vindstyrke")
            builder.setMessage("Alle dronemodeller kommer med informasjon om den høyeste vindstyrken det er mulig for dronen og fly i. " +
                    "Sjekk manualen eller besøk produsentens nettside for å finne din drones maks vindstyrke.")
            builder.setPositiveButton("OK") { _: DialogInterface, _: Int -> }
            builder.show()
        }
        //Endre bilde
        endreBildeKnapp.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, EditDrone.PERMISSION_CODE)
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
        when(requestCode){
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
            //imageView3.setImageURI(data?.data)
            Glide.with(this)
                .load(data?.data)
                .into(imageView3)
            droneBilde = data?.data.toString()
        }
    }
}
