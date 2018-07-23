data class UserRights(
        val haveFullAccess:Boolean,
        val accessTo:List<String>,
        val canUpdate:Boolean,
        val canDelete:Boolean,
        val canBan:Boolean,
        val canMute:Boolean)

fun GetRights(role:String): UserRights {
    var result = UserRights(false, listOf(""),false,false,false,false)
    when(role){
        "Guest" -> result = UserRights(
                haveFullAccess = false,
                accessTo = listOf("User"),
                canUpdate = false,
                canDelete = false,
                canBan = false,
                canMute = false)
        "User" -> result = UserRights(
                haveFullAccess = false,
                accessTo = listOf("User"),
                canUpdate = false,
                canDelete = false,
                canBan = false,
                canMute = false)
        "Moder" -> result = UserRights(
                haveFullAccess = false,
                accessTo = listOf("User"),
                canUpdate = true,
                canDelete = false,
                canBan = false,
                canMute = true)
        "Admin" -> result = UserRights(
                haveFullAccess = false,
                accessTo = listOf("User", "Moder"),
                canUpdate = true,
                canDelete = true,
                canBan = true,
                canMute = true)
    }
    return result
}