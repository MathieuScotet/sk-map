package tech.skot.libraries.map


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.collection.LruCache

import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.LatLng

@SuppressLint("PotentialBehaviorOverride")
open class GMapInteractionHelper(
    context: Context,
    mapView: MapView,
    memoryCache: LruCache<String, SKMapView.BitmapDescriptorContainer>
    ) : MapInteractionHelper(context, mapView, memoryCache) {
    private var items: List<Pair<SKMapVC.Marker, Marker>> = emptyList()
    private var lastSelectedMarker: Pair<SKMapVC.Marker, Marker>? = null
    override var onMarkerSelected: ((SKMapVC.Marker?) -> Unit)? = null
    override var onMarkerClick: ((SKMapVC.Marker) -> Unit)? = null


    override fun addMarkers(markers: List<SKMapVC.Marker>) {
        mapView.getMapAsync { map ->
            //first parts -> remove
            //second parts -> update
            val currentMarker = this.items.partition { currentItem ->
                currentItem.first.id == null || markers.any {
                    currentItem.first.id == it.id
                }
            }

            //first parts -> update
            //second parts -> add
            val newMarkers = markers.partition { marker ->
                marker.id != null && this.items.any {
                    marker.id != null && marker.id == it.first.id
                }
            }

            //items to remove from map
            currentMarker.first.forEach { pair ->
                if (pair.first.id == lastSelectedMarker?.first?.id) {
                    lastSelectedMarker = null
                }
                pair.second.remove()
            }

            //items to update on map
            val updatedMarker = currentMarker.second.mapNotNull { currentPair ->
                newMarkers.first.find {
                    it.id == currentPair.first.id
                }?.let {
                    Pair(it, currentPair.second.apply {
                        this.position = LatLng(
                            it.position.first,
                            it.position.second
                        )

                        getIcon(it, lastSelectedMarker?.first?.id == it.id)?.let {
                            this.setIcon(it)
                        }
                    })
                }
            }

            //items to add to map
            val addedMarker = newMarkers.second.mapNotNull { skMarker ->
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                skMarker.position.first,
                                skMarker.position.second
                            )
                        ).apply {
                            getIcon(skMarker, false)?.let {
                                this.icon(it)
                            }
                        }
                )
                marker?.let {
                    Pair(skMarker, marker)
                }
            }

            this.items = updatedMarker + addedMarker
        }
    }


    init {
        mapView.getMapAsync {
            it.setOnMarkerClickListener { clickedMarker ->
                val item = items.find { (_, marker) ->
                    marker == clickedMarker
                }
                item?.let {
                    onMarkerClick?.invoke(item.first)
                }
                true
            }
        }
    }

    override fun onOnMapBoundsChange(onMapBoundsChange: ((SKMapVC.LatLngBounds) -> Unit)?) {
        mapView.getMapAsync {
            if (onMapBoundsChange == null) {
                it.setOnCameraIdleListener(null)
            } else {
                it.setOnCameraIdleListener {
                    onMapBoundsChange(getMapBounds(it.projection))
                }
            }
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


    override fun onSelectedMarker(selectedMarker: SKMapVC.Marker?) {
        onMarkerSelected?.invoke(selectedMarker)
        mapView.getMapAsync {
            lastSelectedMarker?.let { current ->
                getIcon(current.first, false)?.let {
                    current.second.setIcon(it)
                }
            }
            lastSelectedMarker = items.find {
                it.first == selectedMarker
            }?.also { newSelectedMarker ->
                getIcon(newSelectedMarker.first, true)?.let {
                    newSelectedMarker.second.setIcon(it)
                }
            }
        }
    }

    private fun getBitmapDescriptorFromBitmap(bitmap: Bitmap): BitmapDescriptor {
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


}
