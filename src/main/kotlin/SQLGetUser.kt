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
fun SQLGetUserData(user_id:Int,datatype:String):String {
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
    var result = ""
    transaction {
        user_list.select { user_list.user_id eq user_id }.forEach {
            when(datatype){
                "user_email" -> result = it[user_list.user_email]
                "user_name" -> result = it[user_list.user_name]
                "avatar_url" -> result = it[user_list.avatar_url]
                else -> result = "ERROR"
            }
        }
    }
    if (result.equals("")) {
        return "Not Logined"
    }else return result
}
