# Fragments

## Aksess til child views i `onCreateView`

I en `Activity`kan man referere til elementer i `onCreate` ved å bruke id navnet
direkte (automatisk import). Dette fungerer ikke i fragmenter, der må man bruke
`root.idNavn` i `onCreateView` da `this` først blir popuplert med idene etter at
`onCreateView` har returnert `root.`

