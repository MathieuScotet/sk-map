package tech.skot.libraries.map

import tech.skot.core.components.SKComponent
import tech.skot.libraries.map.di.skmapViewInjector

/**
 * # SKMap
 * ## A map component
 * Based on google Map
 * no iOS version at this time
 *
 * @param initialItems the [markers][SKMapVC.Marker] shown on the map
 * @param selectMarkerWhenClicked indicate if a marker must be selected when clicked
 * @param unselectMarkerWhenMapClicked indicate if the selected marker must be unselect when map is clicked
 * @param onMapClickedInitial nullable function type which allow to obtain the map click position
 */
class SKMap(
    initialItems: List<SKMapVC.Marker>,
    val selectMarkerWhenClicked: Boolean = true,
    val unselectMarkerWhenMapClicked: Boolean = true,
    onMapClickedInitial: ((Pair<Double, Double>) -> Unit)? = null,
    var onMarkerSelected: ((SKMapVC.Marker?) -> Unit)? = null
) : SKComponent<SKMapVC>() {


    var selectedMarker: SKMapVC.Marker?
        get() = view.selectedMarker
        set(value) {
            view.selectedMarker = value
            onMarkerSelected?.invoke(value)
        }



    var items: List<SKMapVC.Marker>
        get() = view.markers
        set(value) {
            view.markers = value
        }

    override val view: SKMapVC =
        skmapViewInjector.sKMap(
            itemsInitial = initialItems,
            onMapClickedInitial = null,
            selectedMarkerInitial = null,
                onMarkerClick = {
                it.onMarkerClick.invoke()
                if (selectMarkerWhenClicked) {
                    selectedMarker = it
                }
            }
        )

    init {
        setOnMapClicked(onMapClickedInitial)
    }


    /**
     * set new function type which allow to obtain the map click position
     * @param onMapClicked function type which allow to obtain the map click position
     */
    fun setOnMapClicked(onMapClicked: ((Pair<Double, Double>) -> Unit)?) {
        view.onMapClicked = if (unselectMarkerWhenMapClicked) {
            {
                onMapClicked?.invoke(it)
                selectedMarker = null
            }
        } else {
            onMapClicked
        }
    }

    /**
     * center map to show all markers currently added
     */
    fun centerOnMarkers(){
        centerOnPositions(items.toPositions())
    }

    /**
     * center map to show all positions
     */
    fun centerOnPositions(positions : List<Pair<Double, Double>>){
        view.centerOnPositions(positions)
    }


    /**
     *  function to call for moving camera on another location
     *  @param pos a [Pair] of [Double] representing the requested location for the center of the map
     *  @param zoomLevel a [Float] representing the zoom level to use
     *  @param animate true if the position change must be animated, false otherwise
     */
    fun setCameraPosition(pos: Pair<Double, Double>, zoomLevel: Float, animate: Boolean = true) {
        view.setCameraPosition(pos, zoomLevel, animate)
    }

    private fun List<SKMapVC.Marker>.toPositions() : List<Pair<Double, Double>> = map { it.position }

}
