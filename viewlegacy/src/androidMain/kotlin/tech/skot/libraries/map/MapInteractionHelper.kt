package tech.skot.libraries.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import androidx.collection.LruCache
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import tech.skot.core.SKLog

abstract class MapInteractionHelper(
    val context: Context,
    val mapView: MapView,
    val memoryCache: LruCache<String, SKMapView.BitmapDescriptorContainer>
) {


    abstract var onMarkerClick: ((SKMapVC.Marker) -> Unit)?
    var onCreateCustomMarkerIcon: ((SKMapVC.CustomMarker, selected: Boolean) -> Bitmap?)? = null
    abstract fun onSelectedMarker(selectedMarker: SKMapVC.Marker?)
    abstract fun addMarkers(markers: List<SKMapVC.Marker>)
    abstract fun onOnMapBoundsChange(onMapBoundsChange: ((SKMapVC.LatLngBounds) -> Unit)?)

    /**
     * Helper method to obtain BitmapDescriptor from resource.
     * Add compatibility with vector resources
     */
    fun getBitmap(context: Context, resId: Int, color: Int?): Bitmap? {
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
            bitmap
        }
    }

    fun getIcon(marker: SKMapVC.Marker, selected: Boolean): BitmapDescriptor? {
        val hash = marker.iconHash(selected)

        return when (marker) {
            is SKMapVC.IconMarker -> {
                memoryCache.get(hash)?.bitmapDescriptor?.apply {
                    SKLog.d("icon : get icon bitmap ${marker.id} from cache with hash $hash")
                } ?: kotlin.run {
                    if (selected) {
                        getBitmap(context, marker.selectedIcon.res, null)?.let {
                            SKMapView.BitmapDescriptorContainer(it).let {
                                SKLog.d("icon : put icon bitmap ${marker.id} in cache  with hash $hash")
                                memoryCache.put(hash, it)
                                it.bitmapDescriptor
                            }
                        }
                    } else {
                        getBitmap(context, marker.normalIcon.res, null)?.let {
                            SKMapView.BitmapDescriptorContainer(it).let {
                                SKLog.d("icon : put icon bitmap ${marker.id} in cache  with hash $hash")
                                memoryCache.put(hash, it)
                                it.bitmapDescriptor
                            }
                        }
                    }
                }

            }
            is SKMapVC.ColorizedIconMarker -> {
                memoryCache.get(hash)?.bitmapDescriptor?.apply {
                    SKLog.d("icon : get colorized bitmap ${marker.id} from cache with hash $hash")
                } ?: kotlin.run {
                    if (selected) {
                        getBitmap(context, marker.icon.res, marker.selectedColor.res)?.let {
                            SKMapView.BitmapDescriptorContainer(it).let {
                                SKLog.d("icon : put colorized bitmap ${marker.id} in cache  with hash $hash")
                                memoryCache.put(hash, it)
                                it.bitmapDescriptor
                            }
                        }
                    } else {
                        getBitmap(context, marker.icon.res, marker.normalColor.res)?.let {
                            SKMapView.BitmapDescriptorContainer(it).let {
                                SKLog.d("icon : put colorized bitmap ${marker.id} in cache  with hash $hash")
                                memoryCache.put(hash, it)
                                it.bitmapDescriptor
                            }
                        }
                    }
                }
            }
            is SKMapVC.CustomMarker -> {
                memoryCache.get(hash)?.bitmapDescriptor?.apply {
                    SKLog.d("icon : get custom bitmap ${marker.id} from cache with hash $hash")
                } ?: kotlin.run {
                    onCreateCustomMarkerIcon?.invoke(marker, selected)?.let {
                        SKMapView.BitmapDescriptorContainer(it).let {
                            SKLog.d("icon : put custom bitmap ${marker.id} in cache  with hash $hash")
                            memoryCache.put(hash, it)
                            it.bitmapDescriptor
                        }
                    }
                        ?: throw NoSuchFieldException("onCreateCustomMarkerIcon must not be null with CustomMarker")
                }
            }
        }
    }
}