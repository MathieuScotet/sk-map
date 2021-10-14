package tech.skot.libraries.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import tech.skot.core.components.SKActivity
import tech.skot.core.components.SKComponentView
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.model.BitmapDescriptor


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
    private fun getBitmapDescriptor(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable?.let {
            return when (it) {
                is VectorDrawable -> {
                    vectorDrawable.setBounds(
                        0,
                        0,
                        vectorDrawable.intrinsicWidth,
                        vectorDrawable.intrinsicHeight
                    )
                    val bitmap: Bitmap = Bitmap.createBitmap(
                        vectorDrawable.intrinsicWidth,
                        vectorDrawable.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(bitmap)
                    vectorDrawable.draw(canvas)
                    BitmapDescriptorFactory.fromBitmap(bitmap)
                }
                else -> {
                    BitmapDescriptorFactory.fromResource(vectorResId)
                }
            }
        }
        return null
    }


    override fun onSelectedMarker(selectedMarker: SKMapVC.Marker?) {
        mapView.getMapAsync {
            lastSelectedMarker?.first?.normalIcon?.let {
                lastSelectedMarker?.second?.setIcon(getBitmapDescriptor(context, it.res))
            }
            lastSelectedMarker = items.find {
                it.first == selectedMarker
            }?.also { newSelectedMarker ->
                newSelectedMarker.first.selectedIcon?.also {
                    newSelectedMarker.second.setIcon(getBitmapDescriptor(context, it.res))
                } ?: kotlin.run {
                    newSelectedMarker.second.setIcon(null)
                }
            }
        }
    }

    override fun onItems(items: List<SKMapVC.Marker>) {
        mapView.getMapAsync { map ->
            lastSelectedMarker = null
            map.clear()
            this.items = items.mapNotNull { skMarker ->
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                skMarker.position.first,
                                skMarker.position.second
                            )
                        ).apply {
                            skMarker.normalIcon?.let {
                                this.icon(getBitmapDescriptor(context, it.res))
                            }
                        }

                )
                marker?.let {
                    Pair(skMarker, marker)
                }
            }
        }
    }

    override fun setCameraPosition(position: Pair<Double, Double>, zoomLevel: Float, animate: Boolean) {
        val cameraUpdate =
            CameraUpdateFactory.newLatLngZoom(LatLng(position.first, position.second), zoomLevel)
        mapView.getMapAsync {
            if (animate) {
                it.animateCamera(cameraUpdate)
            } else {
                it.moveCamera(cameraUpdate)
            }
        }
    }

}
