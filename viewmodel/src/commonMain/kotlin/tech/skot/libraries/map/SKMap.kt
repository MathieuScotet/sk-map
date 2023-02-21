package tech.skot.libraries.map

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
 * @param mapInteractionSettingsInitial the [SKMapVC.MapInteractionSettings] to use for map. Any of [MapNormalInteractionSettings][SKMapVC.MapNormalInteractionSettings], [MapClusteringInteractionSettings][SKMapVC.MapClusteringInteractionSettings] or [MapCustomInteractionSettings][SKMapVC.MapCustomInteractionSettings]
 * @param markersInitial the [markers][SKMapVC.Marker] shown on the map
 * @param selectedMarkerInitial the first marker selected
 * @param selectMarkerOnClickInitial indicate if a marker must be selected when clicked
 * @param unselectMarkerOnMapClickInitial indicate if the selected marker must be unselect when map is clicked
 * @param onMarkerClickedInitial nullable function type which allow to obtain the clicked marker
 * @param onMapClickedInitial nullable function type which allow to obtain the map click position
 * @param onMarkerSelectedInitial nullable function type which allow to obtain the selected marker, or null on unselect
 * @param onMapBoundsChangeInitial nullable function type which allow to obtain the MapBounds on each change
 *
 */
@Suppress("unused")
class SKMap(
    mapInteractionSettingsInitial: SKMapVC.MapInteractionSettings = SKMapVC.MapNormalInteractionSettings,
    markersInitial: List<SKMapVC.Marker> = emptyList(),
    linesInitial: List<SKMapVC.Polyline> = emptyList(),
    polygonsInitial: List<SKMapVC.Polygon> = emptyList(),
    selectedMarkerInitial: SKMapVC.Marker? = null,
    selectMarkerOnClickInitial: Boolean = true,
    unselectMarkerOnMapClickInitial: Boolean = true,
    onMarkerClickedInitial: ((SKMapVC.Marker) -> Unit)? = null,
    onMapClickedInitial: ((LatLng) -> Unit)? = null,
    onMapLongClickedInitial: ((LatLng) -> Unit)? = null,
    onMarkerSelectedInitial: ((SKMapVC.Marker?) -> Unit)? = null,
    onMapBoundsChangeInitial: ((SKMapVC.LatLngBounds) -> Unit)? = null,
    showLogInitial: Boolean = false,
    mapTypeInitial: MapType = MapType.NORMAL

) : SKComponent<SKMapVC>() {

    private val declaredPermissionHelper: DeclaredPermissionHelper = get()
    private val permissions: Permissions = get()

    private val internalOnMapClicked: (LatLng) -> Unit = {
        onMapClicked?.invoke(it)
        if (internalView.unselectMarkerOnMapClick) {
            selectedMarker = null
        }
    }

    private val internalOnMapLongClicked: (LatLng) -> Unit = {
        onMapLongClicked?.invoke(it)
    }

    private val internalOnMarkerClicked: (SKMapVC.Marker) -> Unit = {
        onMarkerClicked?.invoke(it)
        it.onMarkerClick?.invoke()
        if (internalView.selectMarkerOnClick) {
            selectedMarker = it
        }
    }


    override val view: SKMapVC = skmapViewInjector.sKMap(
        mapInteractionSettingsInitial = mapInteractionSettingsInitial,
        markersInitial = markersInitial,
        linesInitial = linesInitial,
        polygonsInitial = polygonsInitial,
        selectedMarkerInitial = selectedMarkerInitial,
        selectMarkerOnClickInitial = selectMarkerOnClickInitial,
        unselectMarkerOnMapClickInitial = unselectMarkerOnMapClickInitial,
        onMarkerClickInitial = internalOnMarkerClicked,
        onMapClickedInitial = internalOnMapClicked,
        onMapLongClickedInitial = internalOnMapLongClicked,
        onMarkerSelectedInitial = onMarkerSelectedInitial,
        onMapBoundsChangeInitial = onMapBoundsChangeInitial,
        showLogInitial = showLogInitial,
        mapType = mapTypeInitial
    )

    var mapType: MapType
        get() = view.mapType
        set(value) {
            view.mapType = value
        }


    init {
        MapLogger.enabled = showLogInitial
    }

    private val internalView = view as InternalSKMapVC

    var mapInteractionSettings: SKMapVC.MapInteractionSettings
        get() = internalView.mapInteractionSettings
        set(value) {
            internalView.mapInteractionSettings = value
        }

    var selectMarkerOnClick: Boolean
        get() = internalView.selectMarkerOnClick
        set(value) {
            internalView.selectMarkerOnClick = value
        }

    /**
     * called on marker click
     * @see SKMapVC.Marker.onMarkerClick
     */
    @Suppress("unused")
    var onMarkerClicked: ((SKMapVC.Marker) -> Unit)? = onMarkerClickedInitial


    var showLog: Boolean
        get() = view.showLog
        set(value) {
            view.showLog = value
        }


    var unselectMarkerOnMapClick: Boolean
        get() = internalView.unselectMarkerOnMapClick
        set(value) {
            internalView.unselectMarkerOnMapClick = value
        }

    /**
     * called on map click
     */
    @Suppress("unused")
    var onMapClicked: ((LatLng) -> Unit)? = onMapClickedInitial

    /**
     * called on map long click
     */
    @Suppress("unused")
    var onMapLongClicked: ((LatLng) -> Unit)? = onMapLongClickedInitial


    var onMarkerSelected: ((SKMapVC.Marker?) -> Unit)?
        get() = internalView.onMarkerSelected
        set(value) {
            internalView.onMarkerSelected = value
        }

    /**
     * list of markers
     */
    @Suppress("unused")
    var markers: List<SKMapVC.Marker>
        get() = view.markers
        set(value) {
            view.markers = value
        }

    /**
     * list of lines
     */
    @Suppress("unused")
    var polylines: List<SKMapVC.Polyline>
        get() = view.polylines
        set(value) {
            view.polylines = value
        }

    /**
     * list of lines
     */
    @Suppress("unused")
    var polygons: List<SKMapVC.Polygon>
        get() = view.polygons
        set(value) {
            view.polygons = value
        }


    /**
     * current selected Marker, null if no marker selected
     */
    @Suppress("unused")
    var selectedMarker: SKMapVC.Marker?
        get() = internalView.selectedMarker
        set(value) {
            internalView.selectedMarker = value
            onMarkerSelected?.invoke(value)
        }

    /**
     *  called each time [MapBounds][SKMapVC.LatLngBounds] change (when mapview is idle)
     */
    @Suppress("unused")
    var onMapBoundsChange: ((MapBounds) -> Unit)?
        get() = view.onMapBoundsChange
        set(value) {
            view.onMapBoundsChange = value
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
        val hasCoarsePermission =
            permissions.coarseLocation?.let { declaredPermissionHelper.isPermissionDeclaredForApp(it) }
                ?: false
        val hasFinePermission =
            permissions.fineLocation?.let { declaredPermissionHelper.isPermissionDeclaredForApp(it) }
                ?: false
        val myLocationPermission = mutableListOf<SKPermission>()
        permissions.coarseLocation?.let { myLocationPermission.add(it) }
        permissions.fineLocation?.let { myLocationPermission.add(it) }


        val hasPermissionInManifest = hasCoarsePermission || hasFinePermission

        val hasAtLeastOnePermissionGranted = myLocationPermission.find {
            hasPermission(it)
        } != null

        MapLogger.d("has permission in Manifest : $hasPermissionInManifest")
        MapLogger.d("has permission Granted : $hasAtLeastOnePermissionGranted")
        MapLogger.d("askForPermissionIfNeeded : $askForPermissionIfNeeded")


        when {
            !hasPermissionInManifest -> {
                onPermissionError?.invoke()
            }
            hasAtLeastOnePermissionGranted -> {
                view.showMyLocationButton(show, onPermissionError)
            }
            askForPermissionIfNeeded -> {
                MapLogger.d("Request permission : ")
                requestPermissions(myLocationPermission) {
                    if (it.isNotEmpty()) {
                        MapLogger.d("Requested permission : Granted")
                        view.showMyLocationButton(show, onPermissionError)
                    } else {
                        MapLogger.d("Requested permission : Refused")
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
     * Show my location  on map
     * @param show : true to show my location on map
     * @param askForPermissionIfNeeded true to ask for Permissions if needed. You can use it if permissions are not managed in screen
     * @param onPermissionError a callback fired if no location permissions is granted
     */
    @Suppress("unused")
    fun showMyLocation(
        show: Boolean = true,
        askForPermissionIfNeeded: Boolean = false,
        onPermissionError: (() -> Unit)? = null
    ) {
        val hasCoarsePermission =
            permissions.coarseLocation?.let { declaredPermissionHelper.isPermissionDeclaredForApp(it) }
                ?: false
        val hasFinePermission =
            permissions.fineLocation?.let { declaredPermissionHelper.isPermissionDeclaredForApp(it) }
                ?: false
        val myLocationPermission = mutableListOf<SKPermission>()
        permissions.coarseLocation?.let { myLocationPermission.add(it) }
        permissions.fineLocation?.let { myLocationPermission.add(it) }


        val hasPermissionInManifest = hasCoarsePermission || hasFinePermission

        val hasAtLeastOnePermissionGranted = myLocationPermission.find {
            hasPermission(it)
        } != null

        MapLogger.d("has permission in Manifest : $hasPermissionInManifest")
        MapLogger.d("has permission Granted : $hasAtLeastOnePermissionGranted")
        MapLogger.d("askForPermissionIfNeeded : $askForPermissionIfNeeded")


        when {
            !hasPermissionInManifest -> {
                onPermissionError?.invoke()
            }
            hasAtLeastOnePermissionGranted -> {
                view.showMyLocation(show, onPermissionError)
            }
            askForPermissionIfNeeded -> {
                MapLogger.d("Request permission : ")
                requestPermissions(myLocationPermission) {
                    if (it.isNotEmpty()) {
                        MapLogger.d("Requested permission : Granted")
                        view.showMyLocation(show, onPermissionError)
                    } else {
                        MapLogger.d("Requested permission : Refused")
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
     * @param onResult called once with current [MapBounds][SKMapVC.LatLngBounds]
     */
    @Suppress("unused")
    fun getMapBounds(onResult: (MapBounds) -> Unit) {
        view.getMapBounds(onResult)
    }

    /**
     * center map to show all markers currently added
     */
    @Suppress("unused")
    fun centerOnMarkers() {
        centerOnPositions(markers.toPositions())
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
    fun centerOnPositions(positions: List<LatLng>) {
        view.centerOnPositions(positions)
    }

    /**
     *  function to call for moving camera on another location
     *  @param pos a [Pair] of [LatLng] representing the requested location for the center of the map
     *  @param zoomLevel a [Float] representing the zoom level to use
     *  @param animate true if the position change must be animated, false otherwise
     */
    @Suppress("unused")
    fun setCameraPosition(pos: LatLng, zoomLevel: Float, animate: Boolean = true) {
        view.setCameraPosition(pos, zoomLevel, animate)
    }

    /**
     * getCurrent user Location
     * @param onResult Lambda called with LatLng position in param
     */
    fun getCurrentLocation(onResult: (LatLng) -> Unit) {
        view.getCurrentLocation(onResult)
    }

    private fun List<SKMapVC.Marker>.toPositions(): List<LatLng> = map { it.position }

}