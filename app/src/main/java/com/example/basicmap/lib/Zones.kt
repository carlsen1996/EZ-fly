package com.example.basicmap.lib

import android.graphics.Color
import com.google.gson.Gson
import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.*

/*import com.google.android.gms.maps.GoogleMap
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import com.example.basicmap.ui.home.HomeFragment
import java.io.IOException
import com.google.android.gms.maps.model.Polygon*/



    // Utility for querying various zone data, like no-fly zones etc.

    // Could for instance have an method that takes a rectangle (ie. four LatLang's) and returns
    // a collection of all zones (polygons) inside the rectangle.
    // "//" is used for comments from writer
    // and "*/ */" is used for potentially useful code and/or



var LufthavnMutableListe = mutableListOf<LufthavnKlasse>()

/*public fun initNoFlyLufthavn(jsonStringen : String, kartet : GoogleMap) {*/
/*public fun initNoFlyLufthavnSirkel(jsonStringen : String, kartet : GoogleMap): MutableList<CircleOptions> {*/
public fun initNoFlyLufthavnSirkel(jsonStringen : String): MutableList<CircleOptions> {

    // yeet
    // flyplasser fra:
    // https://luftfartstilsynet.no/aktorer/flyplass/landingsplasser/godkjente-lufthavner-og-flyplasser/

    val sirkelMutableList = mutableListOf<CircleOptions>()
    LufthavnMutableListe = Gson().fromJson(jsonStringen, Array<LufthavnKlasse>::class.java).toMutableList()

    var teller = 0

    var n1: Double
    var e1: Double
    var n2: Double
    var e2: Double
    var n3: Double
    var e3: Double


    val lufthavnNavn = LufthavnMutableListe.map { it.lufthavnNavn}
    val kor1 = LufthavnMutableListe.map { it.lufthavnKordinat1 }
    val kor2 = LufthavnMutableListe.map { it.lufthavnKordinat2 }
    val kor3 = LufthavnMutableListe.map { it.lufthavnKordinat3 }

    /*Log.d("RenString", jsonStringen)
    val TilbakeFraMutableTilJson: String = Gson().toJson(LufthavnMutableListe)
    Log.d("why", TilbakeFraMutableTilJson)
    val luftpls = "Wewo"*/


    for (enkelFlyplass in LufthavnMutableListe) {

        val kor1ArrayKlarForSplitting: String = kor1[teller]
        val kor2ArrayKlarForSplitting: String = kor2[teller]
        val kor3ArrayKlarForSplitting: String = kor3[teller]

        print("Yeet")

        // Må gjøre doublen om til faktisk riktige kordinater med korTilBedreKor

        val kor1ArraySplittet = kor1ArrayKlarForSplitting.split(", ").toTypedArray()
        val kor2ArraySplittet = kor2ArrayKlarForSplitting.split(", ").toTypedArray()
        val kor3ArraySplittet = kor3ArrayKlarForSplitting.split(", ").toTypedArray()

        /*Log.d("e1", kor1ArraySplittet.get(0).toString())
        Log.d("e1", kor1ArraySplittet.get(1).toString())
        Log.d("e1", kor2ArraySplittet.get(0).toString())
        Log.d("e1", kor2ArraySplittet.get(1).toString())
        Log.d("e1", kor3ArraySplittet.get(0).toString())
        Log.d("e1", kor3ArraySplittet.get(1).toString())
        Log.d("-", " ")*/
        n1 = korTilBedreKor(kor1ArraySplittet[0])
        e1 = korTilBedreKor(kor1ArraySplittet[1])
        n2 = korTilBedreKor(kor2ArraySplittet[0])
        e2 = korTilBedreKor(kor2ArraySplittet[1])
        n3 = korTilBedreKor(kor3ArraySplittet[0])
        e3 = korTilBedreKor(kor3ArraySplittet[1])

        /*Log.d("n1", n1.toString())
        Log.d("e1", n1.toString())
        Log.d("n2", N2.toString())
        Log.d("e2", N2.toString())
        Log.d("n3", N3.toString())
        Log.d("e3", N3.toString())
        Log.d("-", "------------------")*/

        val latlng1 = LatLng(n1, e1)
        val latlng2 = LatLng(n2, e2)
        val latlng3 = LatLng(n3, e3)


        val sirkelfarge = Color.parseColor("#66FF0000")


        val sirkelOptionis1 = CircleOptions()
            .center(latlng1) // Senteret
            .radius(5000.0) // I meter
            .fillColor(sirkelfarge) // RBG + alpha (transparancy)
            .strokeColor(Color.TRANSPARENT) // Utkanten av sirkelen
        val sirkelOptionis2 = CircleOptions()
            .center(latlng2) // Senteret
            .radius(5000.0) // I meter
            .fillColor(sirkelfarge) // RBG + alpha (transparancy)
            .strokeColor(Color.TRANSPARENT) // Utkanten av sirkelen
        val sirkelOptionis3 = CircleOptions()
            .center(latlng3) // Senteret
            .radius(5000.0) // I meter
            .fillColor(sirkelfarge) // RBG + alpha (transparancy)
            .strokeColor(Color.TRANSPARENT) // Utkanten av sirkelen

        //-----------------------------------------------------
        //Kommenter ut dette

        sirkelMutableList.add(sirkelOptionis1)
        sirkelMutableList.add(sirkelOptionis2)
        sirkelMutableList.add(sirkelOptionis3)


        teller++

    }


    return sirkelMutableList
}

