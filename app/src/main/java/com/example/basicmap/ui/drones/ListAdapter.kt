package com.example.basicmap.ui.drones

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basicmap.R
import com.example.basicmap.R.layout.drone_kort
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.drone_kort.view.*


class ListAdapter(val context: Context, val droneList: MutableList<Drone>) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(drone: Drone?, pos: Int) {
            itemView.navn.setText(drone?.navn)
            itemView.vindStyrke.setText("Maks vindstyrke: " + drone?.maksVindStyrke.toString())
            if(drone?.imgSrc == "") {
                Glide.with(itemView)
                    .load(R.drawable.drone_img_asst)
                    .into(itemView.imageView)
            }
            else {
                Glide.with(itemView)
                    .load(drone?.imgSrc)
                    .into(itemView.imageView)
            }
            if(drone?.vanntett == true) {
                itemView.vanntett.setText("Er vanntett")
            }
            else {
                itemView.vanntett.setText("Ikke vanntett")
            }
            itemView.deleteKnapp.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Slett Drone")
                builder.setMessage("Er du sikker på at du vil slette \n" + drone?.navn + " ?")
                builder.setPositiveButton("Ja") { dialog, which ->
                    slettDrone(pos)
                }
                builder.setNegativeButton("Nei") { dialog, which ->
                    dialog.dismiss()
                }
                builder.show()
            }
        }
        fun slettDrone(pos: Int) {
            droneList.removeAt(pos)
            notifyDataSetChanged()
            saveData()
        }
        private fun saveData() {
            val sharedPref: SharedPreferences = context.getSharedPreferences("sharedPref", AppCompatActivity.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPref.edit()
            val gson = GsonBuilder().create()
            val json = gson.toJson(droneList)
            editor.putString("droneList", json)
            editor.commit()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(drone_kort, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val drone = droneList.elementAt(position)
        holder.setData(drone, position)
    }

    override fun getItemCount(): Int {
        return droneList.size
    }
}