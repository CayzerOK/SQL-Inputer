import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun SQLGetID(email: String): Int {
    Database.connect(baseURL, baseDriver, baseRoot, basePass)
    System.out.println("[MySQL] Connected")
    var user_id = 0
    transaction {
        user_list.select { user_list.userEmail eq email }.forEach {
            user_id = it[user_list.userID]
        }
    }
    return user_id
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
    System.out.println("[MySQL] Connected")
    var result = UserPublicData("","","","")
    transaction {
        user_list.select { user_list.userID eq userID }.forEach {
            result = UserPublicData(
                    it[user_list.userEmail],
                    it[user_list.userName],
                    it[user_list.avatarURL],
                    it[user_list.role])
        }
    }
    return result
}
fun SQLGetFullUserData(userID:Int): UserFullData {
    Database.connect(baseURL, baseDriver, baseRoot, basePass)
    System.out.println("[MySQL] Connected")
    var result = UserFullData(0,"","","","",false, false)
    transaction {
        user_list.select { user_list.userID eq userID }.forEach {
            result = UserFullData(
                    it[user_list.userID],
                    it[user_list.userEmail],
                    it[user_list.userName],
                    it[user_list.avatarURL],
                    it[user_list.role],
                    it[user_list.ban],
                    it[user_list.mute])
        }
    }
    return result
}


fun SQLGetFullData(userID:Int): UserFullData {
    Database.connect(baseURL, baseDriver, baseRoot, basePass)
    System.out.println("[MySQL] Connected")
    var result = UserFullData(0,"","","","",false, false)
    transaction {
        user_list.select { user_list.userID eq userID }.forEach {
            result = UserFullData(
                    it[user_list.userID],
                    it[user_list.userEmail],
                    it[user_list.userName],
                    it[user_list.avatarURL],
                    it[user_list.role],
                    it[user_list.ban],
                    it[user_list.mute])
        }
    }
    return result
}


fun SQLGetUsers(page:Int, limit:Int): MutableList<UserPublicData> {
    Database.connect(baseURL, baseDriver, baseRoot, basePass)
    System.out.println("[MySQL] Connected")
    val userlist = mutableListOf<UserPublicData>()
    transaction {
        for (users in user_list.selectAll().limit(limit, limit*page-limit-1)) {
            userlist.add(UserPublicData(
                    users[user_list.userEmail],
                    users[user_list.userName],
                    users[user_list.avatarURL],
                    users[user_list.role]))
        }
    }
    return userlist
}

fun SQLGetFullUsers(page:Int, limit:Int): MutableList<UserFullData> {
    Database.connect(baseURL, baseDriver, baseRoot, basePass)
    System.out.println("[MySQL] Connected")
    val userlist = mutableListOf<UserFullData>()
    transaction {
        for (users in user_list.selectAll().limit(limit, limit*page-limit-1)) {
            userlist.add(UserFullData(
                    users[user_list.userID],
                    users[user_list.userEmail],
                    users[user_list.userName],
                    users[user_list.avatarURL],
                    users[user_list.role],
                    users[user_list.ban],
                    users[user_list.ban]))
        }
    }
    return userlist
}