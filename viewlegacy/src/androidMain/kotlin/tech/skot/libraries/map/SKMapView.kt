package tech.skot.libraries.map

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import tech.skot.core.components.SKActivity
import tech.skot.core.components.SKComponentView


class SKMapView(
    override val proxy: SKMapViewProxy,
    activity: SKActivity,
    fragment: Fragment?,
    val mapView: MapView
) : SKComponentView<MapView>(proxy, activity, fragment, mapView), SKMapRAI {

    private var lastSelectedMarker: Pair<SKMapVC.Marker, Marker>? = null


    private var items: List<Pair<SKMapVC.Marker, Marker>> = emptyList()


    init {
        lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                mapView.onDestroy()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() {
                mapView.onCreate(null)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                mapView.onPause()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                mapView.onResume()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                mapView.onStart()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStop() {
                mapView.onStop()
            }


        })



        mapView.getMapAsync {

            it.setOnMapClickListener {
                proxy.onMapClicked?.invoke(Pair(it.latitude, it.longitude))
            }

            it.setOnMarkerClickListener { clickedMarker ->
                val item = items.find { (_, marker) ->
                    marker == clickedMarker
                }
                item?.let {
                    proxy.onMarkerClick.invoke(item.first)
                }
                true
            }

        }
    }

    /**
     * Helper method to obtain BitmapDescriptor from resource.
     * Add compatibility with vector resources
     */
    private fun getBitmapDescriptor(context: Context, resId: Int, color: Int?): BitmapDescriptor? {
        val drawable: Drawable? = ContextCompat.getDrawable(context, resId)
            ?.let { if (color != null) it.mutate() else it }
        return drawable?.let {
            drawable.setBounds(
                0,
                0,
                drawable.intrinsicWidth,
                drawable.intrinsicHeight
            )

            color?.let {
                drawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context,color),PorterDuff.Mode.MULTIPLY)
            }
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)
            drawable.draw(canvas)

            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }





    override fun onSelectedMarker(selectedMarker: SKMapVC.Marker?) {
        mapView.getMapAsync {
            lastSelectedMarker?.let { current ->
                getIcon(current.first, false)?.let {
                    current.second.setIcon(it)
                }
            }
            lastSelectedMarker = items.find {
                it.first == selectedMarker
            }?.also { newSelectedMarker ->
                getIcon(newSelectedMarker.first, true).let {
                    newSelectedMarker.second.setIcon(it)
                }
            }
        }
    }

    override fun onItems(items: List<SKMapVC.Marker>) {
        mapView.getMapAsync { map ->
            //first parts -> remove
            //second parts -> update
            val currentMarker = this.items.partition { currentItem ->
                currentItem.first.itemId == null || items.any { currentItem.first.itemId == it.itemId }
            }

            //first parts -> update
            //second parts -> add
            val newMarkers = items.partition { marker ->
                marker.itemId != null && this.items.any {
                    marker.itemId != null && marker.itemId == it.first.itemId
                }
            }

            //items to remove from map
            currentMarker.first.forEach { pair ->
                if (pair.first.itemId == lastSelectedMarker?.first?.itemId) {
                    lastSelectedMarker = null
                }
                pair.second.remove()
            }

            //items to update on map
            val updatedMarker = currentMarker.second.mapNotNull { currentPair ->
                newMarkers.first.find {
                    it.itemId == currentPair.first.itemId
                }?.let {
                    Pair(it, currentPair.second.apply {
                        this.position = (LatLng(
                            it.position.first,
                            it.position.second
                        ))

                        getIcon(it, lastSelectedMarker?.first?.itemId == it.itemId).let {
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
                            getIcon(skMarker, false).let {
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

    override fun setCameraPosition(
        position: Pair<Double, Double>,
        zoomLevel: Float,
        animate: Boolean
    ) {
        val cameraUpdate =
            CameraUpdateFactory.newLatLngZoom(
                LatLng(position.first, position.second),
                zoomLevel
            )
        mapView.getMapAsync {
            if (animate) {
                it.animateCamera(cameraUpdate)
            } else {
                it.moveCamera(cameraUpdate)
            }
        }
    }

    override fun centerOnPositions(positions: List<Pair<Double, Double>>) {
        if (positions.isNotEmpty()) {
            val latLngBoundsBuilder = LatLngBounds.builder()
            positions.forEach {
                latLngBoundsBuilder.include(LatLng(it.first, it.second))
            }
            val latLngBound = CameraUpdateFactory.newLatLngBounds(
                latLngBoundsBuilder.build(),
                (16 * Resources.getSystem().displayMetrics.density).toInt()
            )
            mapView.getMapAsync {
                it.animateCamera(latLngBound)
            }
        }
    }


    private fun getIcon(marker: SKMapVC.Marker, selected: Boolean): BitmapDescriptor? {
        return when (marker) {
            is SKMapVC.IconMarker -> {
                if (selected) {
                    getBitmapDescriptor(context, marker.selectedIcon.res, null)
                } else {
                    getBitmapDescriptor(context, marker.normalIcon.res, null)
                }
            }
            is SKMapVC.ColorizedIconMarker -> {
                if (selected) {
                    getBitmapDescriptor(context, marker.icon.res, marker.selectedColor.res)
                } else {
                    getBitmapDescriptor(context, marker.icon.res, marker.normalColor.res)
                }
            }
        }
    }


}
