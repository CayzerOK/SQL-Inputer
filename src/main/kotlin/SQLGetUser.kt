
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun SQLGetID(email: String): Int {
    Database.connect(baseURL, baseDriver, baseRoot, basePass)
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
                          val role: String)

data class UserFullData(val userID: Int,
                        val user_email:String,
                        val user_name:String,
                        val avatar_url:String,
                        val role:String,
                        val ban:Boolean,
                        val mute:Boolean)

fun SQLGetUserData(userID:Int): UserPublicData {
    Database.connect(baseURL, baseDriver, baseRoot, basePass)
    var result = UserPublicData("","","","")
    transaction {
        val content = UserData.findById(userID)
        result = UserPublicData(
                content!!.userEmail,
                content!!.userName,
                content!!.avatarURL,
                content!!.userRole)
        }
    return result
}
fun SQLGetFullUserData(userID:Int): UserFullData {
    Database.connect(baseURL, baseDriver, baseRoot, basePass)
    var result = UserFullData(0,"","","","",false, false)
    transaction {
        val content = UserData.findById(userID)
        result = UserFullData(
                content!!.userID,
                content!!.userEmail,
                content!!.userName,
                content!!.avatarURL,
                content!!.userRole,
                content!!.ban,
                content!!.mute)
    }
    return result
}


fun SQLGetUsers(page:Int, limit:Int): MutableList<UserPublicData> {
    Database.connect(baseURL, baseDriver, baseRoot, basePass)
    val userlist = mutableListOf<UserPublicData>()
    transaction {
        val content = UserData.all().limit(limit, limit*page-limit-1)
        for (users in content) {
            userlist.add(UserPublicData(
                    users.userEmail,
                    users.userName,
                    users.avatarURL,
                    users.userRole))
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