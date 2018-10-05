import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun SQLCheckPass(userID: Int?, pass:String): Boolean {
    var result = false
        transaction {
                val target = UserData.findById(userID!!)
                    val basehash = target?.userPass
                    val basesalt1 = target!!.baseSalt1
                    val basesalt2 = target.baseSalt2
                    if (hashit(pass, basesalt1, basesalt2).equals(basehash)) {
                        result = true
                        println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)+" User $userID: Password checked")
                    } else {
                        result = false
                        println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)+" User $userID: Wrong login data")
                    }
        }
    return result
}