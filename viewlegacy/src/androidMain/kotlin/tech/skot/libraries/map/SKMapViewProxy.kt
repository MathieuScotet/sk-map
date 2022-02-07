package tech.skot.libraries.map


import androidx.fragment.app.Fragment
import com.google.android.gms.maps.MapView
import tech.skot.core.components.SKActivity
import tech.skot.core.components.SKComponentViewProxy
import tech.skot.view.live.MutableSKLiveData
import tech.skot.view.live.SKMessage

class SKMapViewProxy(
    mapInteractionSettingsInitial: SKMapVC.MapInteractionSettings,
    itemsInitial: List<SKMapVC.Marker>,
    selectedMarkerInitial: SKMapVC.Marker?,
    selectMarkerOnClickInitial: Boolean,
    unselectMarkerOnMapClickInitial: Boolean,
    onMarkerClickInitial: Function1<SKMapVC.Marker, Unit>?,
    onMarkerSelectedInitial: Function1<SKMapVC.Marker?, Unit>?,
    onMapClickedInitial: Function1<LatLng, Unit>?,
    onMapBoundsChangeInitial: Function1<SKMapVC.LatLngBounds, Unit>?
) : SKComponentViewProxy<MapView>(), InternalSKMapVC {

    private val mapInteractionSettingsLD: MutableSKLiveData<SKMapVC.MapInteractionSettings> = MutableSKLiveData(mapInteractionSettingsInitial)
    override var mapInteractionSettings: SKMapVC.MapInteractionSettings by mapInteractionSettingsLD

    private val itemsLD: MutableSKLiveData<List<SKMapVC.Marker>> = MutableSKLiveData(itemsInitial)
    override var markers: List<SKMapVC.Marker> by itemsLD

    private val onMapClickedLD: MutableSKLiveData<Function1<LatLng, Unit>?> =
        MutableSKLiveData(onMapClickedInitial)
    override var onMapClicked: Function1<LatLng, Unit>? by onMapClickedLD


    private val onMarkerClickLD: MutableSKLiveData<Function1<SKMapVC.Marker, Unit>?> =
        MutableSKLiveData(onMarkerClickInitial)
    override var onMarkerClicked: Function1<SKMapVC.Marker, Unit>? by onMarkerClickLD


    private val onMapBoundsChangeLD: MutableSKLiveData<Function1<SKMapVC.LatLngBounds, Unit>?> =
        MutableSKLiveData(onMapBoundsChangeInitial)
    override var onMapBoundsChange: Function1<SKMapVC.LatLngBounds, Unit>? by onMapBoundsChangeLD

    private val selectMarkerOnClickLD: MutableSKLiveData<Boolean> =
        MutableSKLiveData(selectMarkerOnClickInitial)
    override var selectMarkerOnClick: Boolean by selectMarkerOnClickLD

    private val unselectMarkerOnMapClickLD: MutableSKLiveData<Boolean> =
        MutableSKLiveData(unselectMarkerOnMapClickInitial)
    override var unselectMarkerOnMapClick: Boolean by unselectMarkerOnMapClickLD


    private val onMarkerSelectedLD: MutableSKLiveData<Function1<SKMapVC.Marker?, Unit>?> =
        MutableSKLiveData(onMarkerSelectedInitial)
    override var onMarkerSelected: Function1<SKMapVC.Marker?, Unit>? by onMarkerSelectedLD

    private val selectedMarkerLD: MutableSKLiveData<SKMapVC.Marker?> =
        MutableSKLiveData(selectedMarkerInitial)
    override var selectedMarker: SKMapVC.Marker? by selectedMarkerLD

    private val setCameraPositionMessage: SKMessage<SetCameraPositionData> = SKMessage()
    private val setCenterOnPositionsMessage: SKMessage<CenterOnPositionsData> = SKMessage()
    private val setCameraZoomMessage: SKMessage<SetCameraZoomData> = SKMessage()
    private val showMyLocationButtonMessage: SKMessage<ShowMyLocationButtonData> = SKMessage()
    private val getMapBoundsMessage: SKMessage<GetMapBoundsData> = SKMessage()

    override fun showMyLocationButton(
        show: Boolean,
        onPermissionError: (() -> Unit)?
    ) {
        showMyLocationButtonMessage.post(
            ShowMyLocationButtonData(
                show,
                onPermissionError
            )
        )
    }

    override fun getMapBounds(onResult: (SKMapVC.LatLngBounds) -> Unit) {
        getMapBoundsMessage.post(GetMapBoundsData(onResult))
    }


    override fun setCameraPosition(
        position: LatLng,
        zoomLevel: Float,
        animate: Boolean
    ) {
        setCameraPositionMessage.post(SetCameraPositionData(position, zoomLevel, animate))
    }

    override fun centerOnPositions(positions: List<LatLng>) {
        setCenterOnPositionsMessage.post(CenterOnPositionsData(positions))
    }

    override fun setCameraZoom(zoomLevel: Float, animate: Boolean) {
        setCameraZoomMessage.post(SetCameraZoomData(zoomLevel, animate))
    }


    override fun saveState() {
    }


    override fun bindTo(
        activity: SKActivity,
        fragment: Fragment?,
        binding: MapView
    ): SKMapView = SKMapView(this, activity, fragment, binding).apply {
        itemsLD.observe {
            onItems(it)
        }
        mapInteractionSettingsLD.observe {
            onMapInteractionSettings(it)
        }
        onMarkerClickLD.observe {
            onOnMarkerClick(it)
        }
        selectMarkerOnClickLD.observe {

        }

        selectedMarkerLD.observe {
            onSelectedMarker(it)
        }

        unselectMarkerOnMapClickLD.observe {
        }

        onMapClickedLD.observe {
            onOnMapClicked(it)
        }

        setCameraPositionMessage.observe {
            setCameraPosition(it.position, it.zoomLevel, it.animate)
        }
        setCenterOnPositionsMessage.observe {
            centerOnPositions(it.positions)
        }
        setCameraZoomMessage.observe {
            setCameraZoom(it.zoomLevel, it.animate)
        }
        showMyLocationButtonMessage.observe {
            showMyLocationButton(it.show, it.onPermissionError)
        }

        getMapBoundsMessage.observe {
            getMapBounds(it.onResult)
        }

        onMapBoundsChangeLD.observe {
            onOnMapBoundsChange(it)
        }
    }


    data class SetCameraPositionData(
        val position: LatLng,
        val zoomLevel: Float,
        val animate: Boolean
    )

    data class SetCameraZoomData(
        val zoomLevel: Float,
        val animate: Boolean
    )

    data class ShowMyLocationButtonData(
        val show: Boolean,
        val onPermissionError: (() -> Unit)?
    )

    data class CenterOnPositionsData(val positions: List<LatLng>)

    data class GetMapBoundsData(
        val onResult: (SKMapVC.LatLngBounds) -> Unit
    )

}

interface SKMapRAI {
    fun onItems(items: List<SKMapVC.Marker>)

    fun onSelectedMarker(selectedMarker: SKMapVC.Marker?)

    fun setCameraPosition(
        position: LatLng,
        zoomLevel: Float,
        animate: Boolean
    )

    fun centerOnPositions(positions: List<LatLng>)

    fun setCameraZoom(zoomLevel: Float, animate: Boolean)

    fun showMyLocationButton(
        show: Boolean,
        onPermissionError: (() -> Unit)?
    )

    fun getMapBounds(
        onResult: (SKMapVC.LatLngBounds) -> Unit
    )

    fun onOnMapBoundsChange(
        onMapBoundsChange: ((SKMapVC.LatLngBounds) -> Unit)?
    )

    fun onOnMapClicked(
        onMapClicked: ((LatLng) -> Unit)?
    )

    fun onOnMarkerClick(
        onMarkerClick: ((SKMapVC.Marker) -> Unit)?
    )

    fun onMapInteractionSettings(mapInteractionSettings: SKMapVC.MapInteractionSettings)
}
