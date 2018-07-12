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

data class UserPublicData(val user_email:String, val user_name:String, val avatar_url:String)

fun SQLGetUserData(user_id:Int): UserPublicData {
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
    var result = UserPublicData("401","","")
    transaction {
        user_list.select { user_list.user_id eq user_id }.forEach {
            result = UserPublicData(it[user_list.user_name],it[user_list.user_email],it[user_list.avatar_url])
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
            userlist.add(UserPublicData(users[user_list.user_name],users[user_list.user_email],users[user_list.avatar_url]))
        }
    }
    //for (n in 0..userlist.size-1) {
      //  println(userlist[n].user_name + " | " + userlist[n].user_email)
    //}
    return userlist
}