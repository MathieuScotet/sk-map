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
import com.google.android.gms.maps.model.LatLng as LatLngGMap


class SKMapView(
    override val proxy: SKMapViewProxy,
    activity: SKActivity,
    fragment: Fragment?,
    val mapView: MapView
) : SKComponentView<MapView>(proxy, activity, fragment, mapView), SKMapRAI {

    private var onMarkerSelected: ((SKMapVC.Marker?) -> Unit)? = null
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
                proxy.onMapClicked?.invoke(LatLng(it.latitude, it.longitude))
            }


            it.setOnMarkerClickListener { clickedMarker ->
                val item = items.find { (_, marker) ->
                    marker == clickedMarker
                }
                item?.let {
                    proxy.onMarkerClicked?.invoke(item.first)
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
                currentItem.first.id == null || items.any { currentItem.first.id == it.id }
            }

            //first parts -> update
            //second parts -> add
            val newMarkers = items.partition { marker ->
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
                        this.position = LatLngGMap(
                            it.position.first,
                            it.position.second
                        )

                        getIcon(it, lastSelectedMarker?.first?.id == it.id).let {
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
                            LatLngGMap(
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


    override fun onOnMapClicked(onMapClicked: ((LatLng) -> Unit)?) {
        mapView.getMapAsync {
            if (onMapClicked != null) {
                it.setOnMapClickListener {
                    onMapClicked.invoke(it.latitude to it.longitude)
                }
            } else {
                it.setOnMapClickListener(null)
            }
        }
    }

    override fun onOnMarkerClick(onMarkerClick: ((SKMapVC.Marker) -> Unit)?) {
        mapView.getMapAsync {
            if (onMarkerClick != null) {
                it.setOnMarkerClickListener { clickedMarker ->
                    val item = items.find { (_, marker) ->
                        marker == clickedMarker
                    }
                    item?.let {
                        onMarkerClick.invoke(item.first)
                    }
                    true
                }
            } else {
                it.setOnMapClickListener(null)
            }
        }
    }

    override fun onOnMarkerSelected(onMarkerSelected: ((SKMapVC.Marker?) -> Unit)?) {
        this.onMarkerSelected = onMarkerSelected
    }


    override fun setCameraPosition(
        position: LatLng,
        zoomLevel: Float,
        animate: Boolean
    ) {
        val cameraUpdate =
            CameraUpdateFactory.newLatLngZoom(
                com.google.android.gms.maps.model.LatLng(position.first, position.second),
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

    override fun centerOnPositions(positions: List<LatLng>) {
        if (positions.isNotEmpty()) {
            val latLngBoundsBuilder = LatLngBounds.builder()
            positions.forEach {
                latLngBoundsBuilder.include(LatLngGMap(it.first, it.second))
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

    override fun onOnMapBoundsChange(onMapBoundsChange: ((SKMapVC.MapBounds) -> Unit)?) {
        mapView.getMapAsync {
            if (onMapBoundsChange == null) {
                it.setOnCameraIdleListener(null)
            } else {
                it.setOnCameraIdleListener {
                    it.projection.visibleRegion.latLngBounds.let {
                        onMapBoundsChange(
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
