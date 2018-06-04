import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun checkPass(email:String, pass:String): Boolean {
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
    var result = false
        transaction {
            try {
                user_list.select { user_list.user_email eq email }.forEach {
                    val basehash = it[user_list.user_pass]
                    val basesalt1 = it[user_list.base_salt1]
                    val basesalt2 = it[user_list.base_salt2]
                    if (hashit(pass, basesalt1, basesalt2).equals(basehash)) {
                        result = true
                    } else {
                        result = false
                    }
                }
            }catch (e:ExceptionInInitializerError){
                result = false
            }
        }
    return result
}