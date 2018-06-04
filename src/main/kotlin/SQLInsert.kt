import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun sqlInsert(email:String, nickname:String, password:String): Boolean {
    val salt1 = saltGenerator(6)
    val salt2 = saltGenerator(6)
    val hashedpass = hashit(password, salt1, salt2)
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
        try {
            transaction {
                user_list.insert {
                    it[user_email] = email
                    it[user_name] = nickname
                    it[user_pass] = hashedpass
                    it[base_salt1] = salt1
                    it[base_salt2] = salt2
                }
            }
        } catch (e: java.sql.SQLIntegrityConstraintViolationException) {
            return false
        }
        return true
}