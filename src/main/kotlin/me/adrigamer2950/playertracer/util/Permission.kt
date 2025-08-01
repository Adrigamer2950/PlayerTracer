package me.adrigamer2950.playertracer.util

import me.adrigamer2950.adriapi.api.user.User

@JvmInline
value class Permission(val permission: String) {

    fun isGrantedTo(user: User): Boolean {
        return user.hasPermission(permission) || user.isConsole()
    }

    companion object {
        val SEARCH = Permission("playertracer.search")

        val TELEPORT = Permission("playertracer.teleport")
    }
}