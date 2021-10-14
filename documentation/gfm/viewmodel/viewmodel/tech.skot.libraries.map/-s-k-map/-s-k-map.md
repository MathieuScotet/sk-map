//[viewmodel](../../../index.md)/[tech.skot.libraries.map](../index.md)/[SKMap](index.md)/[SKMap](-s-k-map.md)

# SKMap

[common]\
fun [SKMap](-s-k-map.md)(initialItems: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[SKMapVC.Marker](../../../../viewcontract/viewcontract/tech.skot.libraries.map/-s-k-map-v-c/-marker/index.md)&gt;, selectMarkerWhenClicked: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true, unselectMarkerWhenMapClicked: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true, onMapClickedInitial: ([Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)? = null)

## Parameters

common

| | |
|---|---|
| initialItems | the [markers](../../../../viewcontract/viewcontract/tech.skot.libraries.map/-s-k-map-v-c/-marker/index.md) shown on the map |
| selectMarkerWhenClicked | indicate if a marker must be selected when clicked |
| unselectMarkerWhenMapClicked | indicate if the selected marker must be unselect when map is clicked |
| onMapClickedInitial | nullable function type which allow to obtain the map click position |
