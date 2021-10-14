package tech.skot.libraries.map.di

import tech.skot.core.di.get
import tech.skot.libraries.map.SKMapVC

interface SKMapViewInjector {
    fun sKMap(
        onMarkerClick: Function1<SKMapVC.Marker, Unit>,
        itemsInitial: List<SKMapVC.Marker>,
        onMapClickedInitial: Function1<Pair<Double, Double>, Unit>?,
        selectedMarkerInitial: SKMapVC.Marker?
    ): SKMapVC
}

val skmapViewInjector: SKMapViewInjector = get()