//For eventuell polygon

/*public fun initNoFlyLufthavn(jsonStringen : String, kartet : GoogleMap) {*/
/*public fun initNoFlyLufthavnPolygon(jsonStringen : String, kartet : GoogleMap): MutableList<LatLng> {

    // yeet
    // flyplasser fra:
    // https://luftfartstilsynet.no/aktorer/flyplass/landingsplasser/godkjente-lufthavner-og-flyplasser/

    val PolygonMutableList = mutableListOf<PolygonOptions>()
    LufthavnMutableListe = Gson().fromJson(jsonStringen, Array<LufthavnKlasse>::class.java).toMutableList()

    var teller = 0

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

    *//*Log.d("RenString", jsonStringen)
    val TilbakeFraMutableTilJson: String = Gson().toJson(LufthavnMutableListe)
    Log.d("why", TilbakeFraMutableTilJson)
    val luftpls = "Wewo"*//*


    if (Kor1 == null||Kor2 == null||Kor3 == null) {
        Log.d("Zones", "En kordinat er Null (Kor1/2/3) ------------------------------")
    }

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

            // Må gjøre doublen om til faktisk riktige kordinater med korTilBedreKor

            val Kor1ArraySplittet = Kor1ArrayKlarForSplitting.split(", ").toTypedArray()
            val Kor2ArraySplittet = Kor2ArrayKlarForSplitting.split(", ").toTypedArray()
            val Kor3ArraySplittet = Kor3ArrayKlarForSplitting.split(", ").toTypedArray()

            *//*Log.d("E1", Kor1ArraySplittet.get(0).toString())
            Log.d("E1", Kor1ArraySplittet.get(1).toString())
            Log.d("E1", Kor2ArraySplittet.get(0).toString())
            Log.d("E1", Kor2ArraySplittet.get(1).toString())
            Log.d("E1", Kor3ArraySplittet.get(0).toString())
            Log.d("E1", Kor3ArraySplittet.get(1).toString())
            Log.d("-", " ")*//*
            N1 = korTilBedreKor(Kor1ArraySplittet[0])
            E1 = korTilBedreKor(Kor1ArraySplittet[1])
            N2 = korTilBedreKor(Kor2ArraySplittet[0])
            E2 = korTilBedreKor(Kor2ArraySplittet[1])
            N3 = korTilBedreKor(Kor3ArraySplittet[0])
            E3 = korTilBedreKor(Kor3ArraySplittet[1])

            *//*Log.d("N1", N1.toString())
            Log.d("E1", N1.toString())
            Log.d("N2", N2.toString())
            Log.d("E2", N2.toString())
            Log.d("N3", N3.toString())
            Log.d("E3", N3.toString())
            Log.d("-", "------------------")*//*

            val LatLng1 = LatLng(N1, E1)
            val LatLng2 = LatLng(N2, E2)
            val LatLng3 = LatLng(N3, E3)


            val farge = Color.parseColor("#66FF0000")


            var kordinatListe = arrayOf<LatLng>(LatLng1, LatLng2, LatLng3)
            var ekteKordinatListe = mutableListOf<LatLng>(LatLng1, LatLng2, LatLng3)



            *//*var polyguneerPunktListe = mutableListOf<LatLng>()
            var tellerkar = 0
            while (tellerkar <3) {
                var p = ekteKordinatListe[tellerkar]
                var sirkelPunkter = mutableListOf<LatLng>()
                sirkelPunkter = (tegnSirkel(p, 5000, 1))
                for (punkt in sirkelPunkter) {
                    polyguneerPunktListe.add(punkt)
                }
                polyguneerPunktListe.add(tegnSirkel(p, 5000, 1))
            }*//*



            *//*val poligono = kartet.addPolygon(
                PolygonOptions()
                    //.fillColor(sirkelfarge)
                    //.add(kordinatListe)
                    .addAll(polyguneerPunktListe)
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(farge)
            ):Path {

            }*//*



            //Kommenter inn dette
            //-----------------------------------------------------

            *//*PolygonMutableList.add(poligonoOptioni)*//*


            teller++
        }

    }


    return PolygonMutableList
}*/


