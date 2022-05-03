package tech.skot.libraries.map


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import androidx.collection.LruCache
import com.mapbox.geojson.Point
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.toCameraOptions
import tech.skot.core.toColor
import tech.skot.core.view.Dimen
import tech.skot.core.view.DimenDP
import tech.skot.core.view.DimenRef

@SuppressLint("PotentialBehaviorOverride")
open class MapBoxInteractionHelper(
    context: Context,
    mapView: MapView,
    memoryCache: LruCache<String, Bitmap>
) : MapInteractionHelper(context, mapView, memoryCache) {
    private var clickListener: OnPointAnnotationClickListener? = null
    val annotationApi = mapView.annotations
    val pointAnnotationManager =
        annotationApi.createPointAnnotationManager(AnnotationConfig(layerId = "marker"))
    val lineManager = annotationApi.createPolylineAnnotationManager(
        AnnotationConfig(
            layerId = "line",
            belowLayerId = "marker"
        )
    )

    private var items: List<Pair<SKMapVC.Marker, PointAnnotation>> = emptyList()
    private var polylineItems: List<Pair<SKMapVC.Polyline, PolylineAnnotation>> = emptyList()
    private var lastSelectedMarker: Pair<SKMapVC.Marker, PointAnnotation>? = null
    override var onMarkerSelected: ((SKMapVC.Marker?) -> Unit)? = null
    override var onMarkerClick: ((SKMapVC.Marker) -> Unit)? = null

    override fun onDestroy() {
        clickListener?.let {
            pointAnnotationManager.removeClickListener(it)

        }

    }

    private fun oldLineStillAvailable(
        polyline: SKMapVC.Polyline,
        polylines: List<SKMapVC.Polyline>
    ): Boolean {
        return if (polyline.id != null) {
            polylines.any {
                polyline.id == it.id
            }
        } else {
            false
        }
    }

    private fun newLineAlreadyExist(polyline: SKMapVC.Polyline): Boolean {
        return if (polyline.id != null) {
            this.polylineItems.any {
                polyline.id == it.first.id
            }
        } else {
            false
        }
    }


    private fun oldMarkerStillAvailable(
        marker: SKMapVC.Marker,
        markers: List<SKMapVC.Marker>
    ): Boolean {
        return if (marker.id != null) {
            markers.any {
                marker.id == it.id
            }
        } else {
            false
        }
    }

    private fun newMarkerAlreadyExist(marker: SKMapVC.Marker): Boolean {
        return if (marker.id != null) {
            this.items.any {
                marker.id == it.first.id
            }
        } else {
            false
        }
    }

    override fun addLines(polylines: List<SKMapVC.Polyline>) {
        mapView.getMapboxMap { map ->

            //first parts -> items in map still exist in new markers list
            //second parts -> items in maps no longer exist in new markers list
            val (oldItemsToUpdate, oldItemsToRemove) = this.polylineItems.partition { currentItem ->
                oldLineStillAvailable(currentItem.first, polylines)
            }

            //first parts -> update
            //second parts -> add
            val (newValueForItemsToUpdate, newItemsToAdd) = polylines.partition { marker ->
                newLineAlreadyExist(marker)
            }

            //items to remove from map
            oldItemsToRemove.forEach { pair ->
                if (pair.first.id == lastSelectedMarker?.first?.id) {
                    lastSelectedMarker = null
                }
                lineManager.delete(pair.second)
            }
            //items to update on map
            val updatedMarker = oldItemsToUpdate.mapNotNull { currentPair ->
                newValueForItemsToUpdate.find {
                    it.id == currentPair.first.id
                }?.let {
                    Pair(it, currentPair.second.apply {
                        this.points = it.points.map {
                            Point.fromLngLat(
                                it.second,
                                it.first,
                            )
                        }
                        lineColorInt = it.color.toColor(context)
                        lineWidth = convertLineWidth(it.lineWidth)
                    })
                }
            }

            //items to add to map
            val optionsToAdd = newItemsToAdd.map { line ->
                PolylineAnnotationOptions()
                    .withPoints(line.points.map {
                        Point.fromLngLat(it.second, it.first)
                    })
                    .withLineWidth(
                        convertLineWidth(line.lineWidth)
                    )
                    .withLineColor(line.color.toColor(context))
            }

            val annotations = lineManager.create(options = optionsToAdd)
            val addedLines = polylines.zip(annotations)

            this.polylineItems = updatedMarker + addedLines
        }
    }

    private fun convertLineWidth(dimen: Dimen): Double {
        return when (dimen) {
            is DimenDP -> dimen.dp.toDouble()
            is DimenRef -> (context.resources.getDimension(dimen.res) / Resources.getSystem().displayMetrics.density).toDouble()
            else -> 1.0
        }

    }


    override fun addMarkers(markers: List<SKMapVC.Marker>) {
        mapView.getMapboxMap { map ->

            //first parts -> items in map still exist in new markers list
            //second parts -> items in maps no longer exist in new markers list
            val (oldItemsToUpdate, oldItemsToRemove) = this.items.partition { currentItem ->
                oldMarkerStillAvailable(currentItem.first, markers)
            }


            //first parts -> update
            //second parts -> add
            val (newValueForItemsToUpdate, newItemsToAdd) = markers.partition { marker ->
                newMarkerAlreadyExist(marker)
            }

            //items to remove from map
            oldItemsToRemove.forEach { pair ->
                if (pair.first.id == lastSelectedMarker?.first?.id) {
                    lastSelectedMarker = null
                }
                pointAnnotationManager.delete(pair.second)
            }

            //items to update on map
            val updatedMarker = oldItemsToUpdate.mapNotNull { currentPair ->
                newValueForItemsToUpdate.find {
                    it.id == currentPair.first.id
                }?.let {
                    Pair(it, currentPair.second.apply {
                        this.geometry = Point.fromLngLat(
                            it.position.second,
                            it.position.first,
                        )
                        getIcon(it, lastSelectedMarker?.first?.id == it.id)?.let {
                            this.iconImageBitmap = it
                        }
                    })
                }
            }

            val optionsToAdd = mutableListOf<PointAnnotationOptions>()
            //items to add to map
            val markers = newItemsToAdd.map { skMarker ->

                val options = PointAnnotationOptions()
                    .withGeometry(
                        Point.fromLngLat(
                            skMarker.position.second,
                            skMarker.position.first
                        )
                    )
                getIcon(skMarker, false)?.let {
                    options.withIconImage(it)
                }
                optionsToAdd.add(options)
                skMarker
            }
            val annotations = pointAnnotationManager.create(optionsToAdd)
            val addedMarkers = markers.zip(annotations)


            this.items = updatedMarker + addedMarkers
        }
    }


    init {
        mapView.getMapboxMap {
            clickListener = OnPointAnnotationClickListener { annotation ->
                val item = items.find { (_, marker) ->
                    marker == annotation
                }
                item?.let {
                    onMarkerClick?.invoke(item.first)
                }
                true
            }.apply {
                pointAnnotationManager.addClickListener(this)
            }


        }
    }

    override fun onOnMapBoundsChange(onMapBoundsChange: ((SKMapVC.LatLngBounds) -> Unit)?) {
        mapView.getMapboxMap { mapBox ->
            mapBox.addOnCameraChangeListener {
                val cameraState = mapBox.cameraState
                val bounds = mapBox.coordinateBoundsForCamera(cameraState.toCameraOptions())

                onMapBoundsChange?.invoke(getMapBounds(bounds))
            }
        }
    }

    protected fun getMapBounds(bounds: CoordinateBounds): SKMapVC.LatLngBounds {
        return SKMapVC.LatLngBounds(
            bounds.north() to bounds.east(),
            bounds.south() to bounds.west()
        )
    }


    override fun onSelectedMarker(selectedMarker: SKMapVC.Marker?) {
        onMarkerSelected?.invoke(selectedMarker)
        mapView.getMapboxMap { mapBoxMap ->
            lastSelectedMarker?.let { current ->
                getIcon(current.first, false)?.let {
                    current.second.iconImage = null
                    current.second.iconImageBitmap = it
                    current.second.iconOpacity = 1.0
                    pointAnnotationManager.update(current.second)
                }
            }
            lastSelectedMarker = items.find {
                it.first.id == selectedMarker?.id
            }?.also { newSelectedMarker ->
                getIcon(newSelectedMarker.first, true)?.let {

                    newSelectedMarker.second.iconImage = null
                    newSelectedMarker.second.iconImageBitmap = it
                    newSelectedMarker.second.iconOpacity = 1.0

                    pointAnnotationManager.update(newSelectedMarker.second)

                }
            }
        }
    }


}