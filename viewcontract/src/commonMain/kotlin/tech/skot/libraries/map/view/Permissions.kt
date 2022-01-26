package tech.skot.libraries.map.view

import tech.skot.core.view.SKPermission

interface Permissions {
    val coarseLocation : SKPermission?
    val fineLocation : SKPermission?
}
