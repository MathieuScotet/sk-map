package tech.skot.libraries.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import androidx.collection.LruCache
import androidx.core.content.ContextCompat
import com.mapbox.maps.MapView
import tech.skot.core.toColor
import tech.skot.core.view.Color

abstract class MapInteractionHelper(
    val context: Context,
    val mapView: MapView,
    val memoryCache: LruCache<String, Bitmap>
) {

    abstract fun onSelectedMarker(selectedMarker: SKMapVC.Marker?)
    abstract var onMarkerSelected: ((SKMapVC.Marker?) -> Unit)?
    abstract fun addMarkers(markers: List<SKMapVC.Marker>)
    abstract fun addLines(polylines: List<SKMapVC.Polyline>)
    abstract fun onOnMapBoundsChange(onMapBoundsChange: ((SKMapVC.LatLngBounds) -> Unit)?)
    abstract var onMarkerClick: ((SKMapVC.Marker) -> Unit)?
    var onCreateCustomMarkerIcon: ((SKMapVC.CustomMarker, selected: Boolean) -> Bitmap?)? = null
    abstract fun onDestroy()


    /**
     * Helper method to obtain BitmapDescriptor from resource.
     * Add compatibility with vector resources
     */
    fun getBitmap(context: Context, resId: Int, color: Color?): Bitmap? {
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
                    color.toColor(context),
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


    fun getIcon(marker: SKMapVC.Marker, selected: Boolean): Bitmap? {
        val hash = marker.iconHash(selected)
        return when (marker) {
            is SKMapVC.IconMarker -> {
                memoryCache.get(hash)?.apply {
                    MapLoggerView.d("icon : get icon bitmap ${marker.id} from cache with hash $hash")
                } ?: kotlin.run {
                    if (selected) {
                        getBitmap(context, marker.selectedIcon.res, null)?.let {
                            MapLoggerView.d("icon : put icon bitmap ${marker.id} in cache  with hash $hash")
                            memoryCache.put(hash, it)
                            it
                        }
                    } else {
                        getBitmap(context, marker.normalIcon.res, null)?.let {
                            MapLoggerView.d("icon : put icon bitmap ${marker.id} in cache  with hash $hash")
                            memoryCache.put(hash, it)
                            it
                        }
                    }
                }

            }
            is SKMapVC.ColorizedIconMarker -> {
                memoryCache.get(hash)?.apply {
                    MapLoggerView.d("icon : get colorized bitmap ${marker.id} from cache with hash $hash")
                } ?: kotlin.run {
                    if (selected) {
                        getBitmap(context, marker.icon.res, marker.selectedColor)?.let {
                            MapLoggerView.d("icon : put icon bitmap ${marker.id} in cache  with hash $hash")
                            memoryCache.put(hash, it)
                            it
                        }
                    } else {
                        getBitmap(context, marker.icon.res, marker.normalColor)?.let {
                            MapLoggerView.d("icon : put icon bitmap ${marker.id} in cache  with hash $hash")
                            memoryCache.put(hash, it)
                            it
                        }
                    }
                }
            }
            is SKMapVC.CustomMarker -> {
                memoryCache.get(hash)?.apply {
                    MapLoggerView.d("icon : get custom bitmap ${marker.id} from cache with hash $hash")
                } ?: kotlin.run {
                    onCreateCustomMarkerIcon?.invoke(marker, selected)?.let {
                        MapLoggerView.d("icon : put icon bitmap ${marker.id} in cache  with hash $hash")
                        memoryCache.put(hash, it)
                        it
                    }
                        ?: throw NoSuchFieldException("onCreateCustomMarkerIcon must not be null with CustomMarker")
                }
            }
        }
    }
}