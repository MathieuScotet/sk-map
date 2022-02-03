package tech.skot.libraries.map.di

import tech.skot.core.di.InjectorMock
import tech.skot.core.di.module
import tech.skot.libraries.map.LatLng
import tech.skot.libraries.map.SKMapVC
import tech.skot.libraries.map.SKMapViewMock
import tech.skot.libraries.map.view.Permissions
import tech.skot.libraries.map.*

class SKMapViewInjectorMock : SKMapViewInjector {
    override fun sKMap(
        mapInteractionSettingsInitial: SKMapVC.MapInteractionSettings,
        markersInitial: List<SKMapVC.Marker>,
        selectedMarkerInitial: SKMapVC.Marker?,
        selectMarkerOnClickInitial: Boolean,
        unselectMarkerOnMapClickInitial: Boolean,
        onMarkerClickInitial: ((SKMapVC.Marker) -> Unit)?,
        onMarkerSelectedInitial: ((SKMapVC.Marker?) -> Unit)?,
        onMapClickedInitial: ((LatLng) -> Unit)?,
        onMapBoundsChangeInitial: ((SKMapVC.LatLngBounds) -> Unit)?
    ): SKMapVC {
        return SKMapViewMock(
            mapInteractionSettingsInitial,
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