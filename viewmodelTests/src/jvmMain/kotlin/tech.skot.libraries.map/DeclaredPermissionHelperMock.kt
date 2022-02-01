package tech.skot.libraries.map

import tech.skot.core.view.SKPermission

class DeclaredPermissionHelperMock : DeclaredPermissionHelper {
    override fun isPermissionDeclaredForApp(permission: SKPermission): Boolean {
        return true
    }
}