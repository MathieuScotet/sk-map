package tech.skot.libraries.map.di

import tech.skot.core.di.InjectorMock
import tech.skot.core.di.module
import tech.skot.libraries.map.SKMapVC
import tech.skot.libraries.map.SKMapViewMock
import tech.skot.libraries.map.view.Permissions

class SKMapViewInjectorMock : SKMapViewInjector {
    override fun sKMap(
        onMarkerClick: (SKMapVC.Marker) -> Unit,
        itemsInitial: List<SKMapVC.Marker>,
        onMapClickedInitial: ((Pair<Double, Double>) -> Unit)?,
        selectedMarkerInitial: SKMapVC.Marker?
    ): SKMapVC {
        return SKMapViewMock(
            onMarkerClick,
            itemsInitial,
            onMapClickedInitial,
            selectedMarkerInitial
        )
    }

}
var skMapModuleMock = module<InjectorMock> {
    single<SKMapViewInjector> { SKMapViewInjectorMock() }
}