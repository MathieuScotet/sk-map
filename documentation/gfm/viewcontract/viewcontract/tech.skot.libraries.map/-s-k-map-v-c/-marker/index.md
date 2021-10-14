//[viewcontract](../../../../index.md)/[tech.skot.libraries.map](../../index.md)/[SKMapVC](../index.md)/[Marker](index.md)

# Marker

[common]\
data class [Marker](index.md)(title: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), snippet: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?, normalIcon: Icon?, selectedIcon: Icon?, position: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;, onMarkerClick: () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html))

data class representing a marker to show on the map

## Parameters

common

| | |
|---|---|
| normalIcon | the Icon to use when marker is not selected, null to use google mapView default icon |
| selectedIcon | the Icon to use when marker is selected, null to use google mapView default icon |
| position | a [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html) of [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) representing the location of the marker |
| onMarkerClick | a function type called when marker is clicked |

## Constructors

| | |
|---|---|
| [Marker](-marker.md) | [common]<br>fun [Marker](-marker.md)(title: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), snippet: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?, normalIcon: Icon?, selectedIcon: Icon?, position: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;, onMarkerClick: () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)) |

## Properties

| Name | Summary |
|---|---|
| [normalIcon](normal-icon.md) | [common]<br>val [normalIcon](normal-icon.md): Icon? |
| [onMarkerClick](on-marker-click.md) | [common]<br>val [onMarkerClick](on-marker-click.md): () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [position](position.md) | [common]<br>val [position](position.md): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt; |
| [selectedIcon](selected-icon.md) | [common]<br>val [selectedIcon](selected-icon.md): Icon? |
| [snippet](snippet.md) | [common]<br>val [snippet](snippet.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? |
| [title](title.md) | [common]<br>val [title](title.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
