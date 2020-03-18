# [gmaps](https://developers.google.com/maps/documentation/android-sdk/start)

[android-sdk](https://developers.google.com/maps/documentation/android-sdk/intro)

# Location services

[Location services](https://developer.android.com/training/location/request-updates)
[get last known location](https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient.html)


Get device location:
```kotlin
LocationServices
    .getFusedLocationProviderClient(context!!)
    .lastLocation
    .addOnCompleteListener { it: Task<Location!> ->
        it.result?.apply { // this: Location 
            LagLng(this.latitude, this.longitude)
        }
    }

```

# [Places](https://developers.google.com/places/android-sdk/intro)

[autocomplete for search](https://developers.google.com/places/android-sdk/reference/com/google/android/libraries/places/widget/Autocomplete)

Get information about the current place. (Not sure yet how to get this for an
arbitrary place):
```kotlin
val placesClient = Places.createClient(context!!)

// The fields we're interested in
val placeFields: List<Place.Field> = listOf(
    Place.Field.NAME,
    Place.Field.ADDRESS,
    Place.Field.LAT_LNG
)
val request = FindCurrentPlaceRequest.newInstance(placeFields)
placesClient
    .findCurrentPlace(request)
    .addOnCompleteListener { it: Task<FetchPlaceResponse!> ->
        if (it.isSuccessful && it.result != null) {
            val likely = it.result!!
            for (placeLikelihood in likely.placeLikelihoods) {
                val place = placeLikelihood.place
                Log.d("${place.name}, ${place.address}, ${place.latlng}")
            }
        }
}


```
