package com.example.basicmap.lib

import android.graphics.Color
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson


/*
    Utility for querying various zone data, like no-fly zones etc.

    Could for instance have an method that takes a rectangle (ie. four LatLang's) and returns
    a collection of all zones (polygons) inside the rectangle.
 */

class Zones {

}
private var LufthavnMutableListe = mutableListOf<Lufthavn>()

private fun initNoFlyLufthavn(jsonStringenMedFlyplasser : String) {

    /*val fraStringStedet = getString(R.string.flyplassListe)*/
    var lufthavnerlisteliste = Gson().fromJson(jsonStringenMedFlyplasser, Array<Lufthavn>::class.java).toList()
    LufthavnMutableListe = lufthavnerlisteliste.toMutableList()


    /*val lufthavnListeStoerrelse = 1
    val teller = 0
    while (teller == lufthavnListeStoerrelse) {

    }*/

    for (enkelFlyplass in LufthavnMutableListe) {

    }
    var N1 = 0.0
    var E1 = 0.0
    var N2 = 0.0
    var E2 = 0.0
    var N3 = 0.0
    var E3 = 0.0
    val lufthavnNavn = LufthavnMutableListe.map { it.lufthavnNavn }
    val Kor1 = LufthavnMutableListe.map { it.lufthavnKordinat1 }
    val Kor2 = LufthavnMutableListe.map { it.lufthavnKordinat1 }
    val Kor3 = LufthavnMutableListe.map { it.lufthavnKordinat1 }

    var teller = 0
   /* val lufthavnListeStoerrelse = 1
    var sirkel1navn = "1sirkel"
    var sirkel2navn = "2sirkel"
    var sirkel3navn = "3sirkel"*/

    for (enkelFlyplass in LufthavnMutableListe) {
        /*val sirkelnr = teller.toString()
        var sirkel1_navn = sirkel1navn + sirkelnr
        val sirkel2_navn = sirkel2navn + sirkelnr
        val sirkel3_navn = sirkel3navn + sirkelnr*/
        //val lstVerdier1: List<String> = Kor1.split(", ").map{it->it.trim()}

        /*val wow = "yeet"
        wow.split(",")*/

        val Kor1ArrayKlarForSplitting: String = Kor1.get(teller)
        val Kor1ArraySplittet = Kor1ArrayKlarForSplitting.split(",").toTypedArray()
        N1 = Kor1ArraySplittet.get(0).toDouble()
        E1 = Kor1ArraySplittet.get(1).toDouble()
        val Kor2ArrayKlarForSplitting: String = Kor2.get(teller)
        val Kor2ArraySplittet = Kor2ArrayKlarForSplitting.split(",").toTypedArray()
        N2 = Kor2ArraySplittet.get(0).toDouble()
        E2 = Kor2ArraySplittet.get(1).toDouble()
        val Kor3ArrayKlarForSplitting: String = Kor3.get(teller)
        val Kor3ArraySplittet = Kor1ArrayKlarForSplitting.split(",").toTypedArray()
        N3 = Kor3ArraySplittet.get(0).toDouble()
        E3 = Kor3ArraySplittet.get(1).toDouble()

        // fått og gjort alle verdiene om til doubler.
        // gjør dem til LatLng

        var LatLng1 = LatLng(N1, E1)
        var LatLng2 = LatLng(N2, E2)
        var LatLng3 = LatLng(N3, E3)

        // ez pz next task
        // 5km avstand





        val circleOptions1 = CircleOptions()
            .center(LatLng1)
            .radius(5000.0) // I meter
            .fillColor(229, 40, 40, 0.8) // RBG + alpha (transparancy)
            .strokeColor(Color.TRANSPARENT) // Utkanten av sirkelen
            .
        val circleOptions2 = CircleOptions()
            .center(LatLng2)
            .radius(5000.0)
            .fillColor(229, 40, 40, 0.8)
            .strokeColor(Color.TRANSPARENT)
        val circleOptions3 = CircleOptions()
            .center(LatLng3)
            .radius(5000.0)
            .fillColor(229, 40, 40, 0.8)
            .strokeColor(Color.TRANSPARENT)


        //val circle1: Circle = myMap.addCircle(circleOptions1)
        //val circle2: Circle = myMap.addCircle(circleOptions2)
        //val circle3: Circle = myMap.addCircle(circleOptions3)

        // Ikke glem å legge til en som mater inn stringen i main filen



        teller++
    }



}

/*
val Kor3ArraySplittet: List<String> = Kor3.get(teller).split(",") //Kor3.split(",")
N3 = Kor3ArraySplittet.get(0).toDouble()
E3 = Kor3ArraySplittet.get(1).toDouble()*/

/*// Create marker
var marker = new google.maps.Marker({
    map: map,
    position: new google.maps.LatLng(53, -2.5),
    title: 'Some location'
});

// Add circle overlay and bind to marker
var circle = new google.maps.Circle({
        map: map,
        radius: 16093,    // 10 miles in metres
        fillColor: '#AA0000'
});
circle.bindTo('center', marker, 'position');*/
