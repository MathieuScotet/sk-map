package tech.skot.libraries.map

import tech.skot.core.components.SKComponentVC
import tech.skot.core.components.SKLayoutIsSimpleView
import tech.skot.core.view.Color
import tech.skot.core.view.Dimen
import tech.skot.core.view.Icon
import tech.skot.libraries.map.SKMapVC.MapInteractionSettings
import tech.skot.libraries.map.SKMapVC.Marker


/**
 * # SKMap
 * ## This component can be used if you want to show a map in your application
 * @property markers the list of [markers][Marker] shown on the map
 * @property mapInteractionSettings [MapInteractionSettings][SKMapVC.MapInteractionSettings] to use on map, Any of [MapNormalInteractionSettings][SKMapVC.MapNormalInteractionSettings], [MapClusteringInteractionSettings][SKMapVC.MapClusteringInteractionSettings] or [MapCustomInteractionSettings][SKMapVC.MapCustomInteractionSettings]
 * @property selectedMarker the currentSelected Marker, use it to select a marker instead of previous, or to unselect
 * @property onMapBoundsChange called each time [MapBounds][SKMapVC.LatLngBounds] change (when mapview is idle)
 * @property mapType the type of tiles shown [MapType]
 *
 */
@SKLayoutIsSimpleView
interface SKMapVC : SKComponentVC {

    var showLog: Boolean
    var markers: List<Marker>
    var polylines: List<Polyline>
    var polygons: List<Polygon>
    var onMapBoundsChange: ((MapBounds) -> Unit)?
    var mapInteractionSettings: MapInteractionSettings
    var mapType: MapType

    /**
     *  function to call for moving camera on another location
     *  @param pos a [Pair] of [Double] representing the requested location for the center of the map
     *  @param zoomLevel a [Float] representing the zoom level to use
     *  @param animate true if the position change must be animated, false otherwise
     */
    fun setCameraPosition(position: LatLng, zoomLevel: Float, animate: Boolean)

    /**
     *  function to call for moving camera to show all positions of the list
     *  @param positions list of [Pair] describing the Lat Lng of positions
     */
    fun centerOnPositions(positions: List<LatLng>)

    /**
     * change Camera zoom level with or without animation
     * @param zoomLevel the zoomLevel to apply
     * @param animate true to animate zoom change
     */
    fun setCameraZoom(zoomLevel: Float, animate: Boolean)

    /**
     * Show my location button
     * @param show : true to show my location button
     * @param onPermissionError a callback fired if no location permissions is granted
     */
    fun showMyLocationButton(show: Boolean, onPermissionError: (() -> Unit)?)


    /**
     * Show my location
     * @param show : true to show my location on map
     * @param onPermissionError a callback fired if no location permissions is granted
     */
    fun showMyLocation(show: Boolean, onPermissionError: (() -> Unit)?)

    /**
     * get current MapBounds
     * @param onResult, called once with current [MapBounds][SKMapVC.LatLngBounds]
     */
    fun getMapBounds(onResult: (SKMapVC.LatLngBounds) -> Unit)


    fun getCurrentLocation(onResult: (LatLng) -> Unit)


    /**
     * data class representing a connected series of line segments.
     * @param points sequence of [point][LatLng]
     * @param color color of polyline
     * @param id unique id for the polyline
     * @param lineWidth, [Dimen] representing width of the line
     * @see [tech.skot.core.view.DimenDP]
     * @see [tech.skot.core.view.DimenRef]
     */
    data class Polyline(
        val points: List<LatLng>,
        val color: Color,
        val id: String?,
        val lineWidth: Dimen,
        val hidden: Boolean = false
    )

    /**
     * data class representing a polygon.
     * @param points sequence of [point][LatLng]
     * @param color color of polyline
     * @param id unique id for the polyline
     * @param lineWidth, [Dimen] representing width of the line
     * @see [tech.skot.core.view.DimenDP]
     * @see [tech.skot.core.view.DimenRef]
     */
    data class Polygon(
        val points: List<LatLng>,
        val fillColor: Color,
        val strokeColor: Color,
        val id: String?,
        val lineWidth: Dimen,
        val holes: List<List<LatLng>>,
        val hidden: Boolean = false
    )


    /**
     * data class representing a marker to show on the map
     * @param id unique id for the marker, used to update position
     * @param position a [Pair] of [Double] representing the location of the marker
     * @param onMarkerClick a function type called when marker is clicked
     */
    sealed class Marker(
        open val id: String?,
        open val position: LatLng,
        open val onMarkerClick: (() -> Unit)?,
        open val iconHash: (selected: Boolean) -> String,
        open val hidden: Boolean = false
    )

