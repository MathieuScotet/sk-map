package tech.skot.libraries.map

import android.content.Context
import android.graphics.Bitmap
import androidx.collection.LruCache
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import tech.skot.core.SKLog

class GMapClusteringInteractionHelper(
    context: Context,
    mapView: MapView,
    memoryCache: LruCache<String, SKMapView.BitmapDescriptorContainer>,
    var onClusterClick: ((List<SKMapVC.Marker>) -> Unit)? = null,
) : MapInteractionHelper(context, mapView, memoryCache) {
    private var _clusterManager: ClusterManager<SKClusterMarker>? = null
    private var onMapBoundsChange: ((MapBounds) -> Unit)? = null
    private var lastSelectedMarker: SKClusterMarker? = null
    override var onMarkerSelected: ((SKMapVC.Marker?) -> Unit)? = null
    override var onMarkerClick: ((SKMapVC.Marker) -> Unit)? = null
    private var mutext = Mutex()
    var getClusterIcon: ((List<SKMapVC.Marker>) -> Bitmap?)? = null



    private var items: List<SKClusterMarker> = emptyList()


    private fun getClusterManagerAsync(onReady: (ClusterManager<SKClusterMarker>) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            mutext.lock()
            _clusterManager?.let {
                SKLog.d("AAA use current clusterManager")
                onReady.invoke(it)
                mutext.unlock()
            } ?: kotlin.run {
                mapView.getMapAsync { googleMap ->
                    SKLog.d("AAA init clusterManager")
                    val clusterManager = ClusterManager<SKClusterMarker>(context, googleMap)
                    clusterManager.renderer = object : DefaultClusterRenderer<SKClusterMarker>(
                        context,
                        googleMap,
                        clusterManager
                    ) {
                        override fun onBeforeClusterItemRendered(
                            item: SKClusterMarker,
                            markerOptions: MarkerOptions
                        ) {
                            getIcon(item.marker, item.selected)?.let {
                                markerOptions.icon(it)
                            }
                            super.onBeforeClusterItemRendered(item, markerOptions)
                        }

                        override fun onClusterItemUpdated(item: SKClusterMarker, marker: Marker) {
                            super.onClusterItemUpdated(item, marker)
                            getIcon(item.marker, item.selected)?.let {
                                marker.setIcon(it)
                            }
                        }

                        override fun onBeforeClusterRendered(
                            cluster: Cluster<SKClusterMarker>,
                            markerOptions: MarkerOptions
                        ) {
                            super.onBeforeClusterRendered(cluster, markerOptions)
                            getClusterIcon?.invoke(cluster.items.map { it.marker })?.let {
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(it))
                            }
                        }

                    }
                    googleMap.setOnCameraIdleListener {
                        SKLog.d("AAA OnCameraIdleListener")
                        clusterManager.onCameraIdle()
                        onMapBoundsChange?.invoke(getMapBounds(googleMap.projection))
                    }
                    clusterManager.setOnClusterClickListener {
                        it.items.map {
                            it.marker
                        }.let {
                            SKLog.d("AAA cluster click")
                            onClusterClick?.invoke(it)
                        }
                        true
                    }
                    clusterManager.setOnClusterItemClickListener {
                        it.marker.let {
                            SKLog.d("AAA marker click")
                            onMarkerClick?.invoke(it)
                        }
                        true
                    }
                    _clusterManager = clusterManager
                    onReady.invoke(clusterManager)
                    mutext.unlock()

                }
            }

        }

    }


    override fun onSelectedMarker(selectedMarker: SKMapVC.Marker?) {
        onMarkerSelected?.invoke(selectedMarker)
        getClusterManagerAsync { clusterManager ->
            lastSelectedMarker?.let {
                it.selected = false
                clusterManager.updateItem(lastSelectedMarker)
                clusterManager.cluster()
            }

            lastSelectedMarker = items.find {
                it.marker == selectedMarker
            }?.apply {
                this.selected = true
                clusterManager.updateItem(this)
                clusterManager.cluster()
            }
        }
    }

    override fun onOnMapBoundsChange(onMapBoundsChange: ((SKMapVC.LatLngBounds) -> Unit)?) {
        this.onMapBoundsChange = onMapBoundsChange
    }

    override fun addMarkers(markers: List<SKMapVC.Marker>) {
        getClusterManagerAsync {
            it.clearItems()
            items = markers.map { marker ->
                SKClusterMarker(marker, false)
            }
            it.addItems(items)
            it.cluster()
        }
    }

    protected fun getMapBounds(projection: Projection): SKMapVC.LatLngBounds {
        return projection.visibleRegion.latLngBounds.let {
            SKMapVC.LatLngBounds(
                it.northeast.latitude to it.northeast.longitude,
                it.southwest.latitude to it.southwest.longitude
            )

        }
    }


}