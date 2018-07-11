import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

fun SQLDelete(user_id:Int, pass:String): Boolean {
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
    var result:Boolean
    if (checkPass(user_id, pass)) {
        transaction {
            user_list.deleteWhere { user_list.user_id eq user_id }
        }
        result=true
    } else {
        result=false
    }
    return result
}