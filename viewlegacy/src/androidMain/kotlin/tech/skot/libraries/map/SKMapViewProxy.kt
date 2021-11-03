package tech.skot.libraries.map


import androidx.fragment.app.Fragment
import com.google.android.gms.maps.MapView
import tech.skot.core.components.SKActivity
import tech.skot.core.components.SKComponentViewProxy
import tech.skot.view.live.MutableSKLiveData
import tech.skot.view.live.SKMessage
import kotlin.Boolean
import kotlin.Double
import kotlin.Float
import kotlin.Function1
import kotlin.Pair
import kotlin.Unit
import kotlin.collections.List

class SKMapViewProxy(
    override val onMarkerClick: Function1<SKMapVC.Marker, Unit>,
    itemsInitial: List<SKMapVC.Marker>,
    onMapClickedInitial: Function1<Pair<Double, Double>, Unit>?,
    selectedMarkerInitial: SKMapVC.Marker?
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
    private val setCenterOnPositions: SKMessage<CenterOnPositionsData> = SKMessage()

    override fun setCameraPosition(
        position: Pair<Double, Double>,
        zoomLevel: Float,
        animate: Boolean
    ) {
        setCameraPositionMessage.post(SetCameraPositionData(position, zoomLevel, animate))
    }

    override fun centerOnPositions(positions : List<Pair<Double, Double>>) {
        setCenterOnPositions.post(CenterOnPositionsData(positions))
    }

    override fun saveState() {
    }


    override fun bindTo(
        activity: SKActivity,
        fragment: Fragment?,
        binding: MapView,
        collectingObservers: Boolean
    ): SKMapView = SKMapView(this, activity, fragment, binding).apply {
        collectObservers = collectingObservers
        itemsLD.observe {
            onItems(it)
        }
        selectedMarkerLD.observe {
            onSelectedMarker(it)
        }
        setCameraPositionMessage.observe {
            this.setCameraPosition(it.position, it.zoomLevel, it.animate)
        }
        setCenterOnPositions.observe {
            this.centerOnPositions(it.positions)
        }
    }

    data class SetCameraPositionData(
        val position: Pair<Double, Double>,
        val zoomLevel: Float,
        val animate: Boolean
    )
    data class CenterOnPositionsData(val positions : List<Pair<Double, Double>>)
}

interface SKMapRAI {
    fun onItems(items: List<SKMapVC.Marker>)

    fun onSelectedMarker(selectedMarker: SKMapVC.Marker?)

    fun setCameraPosition(
        position: Pair<Double, Double>,
        zoomLevel: Float,
        animate: Boolean
    )

    fun centerOnPositions(positions: List<Pair<Double,Double>>)
}
