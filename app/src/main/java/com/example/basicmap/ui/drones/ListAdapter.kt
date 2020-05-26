package com.example.basicmap.ui.drones

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basicmap.R
import com.example.basicmap.R.layout.drone_kort
import kotlinx.android.synthetic.main.drone_kort.view.*


class ListAdapter(val context: Context, val droneList: MutableList<Drone>?) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

     inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(drone: Drone?, pos: Int) {
            itemView.navn.setText(drone?.navn)
            itemView.vindStyrke.setText(
                context.getString(R.string.DLA_maksvindstyrke)
                    .format(drone?.maksVindStyrke.toString())
            )
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
                itemView.vanntett.setText(context.getString(R.string.DLA_vanntett))
            }
            else {
                itemView.vanntett.setText(context.getString(R.string.DLA_ikkevanntett))
            }
            itemView.editKnapp.setOnClickListener {
                val intent = Intent(context, EditDrone::class.java)
                intent.putExtra("pos", pos)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(drone_kort, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val drone = droneList?.elementAt(position)
        holder.setData(drone, position)
    }

    override fun getItemCount(): Int {
        return droneList!!.size
    }

}
