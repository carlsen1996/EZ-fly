package com.example.basicmap.lib

import android.graphics.Color
import com.google.gson.Gson
import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.*




    // Utility for querying various zone data, like no-fly zones etc.

    // Could for instance have an method that takes a rectangle (ie. four LatLang's) and returns
    // a collection of all zones (polygons) inside the rectangle.
    // "//" is used for comments from writer
    // and "*/ */" is used for potentially useful code and/or



var LufthavnMutableListe = mutableListOf<LufthavnKlasse>()


fun initNoFlyLufthavnSirkel(jsonStringen : String): MutableList<CircleOptions> {

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


    //val lufthavnNavn = LufthavnMutableListe.map { it.lufthavnNavn}
    val kor1 = LufthavnMutableListe.map { it.lufthavnKordinat1 }
    val kor2 = LufthavnMutableListe.map { it.lufthavnKordinat2 }
    val kor3 = LufthavnMutableListe.map { it.lufthavnKordinat3 }



    for (enkelFlyplass in LufthavnMutableListe) {

        val kor1ArrayKlarForSplitting: String = kor1[teller]
        val kor2ArrayKlarForSplitting: String = kor2[teller]
        val kor3ArrayKlarForSplitting: String = kor3[teller]

        print("Yeet")


        val kor1ArraySplittet = kor1ArrayKlarForSplitting.split(", ").toTypedArray()
        val kor2ArraySplittet = kor2ArrayKlarForSplitting.split(", ").toTypedArray()
        val kor3ArraySplittet = kor3ArrayKlarForSplitting.split(", ").toTypedArray()


        n1 = korTilBedreKor(kor1ArraySplittet[0])
        e1 = korTilBedreKor(kor1ArraySplittet[1])
        n2 = korTilBedreKor(kor2ArraySplittet[0])
        e2 = korTilBedreKor(kor2ArraySplittet[1])
        n3 = korTilBedreKor(kor3ArraySplittet[0])
        e3 = korTilBedreKor(kor3ArraySplittet[1])


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


        sirkelMutableList.add(sirkelOptionis1)
        sirkelMutableList.add(sirkelOptionis2)
        sirkelMutableList.add(sirkelOptionis3)


        teller++

    }


    return sirkelMutableList
}


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
    }
    else if (splittetListe.size == 10) {
        a = (splittetListe[1]+splittetListe[2]).toDouble()
        b = (splittetListe[4]+splittetListe[5]).toDouble()
        c = (splittetListe[6]+splittetListe[7]+"."+splittetListe[8]).toDouble()
    }
    d = a+(b/60)+(c/3600)

    return d
}

