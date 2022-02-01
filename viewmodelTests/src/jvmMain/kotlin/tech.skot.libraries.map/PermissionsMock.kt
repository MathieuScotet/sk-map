package tech.skot.libraries.map

import tech.skot.core.view.SKPermission
import tech.skot.core.view.SKPermissionMock
import tech.skot.libraries.map.view.Permissions

class PermissionsMock : Permissions {
    override val coarseLocation: SKPermission = SKPermissionMock("coarseLocation")
    override val fineLocation: SKPermission = SKPermissionMock("fineLocation")
}