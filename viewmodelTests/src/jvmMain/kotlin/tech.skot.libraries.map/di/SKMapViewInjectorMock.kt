package tech.skot.libraries.map.di

import tech.skot.core.di.InjectorMock
import tech.skot.core.di.module
import tech.skot.libraries.map.*
import tech.skot.libraries.map.view.Permissions

class SKMapViewInjectorMock : SKMapViewInjector {
    override fun sKMap(
        mapInteractionSettingsInitial: SKMapVC.MapInteractionSettings,
        markersInitial: List<SKMapVC.Marker>,
        linesInitial: List<SKMapVC.Polyline>,
        selectedMarkerInitial: SKMapVC.Marker?,
        selectMarkerOnClickInitial: Boolean,
        unselectMarkerOnMapClickInitial: Boolean,
        onMarkerClickInitial: ((SKMapVC.Marker) -> Unit)?,
        onMarkerSelectedInitial: ((SKMapVC.Marker?) -> Unit)?,
        onMapClickedInitial: ((LatLng) -> Unit)?,
        onMapLongClickedInitial: ((LatLng) -> Unit)?,
        onMapBoundsChangeInitial: ((SKMapVC.LatLngBounds) -> Unit)?,
        showLogInitial: Boolean
    ): SKMapVC {
        return SKMapViewMock(
            mapInteractionSettingsInitial,
            markersInitial,
            linesInitial,
            selectedMarkerInitial,
            selectMarkerOnClickInitial,
            unselectMarkerOnMapClickInitial,
            onMarkerClickInitial,
            onMapClickedInitial,
            onMapLongClickedInitial,
            onMarkerSelectedInitial,
            onMapBoundsChangeInitial,
            showLogInitial
        )
    }

}

val skMapModuleMock = module<InjectorMock> {
    single<SKMapViewInjector> { SKMapViewInjectorMock() }
    single<Permissions> { PermissionsMock() }
    single<DeclaredPermissionHelper> { DeclaredPermissionHelperMock() }

}