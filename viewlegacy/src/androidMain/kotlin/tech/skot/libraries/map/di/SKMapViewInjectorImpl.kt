package tech.skot.libraries.map.di


import tech.skot.core.di.BaseInjector
import tech.skot.core.di.module
import tech.skot.libraries.map.*
import tech.skot.libraries.map.view.Permissions
import tech.skot.libraries.map.view.PermissionsImpl
import tech.skot.libraries.map.LatLng


class SKMapViewInjectorImpl : SKMapViewInjector {
    override fun sKMap(
        mapInteractionSettingsInitial: SKMapVC.MapInteractionSettings,
        markersInitial: List<SKMapVC.Marker>,
        linesInitial: List<SKMapVC.Polyline>,
        polygonsInitial: List<SKMapVC.Polygon>,
        selectedMarkerInitial: SKMapVC.Marker?,
        selectMarkerOnClickInitial: Boolean,
        unselectMarkerOnMapClickInitial: Boolean,
        onMarkerClickInitial: Function1<SKMapVC.Marker, Unit>?,
        onMarkerSelectedInitial: Function1<SKMapVC.Marker?, Unit>?,
        onMapClickedInitial: Function1<LatLng, Unit>?,
        onMapLongClickedInitial: Function1<LatLng, Unit>?,
        onMapBoundsChangeInitial: Function1<SKMapVC.LatLngBounds, Unit>?,
        showLogInitial: Boolean,
        mapTypeInitital : MapType
    ): SKMapVC =
        SKMapViewProxy(
            mapInteractionSettingsInitial,
            markersInitial,
            linesInitial,
            polygonsInitial,
            selectedMarkerInitial,
            selectMarkerOnClickInitial,
            unselectMarkerOnMapClickInitial,
            onMarkerClickInitial,
            onMarkerSelectedInitial,
            onMapClickedInitial,
            onMapLongClickedInitial,
            onMapBoundsChangeInitial,
            showLogInitial,
            mapTypeInitital
        )
}


val skmapModule = module<BaseInjector> {
    single<SKMapViewInjector> { SKMapViewInjectorImpl() }
    single<Permissions> { PermissionsImpl() }
    single<tech.skot.libraries.map.DeclaredPermissionHelper> { DeclaredPermissionHelperImpl(get()) }
}