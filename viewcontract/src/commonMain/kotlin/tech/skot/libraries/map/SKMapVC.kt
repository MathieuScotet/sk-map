package tech.skot.libraries.map

import tech.skot.core.components.SKComponentVC
import tech.skot.core.components.SKLayoutIsSimpleView
import tech.skot.core.view.Color
import tech.skot.core.view.Icon


/**
 * # SKMap
 * ## This component can be used if you want to show a map in your application
 * @property markers the list of [markers][Marker] shown on the map
 * @property onMapClicked a function type called when map clicked, can be null if map click is not used
 * @property selectedMarker the currentSelected Marker, use it to select a marker instead of previous, or to unselect
 *
 */
@SKLayoutIsSimpleView
interface SKMapVC : SKComponentVC {
    var markers: List<Marker>
    var onMapClicked: ((Pair<Double, Double>) -> Unit)?
    val onMarkerClick : (Marker) -> Unit
    var selectedMarker: Marker?


    /**
     *  function to call for moving camera on another location
     *  @param pos a [Pair] of [Double] representing the requested location for the center of the map
     *  @param zoomLevel a [Float] representing the zoom level to use
     *  @param animate true if the position change must be animated, false otherwise
     */
    fun setCameraPosition(position: Pair<Double, Double>, zoomLevel: Float, animate: Boolean)

    /**
     *  function to call for moving camera to show all positions of the list
     *  @param positions list of [Pair] describing the Lat Lng of positions
     */
    fun centerOnPositions(positions : List<Pair<Double, Double>>)

    /**
     * change Camera zoom level with or without animation
     * @param zoomLevel the zoomLevel to apply
     * @param animate true to animate zoom change
     */
    fun setCameraZoom(zoomLevel : Float, animate: Boolean)

    /**
     * Show my location button
     * @param show : true to show my location button
     * @param onPermissionError a callback fired if no location permissions is granted
     */
    fun showMyLocationButton(show : Boolean, onPermissionError : (() -> Unit)?)

    /**
     * get current MapBounds
     * @param onResult, called once with current [MapBounds][SKMapVC.MapBounds]
     */
    fun getMapBounds(onResult : (SKMapVC.MapBounds) -> Unit)

    /**
     * called on MapBoundsChange when mapview is idle
     * @param onResult, called each time [MapBounds][SKMapVC.MapBounds] change
     */
    fun onMapBoundsChange(onResult : ((SKMapVC.MapBounds) -> Unit)?)



    /**
     * data class representing a marker to show on the map
     * @param itemId unique id for the marker, used to update position
     * @param position a [Pair] of [Double] representing the location of the marker
     * @param onMarkerClick a function type called when marker is clicked
     */
    sealed class Marker(
        open val itemId : String?,
        open val position: Pair<Double, Double>,
        open val onMarkerClick: () -> Unit
    )

    /**
     * data class representing a marker to show on map as an icon
     * @param itemId unique id for the marker, used to update position
     * @param position a [Pair] of [Double] representing the location of the marker
     * @param onMarkerClick a function type called when marker is clicked
     * @param normalIcon the [Icon] to use when marker is not selected, null to use google mapView default icon
     * @param selectedIcon the [Icon] to use when marker is selected, null to use google mapView default icon
     * @see Marker
     * @see ColorizedIconMarker
     * @see CustomMarker
     */
    open class IconMarker(
        override val itemId : String?,
        open val normalIcon: Icon,
        open val selectedIcon: Icon,
        override val position: Pair<Double, Double>,
        override val onMarkerClick: () -> Unit
    ) : Marker(itemId, position, onMarkerClick)


    /**
     * data class representing a marker to show on map.
     * icon is colorized when selected state change
     * @param itemId unique id for the marker, used to update position
     * @param position a [Pair] of [Double] representing the location of the marker
     * @param onMarkerClick a function type called when marker is clicked
     * @param icon the [Icon] to use for the marker
     * @param normalColor the [Color] to used when marker is not selected
     * @param selectedColor the [Color] to used when marker is selected
     * @see Marker
     * @see IconMarker
     * @see CustomMarker
     */
    class ColorizedIconMarker(
        override val itemId : String?,
        val icon: Icon,
        val normalColor : Color,
        val selectedColor : Color,
        override val position: Pair<Double, Double>,
        override val onMarkerClick: () -> Unit
    ) : Marker(itemId, position, onMarkerClick)

    /**
     * data class representing a marker to show on map.
     * icon is colorized when selected state change
     * @param itemId unique id for the marker, used to update position
     * @param position a [Pair] of [Double] representing the location of the marker
     * @param onMarkerClick a function type called when marker is clicked
     * @param data objet that you can use in screenView Code to create marker as you want in the implementation of  SKMapView.onCreateCustomMarkerIcon lambda
     * @see Marker
     * @see ColorizedIconMarker
     * @see IconMarker
     */
    class   CustomMarker(
        override val itemId : String?,
        val data : Any,
        override val position: Pair<Double, Double>,
        override val onMarkerClick: () -> Unit
    ) : Marker(itemId, position, onMarkerClick)


    /**
     * describe MapBounds
     * @param northeast a [Pair] describing the Lat Lng of the map northeast point
     * @param southwest a [Pair] describing the Lat Lng of the map southwest point
     */
    data class MapBounds(
        val northeast : Pair<Double, Double>,
        val southwest : Pair<Double, Double>,
    )

}




