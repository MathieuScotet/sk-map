//[viewcontract](../../../index.md)/[tech.skot.libraries.map](../index.md)/[SKMapVC](index.md)

# SKMapVC

[common]\
interface [SKMapVC](index.md) : SKComponentVC

# SKMap

##  This component can be used if you want to show a map in your application

## Types

| Name | Summary |
|---|---|
| [Marker](-marker/index.md) | [common]<br>data class [Marker](-marker/index.md)(title: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), snippet: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?, normalIcon: Icon?, selectedIcon: Icon?, position: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;, onMarkerClick: () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html))<br>data class representing a marker to show on the map |

## Functions

| Name | Summary |
|---|---|
| [closeKeyboard](index.md#1601409822%2FFunctions%2F-1824869519) | [common]<br>abstract fun [closeKeyboard](index.md#1601409822%2FFunctions%2F-1824869519)() |
| [displayErrorMessage](index.md#717407720%2FFunctions%2F-1824869519) | [common]<br>abstract fun [displayErrorMessage](index.md#717407720%2FFunctions%2F-1824869519)(message: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) |
| [onRemove](index.md#-975149734%2FFunctions%2F-1824869519) | [common]<br>abstract fun [onRemove](index.md#-975149734%2FFunctions%2F-1824869519)() |
| [setCameraPosition](set-camera-position.md) | [common]<br>abstract fun [setCameraPosition](set-camera-position.md)(position: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;, zoomLevel: [Float](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html), animate: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))<br>function to call for moving camera on another location |

## Properties

| Name | Summary |
|---|---|
| [markers](markers.md) | [common]<br>abstract var [markers](markers.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[SKMapVC.Marker](-marker/index.md)&gt;<br>the list of [markers](-marker/index.md) shown on the map |
| [onMapClicked](on-map-clicked.md) | [common]<br>abstract var [onMapClicked](on-map-clicked.md): ([Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)?<br>a function type called when map clicked, can be null if map click is not used |
| [onMarkerClick](on-marker-click.md) | [common]<br>abstract val [onMarkerClick](on-marker-click.md): ([SKMapVC.Marker](-marker/index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [selectedMarker](selected-marker.md) | [common]<br>abstract var [selectedMarker](selected-marker.md): [SKMapVC.Marker](-marker/index.md)?<br>the currentSelected Marker, use it to select a marker instead of previous, or to unselect |
| [style](index.md#146464684%2FProperties%2F-1824869519) | [common]<br>abstract var [style](index.md#146464684%2FProperties%2F-1824869519): Style? |
