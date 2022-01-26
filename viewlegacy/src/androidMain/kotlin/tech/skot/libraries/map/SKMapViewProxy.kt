package tech.skot.libraries.map


import androidx.fragment.app.Fragment
import com.google.android.gms.maps.MapView
import tech.skot.core.components.SKActivity
import tech.skot.core.components.SKComponentViewProxy
import tech.skot.view.live.MutableSKLiveData
import tech.skot.view.live.SKMessage

class SKMapViewProxy(
    override val onMarkerClick: Function1<SKMapVC.Marker, Unit>,
    itemsInitial: List<SKMapVC.Marker>,
    onMapClickedInitial: Function1<Pair<Double, Double>, Unit>?,
    selectedMarkerInitial: SKMapVC.Marker?,
) : SKComponentViewProxy<MapView>(), SKMapVC {

    private val itemsLD: MutableSKLiveData<List<SKMapVC.Marker>> = MutableSKLiveData(itemsInitial)
    override var markers: List<SKMapVC.Marker> by itemsLD

    private val onMapClickedLD: MutableSKLiveData<Function1<Pair<Double, Double>, Unit>?> =
        MutableSKLiveData(onMapClickedInitial)
    override var onMapClicked: Function1<Pair<Double, Double>, Unit>? by onMapClickedLD

    private val selectedMarkerLD: MutableSKLiveData<SKMapVC.Marker?> =
        MutableSKLiveData(selectedMarkerInitial)
    override var selectedMarker: SKMapVC.Marker? by selectedMarkerLD

    private val setCameraPositionMessage: SKMessage<SetCameraPositionData> = SKMessage()
    private val setCenterOnPositionsMessage: SKMessage<CenterOnPositionsData> = SKMessage()
    private val setCameraZoomMessage: SKMessage<SetCameraZoomData> = SKMessage()
    private val showMyLocationButtonMessage: SKMessage<ShowMyLocationButtonData> = SKMessage()
    private val getMapBoundsMessage: SKMessage<GetMapBoundsData> = SKMessage()
    private val onMapBoundsChangeMessage: SKMessage<OnMapBoundsChangeData> = SKMessage()

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

    override fun getMapBounds(onResult: (SKMapVC.MapBounds) -> Unit) {
        getMapBoundsMessage.post(GetMapBoundsData(onResult))
    }

    override fun onMapBoundsChange(onResult: ((SKMapVC.MapBounds) -> Unit)?) {
        onMapBoundsChangeMessage.post(OnMapBoundsChangeData(onResult))
    }

    override fun setCameraPosition(
        position: Pair<Double, Double>,
        zoomLevel: Float,
        animate: Boolean
    ) {
        setCameraPositionMessage.post(SetCameraPositionData(position, zoomLevel, animate))
    }

    override fun centerOnPositions(positions: List<Pair<Double, Double>>) {
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
        selectedMarkerLD.observe {
            onSelectedMarker(it)
        }

        setCameraPositionMessage.observe {
            this.setCameraPosition(it.position, it.zoomLevel, it.animate)
        }
        setCenterOnPositionsMessage.observe {
            this.centerOnPositions(it.positions)
        }
        setCameraZoomMessage.observe {
            this.setCameraZoom(it.zoomLevel, it.animate)
        }
        showMyLocationButtonMessage.observe {
            this.showMyLocationButton(it.show, it.onPermissionError)
        }

        getMapBoundsMessage.observe {
            this.getMapBounds(it.onResult)
        }

        onMapBoundsChangeMessage.observe {
            this.onMapBoundsChange(it.onResult)
        }
    }

    data class SetCameraPositionData(
        val position: Pair<Double, Double>,
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

    data class CenterOnPositionsData(val positions: List<Pair<Double, Double>>)

    data class GetMapBoundsData(
        val onResult: (SKMapVC.MapBounds) -> Unit
    )

    data class OnMapBoundsChangeData(val onResult: ((SKMapVC.MapBounds) -> Unit)?)
}

interface SKMapRAI {
    fun onItems(items: List<SKMapVC.Marker>)

    fun onSelectedMarker(selectedMarker: SKMapVC.Marker?)

    fun setCameraPosition(
        position: Pair<Double, Double>,
        zoomLevel: Float,
        animate: Boolean
    )

    fun centerOnPositions(positions: List<Pair<Double, Double>>)

    fun setCameraZoom(zoomLevel: Float, animate: Boolean)

    fun showMyLocationButton(
        show: Boolean,
        onPermissionError: (() -> Unit)?
    )

    fun getMapBounds(
        onResult: (SKMapVC.MapBounds) -> Unit
    )

    fun onMapBoundsChange(
        onResult: ((SKMapVC.MapBounds) -> Unit)?
    )
}
