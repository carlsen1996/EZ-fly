package com.example.basicmap.lib

import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import android.content.Context
import com.example.basicmap.ui.home.HomeFragment
import java.io.IOException
import android.util.Log
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.Polygon


/*
    Utility for querying various zone data, like no-fly zones etc.

    Could for instance have an method that takes a rectangle (ie. four LatLang's) and returns
    a collection of all zones (polygons) inside the rectangle.
 */

class Zones {

}
var LufthavnMutableListe = mutableListOf<LufthavnKlasse>()

public fun initNoFlyLufthavn(jsonStringen : String, kartet : GoogleMap) {

    //yeet

    LufthavnMutableListe = Gson().fromJson(jsonStringen, Array<LufthavnKlasse>::class.java).toMutableList()



    var N1 = 0.0
    var E1 = 0.0
    var N2 = 0.0
    var E2 = 0.0
    var N3 = 0.0
    var E3 = 0.0

    var lufthavnNavn = LufthavnMutableListe.map { it -> it.lufthavnNavn}
    val Kor1 = LufthavnMutableListe.map { it.lufthavnKordinat1 }
    val Kor2 = LufthavnMutableListe.map { it.lufthavnKordinat2 }
    val Kor3 = LufthavnMutableListe.map { it.lufthavnKordinat3 }

    Log.d("RenString", jsonStringen)
    val TilbakeFraMutableTilJson: String = Gson().toJson(LufthavnMutableListe)
    Log.d("why", TilbakeFraMutableTilJson)

    val luftpls = "Wewo"





    if (Kor1 == null||Kor2 == null||Kor3 == null) {
        Log.d("Zones", "En kordinat er Null (Kor1/2/3) ------------------------------")
    }

    var teller = 0
    for (enkelFlyplass in LufthavnMutableListe) {

        val Kor1ArrayKlarForSplitting: String = Kor1.get(teller)
        val Kor2ArrayKlarForSplitting: String = Kor2.get(teller)
        val Kor3ArrayKlarForSplitting: String = Kor3.get(teller)

        print("Yeet")
        if (Kor1ArrayKlarForSplitting == null||Kor2ArrayKlarForSplitting == null||Kor3ArrayKlarForSplitting == null) {
            Log.d("Zones", "En Kor-Klar for splitting er NULL ------------------------------")
            val lufthavn1 = lufthavnNavn[0]
            val yeboi = "Yeboi"
            val noboi = "Noboi"
            if (lufthavnNavn == null) {
                Log.d("Lufthavn1", noboi)
            }
            else {
                Log.d("Lufthavn1", yeboi)
            }

            if (Kor1ArrayKlarForSplitting == null) {
                Log.d("Zones", "Kor1Array er NULL ------------------------------")
            }
            if (Kor2ArrayKlarForSplitting == null) {
                Log.d("Zones", "Kor2Array er NULL ------------------------------")
            }
            if (Kor3ArrayKlarForSplitting == null) {
                Log.d("Zones", "Kor3Array er NULL ------------------------------")
            }
            teller++
        }
        else {
            val Kor1ArraySplittet = Kor1ArrayKlarForSplitting.split(",").toTypedArray()
            N1 = Kor1ArraySplittet.get(0).toDouble()
            E1 = Kor1ArraySplittet.get(1).toDouble()
            val Kor2ArraySplittet = Kor2ArrayKlarForSplitting.split(",").toTypedArray()
            N2 = Kor2ArraySplittet.get(0).toDouble()
            E2 = Kor2ArraySplittet.get(1).toDouble()
            val Kor3ArraySplittet = Kor3ArrayKlarForSplitting.split(",").toTypedArray()
            N3 = Kor3ArraySplittet.get(0).toDouble()
            E3 = Kor3ArraySplittet.get(1).toDouble()

            var LatLng1 = LatLng(N1, E1)
            var LatLng2 = LatLng(N2, E2)
            var LatLng3 = LatLng(N3, E3)


            val sirkelfarge = Color.parseColor("#66FF0000")



            val circleOptions1 = CircleOptions()
                .center(LatLng1) // Senteret
                .radius(5000.0) // I meter
                .fillColor(sirkelfarge) // RBG + alpha (transparancy)
                .strokeColor(Color.TRANSPARENT) // Utkanten av sirkelen
            val circleOptions2 = CircleOptions()
                .center(LatLng2)
                .radius(5000.0)
                .fillColor(sirkelfarge)
                .strokeColor(Color.TRANSPARENT)
            val circleOptions3 = CircleOptions()
                .center(LatLng3)
                .radius(5000.0)
                .fillColor(sirkelfarge)
                .strokeColor(Color.TRANSPARENT)

            //-----------------------------------------------------
            //Kommenter ut dette

            val circle1: Circle = kartet.addCircle(circleOptions1)
            val circle2: Circle = kartet.addCircle(circleOptions2)
            val circle3: Circle = kartet.addCircle(circleOptions3)

            //Kommenter ut dette
            //-----------------------------------------------------

            //yeet

            //-----------------------------------------------------
            //Kommenter inn dette

            /*var kordinatListe = arrayOf<LatLng>(LatLng1, LatLng2, LatLng3)
            var ekteKordinatListe = mutableListOf<LatLng>(LatLng1, LatLng2, LatLng3)

            val trekantpoligon = kartet.addPolygon(
                PolygonOptions()
                    //.fillColor(sirkelfarge)
                    //.add(kordinatListe)
                    .addAll(ekteKordinatListe)
            )


            var polyguneerPunktListe = mutableListOf<LatLng>()
            var tellerkar = 0
            while (tellerkar <3) {
                var p = ekteKordinatListe[tellerkar]
                var sirkelPunkter = mutableListOf<LatLng>()
                sirkelPunkter = (tegnSirkel(p, 5000, 1))
                for (punkt in sirkelPunkter) {
                    polyguneerPunktListe.add(punkt)
                }
                //polyguneerPunktListe.add(tegnSirkel(p, 5000, 1))
            }



            val poligono = kartet.addPolygon(
                PolygonOptions()
                    //.fillColor(sirkelfarge)
                    //.add(kordinatListe)
                    .addAll(polyguneerPunktListe)
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(sirkelfarge)
            )*/

            //Kommenter inn dette
            //-----------------------------------------------------




            teller++
        }

    }



}


