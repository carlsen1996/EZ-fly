package com.example.basicmap.ui.drones

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.basicmap.R.layout.drone_kort
import kotlinx.android.synthetic.main.drone_kort.view.*

class ListAdapter(val context: Context, val droneList: MutableList<Drone>) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(drone: Drone?, pos: Int) {
            itemView.navn.setText(drone?.navn)
            itemView.vindStyrke.setText("Maks vindstyrke: " + drone?.maksVindStyrke.toString())
            if(drone?.vanntett == true) {
                itemView.vanntett.setText("Er vanntett")
            }
            else {
                itemView.vanntett.setText("Ikke vanntett")
            }
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