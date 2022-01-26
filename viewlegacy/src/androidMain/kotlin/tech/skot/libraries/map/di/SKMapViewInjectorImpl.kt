package tech.skot.libraries.map.di

import tech.skot.libraries.map.DeclaredPermissionHelperImpl
import tech.skot.core.di.BaseInjector
import tech.skot.core.di.module
import tech.skot.libraries.map.DeclaredPermissionHelper
import tech.skot.libraries.map.SKMapVC
import tech.skot.libraries.map.SKMapViewProxy
import tech.skot.libraries.map.view.Permissions
import tech.skot.libraries.map.view.PermissionsImpl

class SKMapViewInjectorImpl : SKMapViewInjector {
    override fun sKMap(
        onMarkerClick: Function1<SKMapVC.Marker, Unit>,
        itemsInitial: List<SKMapVC.Marker>,
        onMapClickedInitial: Function1<Pair<Double, Double>, Unit>?,
        selectedMarkerInitial: SKMapVC.Marker?
    ): SKMapVC =
        SKMapViewProxy(onMarkerClick, itemsInitial, onMapClickedInitial, selectedMarkerInitial)
}


val skmapModule = module<BaseInjector> {
    single<SKMapViewInjector> { SKMapViewInjectorImpl() }
    single<Permissions> { PermissionsImpl() }
    single<DeclaredPermissionHelper> { DeclaredPermissionHelperImpl(get()) }
}