package tech.skot.libraries.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.MapView

abstract class MapInteractionHelper(
    val context: Context,
    val mapView: MapView) {

    abstract fun onSelectedMarker(selectedMarker: SKMapVC.Marker?)
//    val getIcon : (SKMapVC.Marker, selected : Boolean) -> Bitmap?
//    val getClusterIcon : ((List<SKMapVC.Marker>) -> Bitmap?)?
    abstract var onMarkerSelected: ((SKMapVC.Marker?) -> Unit)?
    abstract fun addMarkers(markers: List<SKMapVC.Marker>)
    abstract fun onOnMapBoundsChange(onMapBoundsChange: ((SKMapVC.MapBounds) -> Unit)?)
    abstract var onMarkerClick: ((SKMapVC.Marker) -> Unit)?
    abstract var onCreateCustomMarkerIcon : ((SKMapVC.CustomMarker, selected : Boolean) -> Bitmap?)?


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


    fun getIcon(marker: SKMapVC.Marker, selected: Boolean): Bitmap? {
        return when (marker) {
            is SKMapVC.IconMarker -> {
                if (selected) {
                    getBitmap(context, marker.selectedIcon.res, null)
                } else {
                    getBitmap(context, marker.normalIcon.res, null)
                }
            }
            is SKMapVC.ColorizedIconMarker -> {
                if (selected) {
                    getBitmap(context, marker.icon.res, marker.selectedColor.res)
                } else {
                    getBitmap(context, marker.icon.res, marker.normalColor.res)
                }
            }
            is SKMapVC.CustomMarker -> {
                onCreateCustomMarkerIcon?.invoke(marker, selected)
                    ?: throw NoSuchFieldException("onCreateCustomMarkerIcon must not be null with CustomMarker")
            }
        }
    }
}