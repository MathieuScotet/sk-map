package tech.skot.libraries.map

import tech.skot.core.view.SKPermission

interface DeclaredPermissionHelper {
     fun isPermissionDeclaredForApp(permission: SKPermission): Boolean
}