package tech.skot.libraries.map

import tech.skot.core.components.SKComponentViewMock

class SKMapViewMock(
    mapInteractionSettingsInitial: SKMapVC.MapInteractionSettings,
    itemsInitial: List<SKMapVC.Marker>,
    linesInitial: List<SKMapVC.Polyline>,
    selectedMarkerInitial: SKMapVC.Marker?,
    selectMarkerOnClickInitial: Boolean,
    unselectMarkerOnMapClickInitial: Boolean,
    onMarkerClickedInitial: ((SKMapVC.Marker) -> Unit)?,
    onMapClickedInitial: ((Pair<Double, Double>) -> Unit)?,
    onMapLongClickedInitial: ((Pair<Double, Double>) -> Unit)?,
    onMarkerSelectedInitial: ((SKMapVC.Marker?) -> Unit)?,
    onMapBoundChangeInitial: ((SKMapVC.LatLngBounds) -> Unit)?,
    showLogInitial: Boolean
) : SKComponentViewMock(), InternalSKMapVC {
    override var markers: List<SKMapVC.Marker> = itemsInitial
    override var polylines: List<SKMapVC.Polyline> = linesInitial
    override var onMapClicked: ((Pair<Double, Double>) -> Unit)? = onMapClickedInitial
    override var onMapLongClicked: ((Pair<Double, Double>) -> Unit)? = onMapLongClickedInitial
    override var onMarkerClicked: ((SKMapVC.Marker) -> Unit)? = onMarkerClickedInitial
    override var onMarkerSelected: ((SKMapVC.Marker?) -> Unit)? = onMarkerSelectedInitial
    override var selectedMarker: SKMapVC.Marker? = selectedMarkerInitial
    override var selectMarkerOnClick: Boolean = selectMarkerOnClickInitial
    override var unselectMarkerOnMapClick: Boolean = unselectMarkerOnMapClickInitial
    override var showLog: Boolean = showLogInitial
    override var onMapBoundsChange: ((MapBounds) -> Unit)? = onMapBoundChangeInitial
    override var mapInteractionSettings: SKMapVC.MapInteractionSettings =
        mapInteractionSettingsInitial

    override fun setCameraPosition(
        position: Pair<Double, Double>,
        zoomLevel: Float,
        animate: Boolean
    ) {
        setCameraPositionCalls.add(SetCameraPositionCall(position, zoomLevel, animate))
    }

    val centerPositionsCalls = mutableListOf<CenterPositionsCall>()

    data class CenterPositionsCall(
        val positions: List<Pair<Double, Double>>
    )


    val setCameraPositionCalls = mutableListOf<SetCameraPositionCall>()

    data class SetCameraPositionCall(
        val position: Pair<Double, Double>,
        val zoomLevel: Float,
        val animate: Boolean
    )

    val setCameraZoomCalls = mutableListOf<SetCameraZoomCall>()

    data class SetCameraZoomCall(
        val zoomLevel: Float,
        val animate: Boolean
    )

    val getMapBoundsCalls = mutableListOf<GetMapBoundsCall>()

    data class GetMapBoundsCall(
        val onResult: ((SKMapVC.LatLngBounds) -> Unit)?
    )

    val getCurrentLocationCalls = mutableListOf<GetCurrentLocationCall>()

    data class GetCurrentLocationCall(
        val onResult: ((LatLng) -> Unit)?
    )

    val showMyLocationButtonCalls = mutableListOf<ShowMyLocationButtonCall>()

    data class ShowMyLocationButtonCall(
        val show: Boolean,
        val onPermissionError: (() -> Unit)?
    )

    override fun centerOnPositions(positions: List<Pair<Double, Double>>) {
        centerPositionsCalls.add(CenterPositionsCall(positions))
    }

    override fun setCameraZoom(zoomLevel: Float, animate: Boolean) {
        setCameraZoomCalls.add(SetCameraZoomCall(zoomLevel, animate))
    }

    override fun showMyLocationButton(show: Boolean, onPermissionError: (() -> Unit)?) {
        showMyLocationButtonCalls.add(ShowMyLocationButtonCall(show, onPermissionError))
    }

    override fun getMapBounds(onResult: (SKMapVC.LatLngBounds) -> Unit) {
        getMapBoundsCalls.add(GetMapBoundsCall(onResult))
    }

    override fun getCurrentLocation(onResult: (LatLng) -> Unit) {
        getCurrentLocationCalls.add(GetCurrentLocationCall(onResult))
    }

}