/*fun tegnSirkel(sentrum : LatLng, radius : Int, dir : Int):MutableList<LatLng> {
    var radian = (Math.PI/180)
    var grader = (180/Math.PI)
    var radousPaaJorden = 6371
    var points = 32

    var rlat = (radius / radousPaaJorden)*grader
    var rlng = rlat/Math.cos(sentrum.latitude*radian)


    var punktListe = MutableList<LatLng>()
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
            val theta = Math.PI*(i/(points+1))
            var ey = sentrum.longitude+(rlng*Math.cos(theta))
            var ex = sentrum.latitude+(rlat*Math.sin(theta))
            i=i+dir
        }
        i = 0
    }
    else {
        // i > ende
        while (i <= ende) {
            val theta = Math.PI*(i/(points+1))
            var ey = sentrum.longitude+(rlng*Math.cos(theta))
            var ex = sentrum.latitude+(rlat*Math.sin(theta))
            punktListe.add(1, LatLng(ey, ex))
            i=i+dir
        }
        i = 0
    }
    return punktListe

}*/

fun getJsonDataFromAsset(context: Context, fileName: String): String {
    val jsonString: String
    jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    return jsonString
}

fun korTilBedreKor(daarligKor : String) : Double {
    var a = 0.0
    var b = 0.0
    var c = 0.0
    var d = 0.0

    val splittetListe = daarligKor.split("")
    if (splittetListe.size == 9) {
        a = (splittetListe[1]).toDouble()
        b = (splittetListe[3]+splittetListe[4]).toDouble()
        c = (splittetListe[5]+splittetListe[6]+"."+splittetListe[7]).toDouble()
        /*Log.d("9", "9")*/
    }
    else if (splittetListe.size == 10) {
        a = (splittetListe[1]+splittetListe[2]).toDouble()
        b = (splittetListe[4]+splittetListe[5]).toDouble()
        c = (splittetListe[6]+splittetListe[7]+"."+splittetListe[8]).toDouble()
        /*Log.d("10", "10")*/
    }
    d = a+(b/60)+(c/3600)

    /*Log.d("dk", daarligKor)
    Log.d("sl", splittetListe.toString())
    Log.d("a", a.toString())
    Log.d("b", b.toString())
    Log.d("c", c.toString())
    Log.d("d", d.toString())
    Log.d("-", "------------------")*/

    return d
}

