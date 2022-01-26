package tech.skot.libraries.map.view

import android.Manifest
import tech.skot.view.SKPermissionAndroid

class PermissionsImpl : Permissions {
    override val coarseLocation = SKPermissionAndroid(Manifest.permission.ACCESS_COARSE_LOCATION)
    override val fineLocation = SKPermissionAndroid(Manifest.permission.ACCESS_FINE_LOCATION)


}
