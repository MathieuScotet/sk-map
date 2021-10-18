package tech.skot.libraries.map

import tech.skot.core.components.SKComponentVC
import tech.skot.core.components.SKLayoutIsSimpleView
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
     * data class representing a marker to show on the map
     * @param normalIcon the [Icon] to use when marker is not selected, null to use google mapView default icon
     * @param selectedIcon the [Icon] to use when marker is selected, null to use google mapView default icon
     * @param position a [Pair] of [Double] representing the location of the marker
     * @param onMarkerClick a function type called when marker is clicked
     */
    data class Marker(
        val title: String,
        val snippet: String?,
        val normalIcon: Icon?,
        val selectedIcon: Icon?,
        val position: Pair<Double, Double>,
        val onMarkerClick: () -> Unit
    )
}




