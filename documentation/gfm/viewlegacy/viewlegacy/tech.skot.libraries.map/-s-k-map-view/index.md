//[viewlegacy](../../../index.md)/[tech.skot.libraries.map](../index.md)/[SKMapView](index.md)

# SKMapView

[android]\
class [SKMapView](index.md)(proxy: [SKMapViewProxy](../-s-k-map-view-proxy/index.md), activity: SKActivity, fragment: [Fragment](https://developer.android.com/reference/kotlin/androidx/fragment/app/Fragment.html)?, mapView: MapView) : SKComponentView&lt;MapView&gt; , [SKMapRAI](../-s-k-map-r-a-i/index.md)

## Functions

| Name | Summary |
|---|---|
| [closeKeyboard](index.md#1428997254%2FFunctions%2F-2118544462) | [android]<br>fun [closeKeyboard](index.md#1428997254%2FFunctions%2F-2118544462)() |
| [displayError](index.md#-1092193227%2FFunctions%2F-2118544462) | [android]<br>fun [displayError](index.md#-1092193227%2FFunctions%2F-2118544462)(message: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) |
| [getLifecycle](index.md#-2126188895%2FFunctions%2F-2118544462) | [android]<br>open override fun [getLifecycle](index.md#-2126188895%2FFunctions%2F-2118544462)(): [Lifecycle](https://developer.android.com/reference/kotlin/androidx/lifecycle/Lifecycle.html) |
| [observe](index.md#665239928%2FFunctions%2F-2118544462) | [android]<br>fun &lt;[D](index.md#665239928%2FFunctions%2F-2118544462)&gt; SKLiveData&lt;[D](index.md#665239928%2FFunctions%2F-2118544462)&gt;.[observe](index.md#665239928%2FFunctions%2F-2118544462)(onChanged: ([D](index.md#665239928%2FFunctions%2F-2118544462)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html))<br>fun &lt;[D](index.md#2043339295%2FFunctions%2F-2118544462)&gt; SKMessage&lt;[D](index.md#2043339295%2FFunctions%2F-2118544462)&gt;.[observe](index.md#2043339295%2FFunctions%2F-2118544462)(onReceive: ([D](index.md#2043339295%2FFunctions%2F-2118544462)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)) |
| [onItems](on-items.md) | [android]<br>open override fun [onItems](on-items.md)(items: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[SKMapVC.Marker](../../../../viewcontract/viewcontract/tech.skot.libraries.map/-s-k-map-v-c/-marker/index.md)&gt;) |
| [onSelectedMarker](on-selected-marker.md) | [android]<br>open override fun [onSelectedMarker](on-selected-marker.md)(selectedMarker: [SKMapVC.Marker](../../../../viewcontract/viewcontract/tech.skot.libraries.map/-s-k-map-v-c/-marker/index.md)?) |
| [removeObservers](index.md#810891660%2FFunctions%2F-2118544462) | [android]<br>open fun [removeObservers](index.md#810891660%2FFunctions%2F-2118544462)() |
| [setCameraPosition](set-camera-position.md) | [android]<br>open override fun [setCameraPosition](set-camera-position.md)(position: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;, zoomLevel: [Float](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html), animate: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)) |
| [setTextColor](index.md#1463877402%2FFunctions%2F-2118544462) | [android]<br>fun [TextView](https://developer.android.com/reference/kotlin/android/widget/TextView.html).[setTextColor](index.md#1463877402%2FFunctions%2F-2118544462)(color: Color) |

## Properties

| Name | Summary |
|---|---|
| [activity](index.md#-63476570%2FProperties%2F-2118544462) | [android]<br>val [activity](index.md#-63476570%2FProperties%2F-2118544462): SKActivity |
| [binding](index.md#-75864896%2FProperties%2F-2118544462) | [android]<br>val [binding](index.md#-75864896%2FProperties%2F-2118544462): MapView |
| [collectObservers](index.md#-520305182%2FProperties%2F-2118544462) | [android]<br>var [collectObservers](index.md#-520305182%2FProperties%2F-2118544462): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [context](index.md#671965398%2FProperties%2F-2118544462) | [android]<br>val [context](index.md#671965398%2FProperties%2F-2118544462): [Context](https://developer.android.com/reference/kotlin/android/content/Context.html) |
| [mapView](map-view.md) | [android]<br>val [mapView](map-view.md): MapView |
| [proxy](proxy.md) | [android]<br>open override val [proxy](proxy.md): [SKMapViewProxy](../-s-k-map-view-proxy/index.md) |
