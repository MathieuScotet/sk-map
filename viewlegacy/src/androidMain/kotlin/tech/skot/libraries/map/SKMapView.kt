package tech.skot.libraries.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import androidx.collection.LruCache
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import tech.skot.core.components.SKActivity
import tech.skot.core.components.SKComponentView
import com.google.android.gms.maps.model.LatLng as LatLngGMap

class SKMapView(
    override val proxy: SKMapViewProxy,
    activity: SKActivity,
    fragment: Fragment?,
    val mapView: MapView
) : SKComponentView<MapView>(proxy, activity, fragment, mapView), SKMapRAI {


    private var mapInteractionHelper: MapInteractionHelper? = null
    private val memoryCache: LruCache<String, BitmapDescriptorContainer>
    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)


    /**
     * use it to create BitmapDescriptor in case of  [CustomMarker][SKMapVC.CustomMarker] use
     */
    @Suppress("unused")
    var onCreateCustomMarkerIcon: ((SKMapVC.CustomMarker, selected: Boolean) -> Bitmap)? = null
        set(value) {
            field = value
            mapInteractionHelper?.onCreateCustomMarkerIcon = value
        }


    @Suppress("unused")
    var getMarkerAnchor : ((SKMapVC.Marker, selected: Boolean) -> Pair<Float, Float>?)? = null
        set(value) {
            field = value
            mapInteractionHelper?.getMarkerAnchor = value
        }

    init {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
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

//        mapInteractionHelper = when (val settings = proxy.mapInteractionSettings) {
//            is SKMapVC.MapClusteringInteractionSettings -> {
//                GMapClusteringInteractionHelper(activity, mapView, memoryCache, settings)
//            }
//            is SKMapVC.MapNormalInteractionSettings -> {
//                GMapInteractionHelper(activity, mapView, memoryCache)
//            }
//            is SKMapVC.MapCustomInteractionSettings -> {
//                mapRefCustomInteractionHelper[settings.customRef]?.invoke(
//                    activity,
//                    mapView,
//                    memoryCache,
//                    settings.data
//                )
//                    ?: throw NotImplementedError("With MapCustomInteractionSettings you must provide a CustomInteractionHelper with ref ${settings.customRef} in mapRefCustomInteractionHelper ")
//            }
//        }.apply {
//            this.onCreateCustomMarkerIcon = this@SKMapView.onCreateCustomMarkerIcon
//        }
    }

    override fun onSelectedMarker(selectedMarker: SKMapVC.Marker?) {
        mapInteractionHelper?.onSelectedMarker(selectedMarker)
    }

    override fun onMarkers(markers: List<SKMapVC.Marker>) {
        mapInteractionHelper?.addMarkers(markers)
    }

    override fun onLines(polylines: List<SKMapVC.Polyline>) {
        mapInteractionHelper?.addLines(polylines)
    }

    override fun onPolygons(polygons: List<SKMapVC.Polygon>) {
        mapInteractionHelper?.addPolygons(polygons)
    }

    override fun onMapType(mapType : MapType) {
        mapView.getMapAsync {
            when(mapType){
                MapType.NORMAL ->  it.mapType = GoogleMap.MAP_TYPE_NORMAL
                MapType.SATELLITE ->   it.mapType = GoogleMap.MAP_TYPE_SATELLITE
                MapType.HYBRID ->  it.mapType = GoogleMap.MAP_TYPE_HYBRID
                MapType.TERRAIN ->  it.mapType = GoogleMap.MAP_TYPE_TERRAIN
                MapType.NONE ->  it.mapType = GoogleMap.MAP_TYPE_NONE
                else -> it.mapType = GoogleMap.MAP_TYPE_NORMAL
            }

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

    override fun onOnMapLongClicked(onMapLongClicked: ((LatLng) -> Unit)?) {
        mapView.getMapAsync {
            if (onMapLongClicked != null) {
                it.setOnMapLongClickListener {
                    onMapLongClicked.invoke(it.latitude to it.longitude)
                }
            } else {
                it.setOnMapClickListener(null)
            }
        }
    }

    override fun onOnMarkerClick(onMarkerClick: ((SKMapVC.Marker) -> Unit)?) {
        mapInteractionHelper?.onMarkerClick = onMarkerClick
    }

    override fun onMapInteractionSettings(mapInteractionSettings: SKMapVC.MapInteractionSettings) {
        mapView.getMapAsync { googleMap ->
            googleMap.clear()
            mapInteractionHelper = null
            mapInteractionHelper = when (mapInteractionSettings) {
                is SKMapVC.MapClusteringInteractionSettings -> {
                    GMapClusteringInteractionHelper(
                        context = activity,
                        mapView = mapView,
                        memoryCache = memoryCache,
                        mapInteractionSettings
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
                this.getMarkerAnchor = this@SKMapView.getMarkerAnchor
                this.onOnMapBoundsChange(proxy.onMapBoundsChange)
                this.onMarkerClick = proxy.onMarkerClicked
                this.onSelectedMarker(proxy.selectedMarker)
                this.addMarkers(proxy.markers)
                this.addLines(proxy.polylines)
            }


        }

    }

    override fun onShowLog(show: Boolean) {
        MapLoggerView.enabled = show
    }

    override fun getCurrentLocation(onResult: (LatLng) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            onResult(LatLng(it.latitude, it.longitude))
        }
    }

    override fun setCameraPosition(
        position: LatLng,
        zoomLevel: Float,
        animate: Boolean
    ) {
        mapView.getMapAsync {
            it.setOnMapLoadedCallback {
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
    }

    override fun centerOnPositions(positions: List<LatLng>) {
        if (positions.isNotEmpty()) {
                mapView.getMapAsync {
                    it.setOnMapLoadedCallback {
                        val latLngBoundsBuilder = LatLngBounds.builder()
                        positions.forEach {
                            latLngBoundsBuilder.include(LatLngGMap(it.first, it.second))
                        }
                        val latLngBound = CameraUpdateFactory.newLatLngBounds(
                            latLngBoundsBuilder.build(),
                            (16 * Resources.getSystem().displayMetrics.density).toInt()
                        )
                        it.animateCamera(latLngBound)
                }
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
        mapInteractionHelper?.onOnMapBoundsChange(onMapBoundsChange)
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
                MapLoggerView.d("enabled myLocationButton :$show")
                it.isMyLocationEnabled = show
                it.uiSettings.isMyLocationButtonEnabled = true
            } else {
                MapLoggerView.d("permission error :$show")
                onPermissionError?.invoke()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun showMyLocation(
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
                MapLoggerView.d("enabled myLocationButton :$show")
                it.isMyLocationEnabled = show
            } else {
                MapLoggerView.d("permission error :$show")
                onPermissionError?.invoke()
            }
        }
    }

    class BitmapDescriptorContainer(bitmap: Bitmap) {
        val bitmapDescriptor: BitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap)
        val size: Int = bitmap.byteCount
    }

    companion object {
        val mapRefCustomInteractionHelper: MutableMap<Int, (context: Context, mapView: MapView, memoryCache: LruCache<String, BitmapDescriptorContainer>, data: Any?) -> MapInteractionHelper> =
            mutableMapOf()
    }


}