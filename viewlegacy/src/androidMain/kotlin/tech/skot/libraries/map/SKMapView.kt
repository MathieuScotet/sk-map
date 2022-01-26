package tech.skot.libraries.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import tech.skot.core.SKLog
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


    /**
     * use it to create BitmapDescriptor in case of  [CustomMarker][SKMapVC.CustomMarker] use
     */
    var onCreateCustomMarkerIcon: ((SKMapVC.CustomMarker, selected: Boolean) -> BitmapDescriptor)? =
        null


    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                mapView.onDestroy()
            }

            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                mapView.onCreate(null)
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                mapView.onPause()
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                mapView.onResume()
            }

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                mapView.onStart()
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
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
                drawable.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(context, color),
                    PorterDuff.Mode.MULTIPLY
                )
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


    override fun getMapBounds(onResult: (SKMapVC.MapBounds) -> Unit) {
        mapView.getMapAsync {
            it.projection.visibleRegion.latLngBounds.let {
                onResult(
                    SKMapVC.MapBounds(
                        it.northeast.latitude to it.northeast.longitude,
                        it.southwest.latitude to it.southwest.longitude
                    )
                )
            }
        }
    }

    override fun onMapBoundsChange(onResult: ((SKMapVC.MapBounds) -> Unit)?) {
        mapView.getMapAsync {
            if (onResult == null) {
                it.setOnCameraIdleListener(null)
            } else {
                it.setOnCameraIdleListener {
                    it.projection.visibleRegion.latLngBounds.let {
                        onResult(
                            SKMapVC.MapBounds(
                                it.northeast.latitude to it.northeast.longitude,
                                it.southwest.latitude to it.southwest.longitude
                            )
                        )
                    }
                }
            }
        }
    }


    override fun setCameraZoom(zoomLevel: Float, animate: Boolean) {
        mapView.getMapAsync {
            CameraUpdateFactory.zoomTo(zoomLevel).let { cameraUpdate ->
                if (animate) {
                    it.animateCamera(cameraUpdate)
                } else {
                    it.moveCamera(cameraUpdate)
                }
            }

        }
    }

    @SuppressLint("MissingPermission")
    override fun showMyLocationButton(
        show: Boolean,
        onPermissionError: (() -> Unit)?
    ) {
        mapView.getMapAsync {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                SKLog.d("enabled button :$show")
                it.isMyLocationEnabled = show
            } else {
                SKLog.d("permission error :$show")
                onPermissionError?.invoke()
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
            is SKMapVC.CustomMarker -> {
                onCreateCustomMarkerIcon?.invoke(marker, selected)
                    ?: throw NoSuchFieldException("onCreateCustomMarkerIcon must not be null with CustomMarker")
            }
        }
    }


}
