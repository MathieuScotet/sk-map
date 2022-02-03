package tech.skot.libraries.map.di

import tech.skot.core.di.BaseInjector
import tech.skot.core.di.module
import tech.skot.libraries.map.*
import tech.skot.libraries.map.view.Permissions
import tech.skot.libraries.map.view.PermissionsImpl

class SKMapViewInjectorImpl : SKMapViewInjector {
    override fun sKMap(
        mapInteractionSettingsInitial : SKMapVC.MapInteractionSettings,
        markersInitial: List<SKMapVC.Marker>,
        selectedMarkerInitial: SKMapVC.Marker?,
        selectMarkerOnClickInitial: Boolean,
        unselectMarkerOnMapClickInitial: Boolean,
        onMarkerClickInitial: Function1<SKMapVC.Marker, Unit>?,
        onMarkerSelectedInitial: Function1<SKMapVC.Marker?, Unit>?,
        onMapClickedInitial: Function1<LatLng, Unit>?,
        onMapBoundsChangeInitial: Function1<SKMapVC.LatLngBounds, Unit>?,
    ): SKMapVC =
        SKMapViewProxy(
            mapInteractionSettingsInitial,
            markersInitial,
            selectedMarkerInitial,
            selectMarkerOnClickInitial,
            unselectMarkerOnMapClickInitial,
            onMarkerClickInitial,
            onMarkerSelectedInitial,
            onMapClickedInitial,
            onMapBoundsChangeInitial)
}


val skmapModule = module<BaseInjector> {
    single<SKMapViewInjector> { SKMapViewInjectorImpl() }
    single<Permissions> { PermissionsImpl() }
    single<DeclaredPermissionHelper> { DeclaredPermissionHelperImpl(get()) }
}