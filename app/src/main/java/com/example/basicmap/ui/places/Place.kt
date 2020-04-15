package com.example.basicmap.ui.places

import com.google.android.gms.maps.model.LatLng

data class Place(
    var position: LatLng,
    var address: String?
)