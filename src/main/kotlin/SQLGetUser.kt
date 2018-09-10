
import org.jetbrains.exposed.sql.transactions.transaction

fun SQLGetID(email: String): Int {
    var result = 0
    transaction {
        val content = UserData.find {UserList.userEmail eq email}
        for (user in content) {
            result = user.userID
        }
    }
    return result
}

data class UserPublicData(val userEmail:String,
                          val userName:String,
                          val avatarURL:String,
                          val role: String,
                          val mute:Boolean)

data class UserFullData(val userID: Int?,
                        val userEmail: String?,
                        val userName: String?,
                        val avatarURL: String?,
                        val role: String?,
                        val ban: Boolean?,
                        val mute: Boolean?)

fun SQLGetUserData(userID: Int?): UserPublicData {
    var result = UserPublicData("","","","", false)
    transaction {
        val content = UserData.findById(userID!!)
        result = UserPublicData(
                content!!.userEmail,
                content!!.userName,
                content!!.avatarURL,
                content!!.userRole,
                content!!.mute)
        }
    return result
}
fun SQLGetFullUserData(userID: Int?): UserFullData {
    var result = UserFullData(0,"","","","",false, false)
    transaction {
        val content = UserData.findById(userID!!)
        result = UserFullData(
                content?.userID,
                content?.userEmail,
                content?.userName,
                content?.avatarURL,
                content?.userRole,
                content?.ban,
                content?.mute)
    }
    return result
}


fun SQLGetUsers(page:Int, limit:Int): MutableList<UserPublicData> {
    val userlist = mutableListOf<UserPublicData>()
    transaction {
        val content = UserData.all().limit(limit, limit*page-limit-1)
        for (users in content) {
            userlist.add(UserPublicData(
                    users.userEmail,
                    users.userName,
                    users.avatarURL,
                    users.userRole,
                    users.mute))
        }
    }
    return userlist
}

fun SQLGetFullUsers(page:Int, limit:Int): MutableList<UserFullData> {
    val userlist = mutableListOf<UserFullData>()
    transaction {
        val content = UserData.all().limit(limit, limit*page-limit-1)
        for (users in content) {
            userlist.add(UserFullData(
                    users.userID,
                    users.userEmail,
                    users.userName,
                    users.avatarURL,
                    users.userRole,
                    users.ban,
                    users.mute))
        }
    }
    return userlist
}