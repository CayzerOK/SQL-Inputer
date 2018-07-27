import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun checkPass(userID:Int, pass:String): Boolean {
    Database.connect(baseURL, baseDriver, baseRoot, basePass)
    var result = false
        transaction {
            try {
                user_list.select { user_list.userID eq userID }.forEach {
                    val basehash = it[user_list.userPass]
                    val basesalt1 = it[user_list.baseSalt1]
                    val basesalt2 = it[user_list.baseSalt2]
                    if (hashit(pass, basesalt1, basesalt2).equals(basehash)) {
                        result = true
                        println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)+" User $userID. Password checked")
                    } else {
                        result = false
                        println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)+" User $userID. Password incorrect")
                    }
                }
            }catch (e:Exception){
                result = false
            }
        }
    return result
}