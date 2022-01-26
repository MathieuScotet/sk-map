package tech.skot.libraries.map

import tech.skot.core.components.SKComponentViewMock

class SKMapViewMock(override val onMarkerClick: (SKMapVC.Marker) -> Unit,
                     itemsInitial: List<SKMapVC.Marker>,
                     onMapClickedInitial: ((Pair<Double, Double>) -> Unit)?,
                     selectedMarkerInitial: SKMapVC.Marker?) : SKComponentViewMock(), SKMapVC {
    override var markers: List<SKMapVC.Marker> = itemsInitial
    override var onMapClicked: ((Pair<Double, Double>) -> Unit)? = onMapClickedInitial
    override var selectedMarker: SKMapVC.Marker? = selectedMarkerInitial

    override fun setCameraPosition(
        position: Pair<Double, Double>,
        zoomLevel: Float,
        animate: Boolean
    ) {
        setCameraPositionCalls.add(SetCameraPositionCall(position, zoomLevel, animate))
    }

    val centerPositionsCalls = mutableListOf<CenterPositionsCall>()
    data class CenterPositionsCall(
        val positions : List<Pair<Double, Double>>
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
        val onResult: ((SKMapVC.MapBounds) -> Unit)?
    )

    val onMapBoundsChangeCalls = mutableListOf<OnMapBoundsChangeCall>()
    data class OnMapBoundsChangeCall(
        val onResult: ((SKMapVC.MapBounds) -> Unit)?
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

    override fun getMapBounds(onResult: (SKMapVC.MapBounds) -> Unit) {
        getMapBoundsCalls.add(GetMapBoundsCall(onResult))
    }

    override fun onMapBoundsChange(onResult: ((SKMapVC.MapBounds) -> Unit)?) {
        onMapBoundsChangeCalls.add(OnMapBoundsChangeCall(onResult))
    }
}