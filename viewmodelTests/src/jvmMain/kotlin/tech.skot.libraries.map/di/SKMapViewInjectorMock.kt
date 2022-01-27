package tech.skot.libraries.map.di

import tech.skot.core.di.InjectorMock
import tech.skot.core.di.module
import tech.skot.libraries.map.LatLng
import tech.skot.libraries.map.SKMapVC
import tech.skot.libraries.map.SKMapViewMock

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
}