package tech.skot.libraries.map.di

import tech.skot.core.di.InjectorMock
import tech.skot.core.di.module
import tech.skot.libraries.map.*
import tech.skot.libraries.map.view.Permissions

class SKMapViewInjectorMock : SKMapViewInjector {
    override fun sKMap(
        markersInitial: List<SKMapVC.Marker>,
        selectedMarkerInitial: SKMapVC.Marker?,
        selectMarkerOnClickInitial: Boolean,
        unselectMarkerOnMapClickInitial: Boolean,
        onMarkerClickInitial: ((SKMapVC.Marker) -> Unit)?,
        onMarkerSelectedInitial: ((SKMapVC.Marker?) -> Unit)?,
        onMapClickedInitial: ((LatLng) -> Unit)?,
        onMapBoundsChangeInitial: ((SKMapVC.MapBounds) -> Unit)?
    ): SKMapVC {
        return SKMapViewMock(
            markersInitial,
            selectedMarkerInitial,
            selectMarkerOnClickInitial,
            unselectMarkerOnMapClickInitial,
            onMarkerClickInitial,
            onMapClickedInitial,
            onMarkerSelectedInitial,
            onMapBoundsChangeInitial
        )
    }

}

var skMapModuleMock = module<InjectorMock> {
    single<SKMapViewInjector> { SKMapViewInjectorMock() }
    single<Permissions> { PermissionsMock() }
    single<DeclaredPermissionHelper> { DeclaredPermissionHelperMock()}

}