fun tegnSirkel(sentrum : LatLng, radius : Int, dir : Int):MutableList<LatLng> {
    var radian = (Math.PI/180)
    var grader = (180/Math.PI)
    var radousPaaJorden = 6371
    var points = 32

    var rlat = (radius / radousPaaJorden)*grader
    var rlng = rlat/Math.cos(sentrum.latitude*radian)


    var punktListe = mutableListOf<LatLng>()
    var start: Int
    var ende: Int
    if (dir==1) {
        start = 0
        ende = points+1
    }
    else {
        start = points+1
        ende = 0
    }
    var i = start
    if (dir == 1){

        // i < ende
        while (i >= ende) {
            var theta = Math.PI*(i/(points+1))
            var ey = sentrum.longitude+(rlng*Math.cos(theta))
            var ex = sentrum.latitude+(rlat*Math.sin(theta))
            i=i+dir
        }
        i = 0
    }
    else {
        // i > ende
        while (i <= ende) {
            var theta = Math.PI*(i/(points+1))
            var ey = sentrum.longitude+(rlng*Math.cos(theta))
            var ex = sentrum.latitude+(rlat*Math.sin(theta))
            punktListe.add(1, LatLng(ey, ex))
            i=i+dir
        }
        i = 0
    }
    return punktListe

}

fun getJsonDataFromAsset(context: Context, fileName: String): String {
    val jsonString: String
    /*
    try {
        jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }*/
    jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    return jsonString
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

/*val sirkelnr = teller.toString()
        var sirkel1_navn = sirkel1navn + sirkelnr
        val sirkel2_navn = sirkel2navn + sirkelnr
        val sirkel3_navn = sirkel3navn + sirkelnr*/
//val lstVerdier1: List<String> = Kor1.split(", ").map{it->it.trim()}

/*val wow = "yeet"
wow.split(",")*/

//(::polyguneerPunktListe.isInitialized)
