import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun SQLGetID(email: String): Int {
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
    var user_id = 0
    transaction {
        user_list.select { user_list.user_email eq email }.forEach {
            user_id = it[user_list.user_id]
        }
    }
    return user_id
}

data class UserPublicData(val user_email:String,
                          val user_name:String,
                          val avatar_url:String,
                          val role: String)

data class UserFullData(val user_id: Int,
                        val user_email:String,
                        val user_name:String,
                        val avatar_url:String,
                        val role:String,
                        val ban:Boolean,
                        val mute:Boolean)

fun SQLGetUserData(user_id:Int): UserPublicData {
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
    var result = UserPublicData("","","","")
    transaction {
        user_list.select { user_list.user_id eq user_id }.forEach {
            result = UserPublicData(
                    it[user_list.user_email],
                    it[user_list.user_name],
                    it[user_list.avatar_url],
                    it[user_list.role])
        }
    }
    return result
}
fun SQLGetFullUserData(user_id:Int): UserFullData {
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
    var result = UserFullData(0,"","","","",false, false)
    transaction {
        user_list.select { user_list.user_id eq user_id }.forEach {
            result = UserFullData(
                    it[user_list.user_id],
                    it[user_list.user_email],
                    it[user_list.user_name],
                    it[user_list.avatar_url],
                    it[user_list.role],
                    it[user_list.ban],
                    it[user_list.mute])
        }
    }
    return result
}


fun SQLGetFullData(user_id:Int): UserFullData {
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
    var result = UserFullData(0,"","","","",false, false)
    transaction {
        user_list.select { user_list.user_id eq user_id }.forEach {
            result = UserFullData(
                    it[user_list.user_id],
                    it[user_list.user_email],
                    it[user_list.user_name],
                    it[user_list.avatar_url],
                    it[user_list.role],
                    it[user_list.ban],
                    it[user_list.mute])
        }
    }
    return result
}


fun SQLGetUsers(page:Int, limit:Int): MutableList<UserPublicData> {
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
    val userlist = mutableListOf<UserPublicData>()
    transaction {
        for (users in user_list.selectAll().limit(limit, limit*page-limit-1)) {
            userlist.add(UserPublicData(
                    users[user_list.user_email],
                    users[user_list.user_name],
                    users[user_list.avatar_url],
                    users[user_list.role]))
        }
    }
    return userlist
}

fun SQLGetFullUsers(page:Int, limit:Int): MutableList<UserFullData> {
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
    val userlist = mutableListOf<UserFullData>()
    transaction {
        for (users in user_list.selectAll().limit(limit, limit*page-limit-1)) {
            userlist.add(UserFullData(
                    users[user_list.user_id],
                    users[user_list.user_email],
                    users[user_list.user_name],
                    users[user_list.avatar_url],
                    users[user_list.role],
                    users[user_list.ban],
                    users[user_list.ban]))
        }
    }
    return userlist
}