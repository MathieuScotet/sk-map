package tech.skot.libraries.map

import tech.skot.core.SKLog
import tech.skot.core.components.SKComponent
import tech.skot.core.di.get
import tech.skot.core.view.SKPermission
import tech.skot.libraries.map.di.skmapViewInjector
import tech.skot.libraries.map.view.Permissions

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
    var onMarkerSelected: ((SKMapVC.Marker?) -> Unit)? = null,

    ) : SKComponent<SKMapVC>() {

    val declaredPermissionHelper: DeclaredPermissionHelper = get()
    val permissions: Permissions = get()


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
    @Suppress("unused")
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
     * Show my location button
     * @param show : true to show my location button
     * @param askForPermissionIfNeeded true to ask for Permissions if needed. You can use it if permissions are not managed in screen
     * @param onPermissionError a callback fired if no location permissions is granted
     */
    @Suppress("unused")
    fun showMyLocationButton(
        show: Boolean = true,
        askForPermissionIfNeeded: Boolean = false,
        onPermissionError: (() -> Unit)? = null
    ) {
        val hasCoarsePermission =permissions.coarseLocation?.let { declaredPermissionHelper.isPermissionDeclaredForApp(it)} ?: false
        val hasFinePermission = permissions.fineLocation?.let { declaredPermissionHelper.isPermissionDeclaredForApp(it)}?: false
        val myLocationPermission = mutableListOf<SKPermission>()
        permissions.coarseLocation?.let { myLocationPermission.add(it) }
        permissions.fineLocation?.let { myLocationPermission.add(it) }


        val hasPermissionInManifest = hasCoarsePermission || hasFinePermission

        val hasAtLeastOnePermissionGranted = myLocationPermission.find {
            hasPermission(it)
        } != null

        SKLog.d("has permission in Manifest : $hasPermissionInManifest")
        SKLog.d("has permission Granted : $hasAtLeastOnePermissionGranted")
        SKLog.d("askForPermissionIfNeeded : $askForPermissionIfNeeded")


        when {
            !hasPermissionInManifest -> {
                onPermissionError?.invoke()
            }
            hasAtLeastOnePermissionGranted -> {
                view.showMyLocationButton(show, onPermissionError)
            }
            askForPermissionIfNeeded -> {
                SKLog.d("Request permission : ")
                requestPermissions(myLocationPermission) {
                    if (it.isNotEmpty()) {
                        SKLog.d("Requested permission : Granted")
                        view.showMyLocationButton(show, onPermissionError)
                    } else {
                        SKLog.d("Requested permission : Refused")
                        onPermissionError?.invoke()
                    }
                }
            }
            else -> {
                onPermissionError?.invoke()
            }
        }
    }


    /**
     * get current MapBounds
     * @param onResult, called once with current [MapBounds][SKMapVC.MapBounds]
     */
    @Suppress("unused")
    fun getMapBounds(onResult: (SKMapVC.MapBounds) -> Unit) {
        view.getMapBounds(onResult)
    }

    /**
     * called on MapBoundsChange when mapview is idle
     * @param onResult, called each time [MapBounds][SKMapVC.MapBounds] change
     */
    @Suppress("unused")
    fun onMapBoundsChange(onResult: (SKMapVC.MapBounds) -> Unit) {
        view.onMapBoundsChange(onResult)
    }


    /**
     * center map to show all markers currently added
     */
    @Suppress("unused")
    fun centerOnMarkers() {
        centerOnPositions(items.toPositions())
    }

    /**
     * change Camera zoom level with or without animation
     * @param zoomLevel the zoomLevel to apply
     * @param animate true to animate zoom change
     */
    @Suppress("unused")
    fun setCameraZoom(zoomLevel: Float, animate: Boolean) {
        view.setCameraZoom(zoomLevel, animate)
    }

    /**
     * center map to show all positions
     */
    @Suppress("unused")
    fun centerOnPositions(positions: List<Pair<Double, Double>>) {
        view.centerOnPositions(positions)
    }


    /**
     *  function to call for moving camera on another location
     *  @param pos a [Pair] of [Double] representing the requested location for the center of the map
     *  @param zoomLevel a [Float] representing the zoom level to use
     *  @param animate true if the position change must be animated, false otherwise
     */
    @Suppress("unused")
    fun setCameraPosition(pos: Pair<Double, Double>, zoomLevel: Float, animate: Boolean = true) {
        view.setCameraPosition(pos, zoomLevel, animate)
    }

    private fun List<SKMapVC.Marker>.toPositions(): List<Pair<Double, Double>> = map { it.position }

}
