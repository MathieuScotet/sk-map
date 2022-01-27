package tech.skot.libraries.map.di

import tech.skot.core.di.get
import tech.skot.libraries.map.LatLng
import tech.skot.libraries.map.SKMapVC

interface SKMapViewInjector {
    fun sKMap(
        markersInitial: List<SKMapVC.Marker>,
        selectedMarkerInitial: SKMapVC.Marker?,
        selectMarkerOnClickInitial: Boolean,
        unselectMarkerOnMapClickInitial: Boolean,
        onMarkerClickInitial: Function1<SKMapVC.Marker, Unit>?,
        onMarkerSelectedInitial: Function1<SKMapVC.Marker?, Unit>?,
        onMapClickedInitial: Function1<LatLng, Unit>?,
        onMapBoundsChangeInitial: Function1<SKMapVC.MapBounds, Unit>?,
    ): SKMapVC
}

val skmapViewInjector: SKMapViewInjector = get()