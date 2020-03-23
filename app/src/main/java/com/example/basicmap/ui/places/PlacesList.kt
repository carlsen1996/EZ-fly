package com.example.basicmap.ui.places

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.basicmap.R
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.popup.view.*

class PlacesList(private  val places: MutableList<LatLng>): RecyclerView.Adapter<PlacesList.ViewHolder>() {
    class ViewHolder(val cardView: View) : RecyclerView.ViewHolder(cardView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.popup, parent, false)
        return ViewHolder(cardView)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = places[position]

        holder.cardView.textView.text = "${p.latitude}, ${p.longitude}"
    }
}