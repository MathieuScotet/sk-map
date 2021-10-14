//[viewmodel](../../../index.md)/[tech.skot.libraries.map](../index.md)/[SKMap](index.md)

# SKMap

[common]\
class [SKMap](index.md)(initialItems: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[SKMapVC.Marker](../../../../viewcontract/viewcontract/tech.skot.libraries.map/-s-k-map-v-c/-marker/index.md)&gt;, selectMarkerWhenClicked: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), unselectMarkerWhenMapClicked: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), onMapClickedInitial: ([Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)?) : SKComponent&lt;[SKMapVC](../../../../viewcontract/viewcontract/tech.skot.libraries.map/-s-k-map-v-c/index.md)&gt; 

# SKMap

##  A map component

Based on google Map no iOS version at this time

## Parameters

common

| | |
|---|---|
| initialItems | the [markers](../../../../viewcontract/viewcontract/tech.skot.libraries.map/-s-k-map-v-c/-marker/index.md) shown on the map |
| selectMarkerWhenClicked | indicate if a marker must be selected when clicked |
| unselectMarkerWhenMapClicked | indicate if the selected marker must be unselect when map is clicked |
| onMapClickedInitial | nullable function type which allow to obtain the map click position |

## Constructors

| | |
|---|---|
| [SKMap](-s-k-map.md) | [common]<br>fun [SKMap](-s-k-map.md)(initialItems: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[SKMapVC.Marker](../../../../viewcontract/viewcontract/tech.skot.libraries.map/-s-k-map-v-c/-marker/index.md)&gt;, selectMarkerWhenClicked: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true, unselectMarkerWhenMapClicked: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true, onMapClickedInitial: ([Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)? = null) |

## Functions

| Name | Summary |
|---|---|
| [setCameraPosition](set-camera-position.md) | [common]<br>fun [setCameraPosition](set-camera-position.md)(pos: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;, zoomLevel: [Float](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html), animate: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true)<br>function to call for moving camera on another location |
| [setOnMapClicked](set-on-map-clicked.md) | [common]<br>fun [setOnMapClicked](set-on-map-clicked.md)(onMapClicked: ([Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)?)<br>set new function type which allow to obtain the map click position |

## Properties

| Name | Summary |
|---|---|
| [items](items.md) | [common]<br>var [items](items.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[SKMapVC.Marker](../../../../viewcontract/viewcontract/tech.skot.libraries.map/-s-k-map-v-c/-marker/index.md)&gt; |
| [selectedMarker](selected-marker.md) | [common]<br>var [selectedMarker](selected-marker.md): [SKMapVC.Marker](../../../../viewcontract/viewcontract/tech.skot.libraries.map/-s-k-map-v-c/-marker/index.md)? |
| [selectMarkerWhenClicked](select-marker-when-clicked.md) | [common]<br>val [selectMarkerWhenClicked](select-marker-when-clicked.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true |
| [unselectMarkerWhenMapClicked](unselect-marker-when-map-clicked.md) | [common]<br>val [unselectMarkerWhenMapClicked](unselect-marker-when-map-clicked.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true |
| [view](view.md) | [common]<br>open override val [view](view.md): [SKMapVC](../../../../viewcontract/viewcontract/tech.skot.libraries.map/-s-k-map-v-c/index.md) |
