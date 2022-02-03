package tech.skot.libraries.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import androidx.collection.LruCache
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
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

    private var mapInteractionHelper: MapInteractionHelper
    private val memoryCache: LruCache<String, BitmapDescriptorContainer>


    /**
     * use it to create BitmapDescriptor in case of  [CustomMarker][SKMapVC.CustomMarker] use
     */
    @Suppress("unused")
    var onCreateCustomMarkerIcon: ((SKMapVC.CustomMarker, selected: Boolean) -> Bitmap)? = null
        set(value) {
            field = value
            mapInteractionHelper.onCreateCustomMarkerIcon = value
        }


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

        val maxMemory = (Runtime.getRuntime().maxMemory()).toInt()
        val cacheSize = maxMemory / 8
        memoryCache =
            object : LruCache<String, BitmapDescriptorContainer>(cacheSize) {

                override fun sizeOf(
                    key: String,
                    bitmap: BitmapDescriptorContainer
                ): Int {
                    return bitmap.size
                }
            }

        mapInteractionHelper = when (val settings = proxy.mapInteractionSettings) {
            is SKMapVC.MapClusteringInteractionSettings -> {
                GMapClusteringInteractionHelper(activity, mapView, memoryCache)
            }
            is SKMapVC.MapNormalInteractionSettings -> {
                GMapInteractionHelper(activity, mapView, memoryCache)
            }
            is SKMapVC.MapCustomInteractionSettings -> {
                mapRefCustomInteractionHelper[settings.customRef]?.invoke(
                    activity,
                    mapView,
                    memoryCache,
                    settings.data
                )
                    ?: throw NotImplementedError("With MapCustomInteractionSettings you must provide a CustomInteractionHelper with ref ${settings.customRef} in mapRefCustomInteractionHelper ")
            }
        }.apply {
            this.onCreateCustomMarkerIcon = this@SKMapView.onCreateCustomMarkerIcon
        }
    }


    override fun onSelectedMarker(selectedMarker: SKMapVC.Marker?) {
        mapInteractionHelper.onSelectedMarker(selectedMarker)
    }

    override fun onItems(items: List<SKMapVC.Marker>) {
        mapInteractionHelper.addMarkers(markers = items)
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
        mapInteractionHelper.onMarkerClick = onMarkerClick
    }

    override fun onOnMarkerSelected(onMarkerSelected: ((SKMapVC.Marker?) -> Unit)?) {
        mapInteractionHelper.onMarkerSelected = onMarkerSelected
    }


    override fun onMapInteractionSettings(mapInteractionSettings: SKMapVC.MapInteractionSettings) {
        mapView.getMapAsync { googleMap ->
            googleMap.clear()
            mapInteractionHelper = when (mapInteractionSettings) {
                is SKMapVC.MapClusteringInteractionSettings -> {
                    GMapClusteringInteractionHelper(
                        context = activity,
                        mapView = mapView,
                        memoryCache = memoryCache,
                        onClusterClick = mapInteractionSettings.onClusterClick
                    )
                }
                is SKMapVC.MapNormalInteractionSettings -> {
                    GMapInteractionHelper(activity, mapView, memoryCache)
                }
                is SKMapVC.MapCustomInteractionSettings -> {
                    mapRefCustomInteractionHelper[mapInteractionSettings.customRef]?.invoke(
                        activity,
                        mapView,
                        memoryCache,
                        mapInteractionSettings.data
                    )
                        ?: throw NotImplementedError("With MapCustomInteractionSettings you must provide a CustomInteractionHelper with ref ${mapInteractionSettings.customRef} in mapRefCustomInteractionHelper ")
                }
            }.apply {
                this.onCreateCustomMarkerIcon = this@SKMapView.onCreateCustomMarkerIcon

                this.onOnMapBoundsChange(proxy.onMapBoundsChange)
                this.onMarkerClick = proxy.onMarkerClicked
                this.onMarkerSelected = proxy.onMarkerSelected
                this.onSelectedMarker(proxy.selectedMarker)
                this.addMarkers(proxy.markers)
            }


        }

    }


    override fun setCameraPosition(
        position: LatLng,
        zoomLevel: Float,
        animate: Boolean
    ) {
        mapView.getMapAsync {
            val cameraUpdate =
                CameraUpdateFactory.newLatLngZoom(
                    com.google.android.gms.maps.model.LatLng(position.first, position.second),
                    zoomLevel
                )
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
            mapView.getMapAsync {
                val latLngBound = CameraUpdateFactory.newLatLngBounds(
                    latLngBoundsBuilder.build(),
                    (16 * Resources.getSystem().displayMetrics.density).toInt()
                )
                it.animateCamera(latLngBound)
            }
        }
    }


    override fun getMapBounds(onResult: (SKMapVC.LatLngBounds) -> Unit) {
        mapView.getMapAsync {
            it.projection.visibleRegion.latLngBounds.let {
                onResult(
                    SKMapVC.LatLngBounds(
                        it.northeast.latitude to it.northeast.longitude,
                        it.southwest.latitude to it.southwest.longitude
                    )
                )
            }
        }
    }

    override fun onOnMapBoundsChange(onMapBoundsChange: ((SKMapVC.LatLngBounds) -> Unit)?) {
        mapInteractionHelper.onOnMapBoundsChange(onMapBoundsChange)
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


    companion object {
        val mapRefCustomInteractionHelper: MutableMap<Int, (context: Context, mapView: MapView, memoryCache: LruCache<String, BitmapDescriptorContainer>, data: Any?) -> MapInteractionHelper> =
            mutableMapOf()
    }

    class BitmapDescriptorContainer(bitmap: Bitmap) {
        val bitmapDescriptor: BitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap)
        val size: Int = bitmap.byteCount
    }


}
