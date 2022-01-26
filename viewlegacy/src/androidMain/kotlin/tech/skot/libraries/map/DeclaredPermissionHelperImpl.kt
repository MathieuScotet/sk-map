package tech.skot.libraries.map

import android.content.Context
import android.content.pm.PackageManager
import tech.skot.core.view.SKPermission
import tech.skot.view.SKPermissionAndroid

class DeclaredPermissionHelperImpl(
    private val context: Context
) : DeclaredPermissionHelper {
    override fun isPermissionDeclaredForApp(permission: SKPermission): Boolean {
        val name = (permission as? SKPermissionAndroid?)?.name
        val packageName = context.packageName
        try {
            val packageInfo =
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_PERMISSIONS
                )
            val declaredPermissions = packageInfo.requestedPermissions
            if (declaredPermissions != null && declaredPermissions.isNotEmpty()) {
                for (p in declaredPermissions) {
                    if (p.equals(name, ignoreCase = true)) {
                        return true
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return false
        }
        return false
    }
}