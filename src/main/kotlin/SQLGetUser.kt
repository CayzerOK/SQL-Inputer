import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun SQLGetID(email: String): Int {
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
    var user_id:Int = 0
    transaction {
        user_list.select { user_list.user_email eq email }.forEach {
            user_id = it[user_list.user_id]
        }
    }
    return user_id
}

data class UserPubData(val user_email:String, val user_name:String, val avatar_url:String)

fun SQLGetUserData(user_id:Int): UserPubData {
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
    var result = UserPubData("ERROR","ERROR","ERROR")
    transaction {
        user_list.select { user_list.user_id eq user_id }.forEach {
            result = UserPubData(it[user_list.user_name],it[user_list.user_email],it[user_list.avatar_url])
        }
    }
    return result
}