    /**
     * data class representing a marker to show on map as an icon
     * @param itemId unique id for the marker, used to update position
     * @param position a [Pair] of [Double] representing the location of the marker
     * @param onMarkerClick a function type called when marker is clicked
     * @param normalIcon the [Icon] to use when marker is not selected
     * @param selectedIcon the [Icon] to use when marker is selected
     * @see Marker
     * @see ColorizedIconMarker
     * @see CustomMarker
     */
    data class IconMarker(
        override val id: String?,
        val normalIcon: Icon,
        val selectedIcon: Icon,
        override val position: LatLng,
        override val onMarkerClick: (() -> Unit)? = null,
        override val hidden: Boolean = false
    ) : Marker(
        id = id,
        position = position,
        onMarkerClick = onMarkerClick,
        iconHash = { selected ->
            "normal_${if (selected) selectedIcon.toString() else normalIcon.toString()}"
        }
    )

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
    data class ColorizedIconMarker(
        override val id: String?,
        val icon: Icon,
        val normalColor: Color,
        val selectedColor: Color,
        override val position: LatLng,
        override val onMarkerClick: (() -> Unit)? = null,
        override val hidden: Boolean = false
    ) : Marker(id, position, onMarkerClick, { selected ->
        "colorized_${icon}_${if (selected) normalColor.toString() else selectedColor.toString()}"
    })

    /**
     * data class representing a marker to show on map.
     * icon is colorized when selected state change
     * @param itemId unique id for the marker, used to update position
     * @param position a [Pair] of [Double] representing the location of the marker
     * @param onMarkerClick a function type called when marker is clicked
     * @param data objet that you can use in screenView Code to create marker as you want in the implementation of  SKMapView.onCreateCustomMarkerIcon lambda
     * @param iconHash [String], Used for bitmap cache. Must be unique for each different bitmap, and the same for same bitmap. Set it with caution, if you use any irrelevant parameter to know which bitmap to use, cache of Bitmap will be inefficient
     * @see Marker
     * @see ColorizedIconMarker
     * @see IconMarker
     */
    class CustomMarker(
        override val id: String?,
        val data: Any,
        override val position: LatLng,
        override val onMarkerClick: (() -> Unit)? = null,
        override val iconHash: (selected: Boolean) -> String,
        override val hidden: Boolean = false
    ) : Marker(id, position, onMarkerClick, iconHash)

    /**
     * describe MapBounds
     * @param northeast a [Pair] describing the Lat Lng of northeast point
     * @param southwest a [Pair] describing the Lat Lng of southwest point
     */
    data class LatLngBounds(
        val northeast: LatLng,
        val southwest: LatLng,
    )

    sealed class MapInteractionSettings
    class MapClusteringInteractionSettings(
        /**
         * Lambda with List<Marker> in parameter, called when a cluster is clicked
         */
        val onClusterClick: ((markers: List<Marker>) -> Unit)?,
        /**
         * Lambda with clusterSize in parameter, return color of cluster
         * by default, variety of blue in conjonction with cluster size
         */
        val getClusterColor: ((clusterSize: Int) -> Color)? = null,
        /**
         * Lambda with clusterSize in parameter, return text of cluster
         * by default [0..9] 10+, 20+, 50+, 100+, 200+, 500+, 1000+
         */
        val getClusterText: ((clusterSize: Int) -> String)? = null,
        /**
         * Lambda with clusterSize in parameter, return bucket id (used to reused same cluster for same size range)
         * by default [0..9] 10, 20, 50, 100, 200, 500, 1000
         */
        val getBucket: ((clusterSize: Int) -> Int)? = null,
        /**
         * Lambda to set min cluster size (4 by default)
         */
        val getMinClusterSize: (() -> Int)? = null
    ) : MapInteractionSettings()

    class MapCustomInteractionSettings(val customRef: Int, val data: Any?) :
        MapInteractionSettings()

    object MapNormalInteractionSettings : MapInteractionSettings()
}


sealed class MapType {
    object NORMAL : MapType()
    object SATELLITE : MapType()
    object HYBRID : MapType()
    object TERRAIN : MapType()
    object NONE : MapType()
    data class CUSTOM(val uri: String) : MapType()

}

interface InternalSKMapVC : SKMapVC {
    var selectMarkerOnClick: Boolean
    var unselectMarkerOnMapClick: Boolean
    var onMapClicked: ((LatLng) -> Unit)?
    var onMapLongClicked: ((LatLng) -> Unit)?
    var onMarkerClicked: ((Marker) -> Unit)?
    var onMarkerSelected: ((Marker?) -> Unit)?
    var selectedMarker: Marker?
}

typealias MapBounds = SKMapVC.LatLngBounds
typealias LatLng = Pair<Double, Double>