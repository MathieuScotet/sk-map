//[viewmodel](../../../index.md)/[tech.skot.libraries.map](../index.md)/[SKMap](index.md)/[setCameraPosition](set-camera-position.md)

# setCameraPosition

[common]\
fun [setCameraPosition](set-camera-position.md)(pos: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;, zoomLevel: [Float](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html), animate: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true)

function to call for moving camera on another location

## Parameters

common

| | |
|---|---|
| pos | a [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html) of [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) representing the requested location for the center of the map |
| zoomLevel | a [Float](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) representing the zoom level to use |
| animate | true if the position change must be animated, false otherwise |
