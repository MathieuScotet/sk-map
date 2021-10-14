//[viewcontract](../../../../index.md)/[tech.skot.libraries.map](../../index.md)/[SKMapVC](../index.md)/[Marker](index.md)/[Marker](-marker.md)

# Marker

[common]\
fun [Marker](-marker.md)(title: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), snippet: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?, normalIcon: Icon?, selectedIcon: Icon?, position: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;, onMarkerClick: () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html))

## Parameters

common

| | |
|---|---|
| normalIcon | the Icon to use when marker is not selected, null to use google mapView default icon |
| selectedIcon | the Icon to use when marker is selected, null to use google mapView default icon |
| position | a [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html) of [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) representing the location of the marker |
| onMarkerClick | a function type called when marker is clicked |
