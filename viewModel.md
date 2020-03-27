# ViewModel

https://developer.android.com/jetpack/docs/guide

I motsetning til desktop applikasjoner må mobil applikasjoner håndtere å bli
drept av OSet uten bruker interaksjon. Dette gjøres delvis for å håndtere mobilens
minimale ressurser. Det kan sees på som om applikasjonen skal kunne dehydreres
slik at den ikke lenger bruker nevneverdig minne etc. for at den så enkelt kan
hydreres til live igjen når brukeren kommer tilbake.

Dvs. at Aktiviteter og fragmenter er ustabile objekter. De kan bli drept når som helst,
selv når fragmentet har fokus vil det bli laget på nytt ved f.eks. rotasjon og
visse andre preferanse endringer.

Rent teknisk betyr det at fragmenter bare skal ha ansvar for ytterste grafiske
laget, med andre ord det skal håndtere `View`s.

Kortvarig tilstand/state (eg. kart lokasjon, scrollposisjon etc.) bør lagres i
en
[`ViewModel`](https://developer.android.com/topic/libraries/architecture/viewmodel)
som overlever rotasjon etc. Tilstand, som f.eks. en drone, må lagres langvarig i
f.eks. en database eller `SharedPreferences`. `ViewModel`s settes opp slik:
```kotlin
class HomeFragment: Fragment() {
    private val model: HomeViewModel by viewModels()
    ...
}
```

I en `ViewModel` wrapper man som regel variabler i [`LiveData`](https://developer.android.com/topic/libraries/architecture/livedata?hl=en):
```kotlin
 class HomeViewModel : ViewModel() {
    val position: MutableLiveData<LatLng> = MutableLiveData()
}
```

I fragmentet kan man deretter lytte på LiveData og oppdatere UIet når tilstanden endres:
```kotlin
homeModel.position.observe(viewLifecycleOwner, Observer<LatLng> { it: LatLng! ->
    marker?.remove()
    marker = map.addMarker(MarkerOptions().position(p))
})
```
Endringer i ViewModel tilstanden vil da automatisk oppdatere UIet:
```kotlin
override fun onMapClick(p0: LatLng?) {
        if (p0 == null)
            return
        model.position.value = p0 // Dette vil endre kartets marker automatisk
}
```

