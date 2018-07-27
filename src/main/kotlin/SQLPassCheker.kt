import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun checkPass(user_id:Int, pass:String): Boolean {
    Database.connect(baseURL, baseDriver, baseRoot, basePass)
    System.out.println("[MySQL] Connected")
    var result = false
        transaction {
            try {
                user_list.select { user_list.userID eq user_id }.forEach {
                    val basehash = it[user_list.userPass]
                    val basesalt1 = it[user_list.baseSalt1]
                    val basesalt2 = it[user_list.baseSalt2]
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