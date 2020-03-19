package com.example.basicmap.ui.places

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.basicmap.R

class PlacesFragment : Fragment() {

    private lateinit var tripsViewModel: TripsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tripsViewModel =
            ViewModelProviders.of(this).get(TripsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_places, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        tripsViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}