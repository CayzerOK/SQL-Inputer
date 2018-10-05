import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction

fun SQLGetID(email: String): Int {
    var result = 0
    transaction {
        val content = UserData.find {UserList.userEmail eq email}
        for (user in content) {
            result = user.userID
        }
        if (result == 0) {throw CallException(404, "User not found")}
    }
    return result
}

data class UserPublicData(val userEmail: String? =null,
                          val userName: String? =null,
                          val avatarURL: String? =null,
                          val role: String? =null,
                          val mute: Boolean? =null)

data class UserFullData(val userID: Int?=null,
                        val userEmail: String?=null,
                        val userName: String?=null,
                        val avatarURL: String?=null,
                        val role: String?=null,
                        val mute: Boolean?=null)

fun SQLGetUserData(userID: Int): UserPublicData {
    var result: UserPublicData? = null
    transaction {
        val content = UserData.findById(userID)
        result = UserPublicData(
                content!!.userEmail,
                content.userName,
                content.avatarURL,
                content.userRole,
                content.mute)
        }
    if (result?.userName == null) {throw CallException(404, "User not found")}
    return result!!
}
fun SQLGetFullUserData(userID: Int?): UserFullData {
    var result = UserFullData(0,"","","","",false)
    transaction {
        val content = UserData.findById(userID!!)
        result = UserFullData(
                content?.userID,
                content?.userEmail,
                content?.userName,
                content?.avatarURL,
                content?.userRole,
                content?.mute)
    }
    if (result.userName == null) {throw CallException(404, "User not found")}
    return result
}


fun SQLGetUsers(page:Int, limit:Int): MutableList<UserPublicData> {
        val userlist = mutableListOf<UserPublicData>()
    transaction {
        val content = UserData.find{
            UserList.role eq "User" or
                (UserList.role eq "Moder") or
                (UserList.role eq "Admin")}.limit(limit,page*limit-limit)
        for (users in content) {
            userlist.add(UserPublicData(
                    users.userEmail,
                    users.userName,
                    users.avatarURL,
                    users.userRole,
                    users.mute))
        }
        if(userlist.isEmpty()) throw CallException(404,"Out of list range")
    }
    return userlist
}

fun SQLGetFullUsers(page:Int, limit:Int): MutableList<UserFullData> {
    val userlist = mutableListOf<UserFullData>()
    transaction {
        val content = UserData.all().limit(limit, page*limit-limit)
        for (users in content) {
            userlist.add(UserFullData(
                    users.userID,
                    users.userEmail,
                    users.userName,
                    users.avatarURL,
                    users.userRole,
                    users.mute))
        }
    }
    if(userlist.isEmpty()) throw CallException(404,"Out of list range")
    return userlist
}

fun SQLLogin(email:String, password:String) : SessionData {
    val userData = SQL.GetFullUserData(SQL.GetUserID(email))
    if (!SQL.CheckPass(userData.userID!!,password)) {throw CallException(400, "Wrong Password")}
    when(userData.role){
        else -> return SessionData(userData.userID,userData.role!!)
    }
}