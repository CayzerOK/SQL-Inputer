import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun SQLInsert(email:String, nickname:String, password:String): HttpStatusCode {
    val salt1 = saltGenerator(6)
    val salt2 = saltGenerator(6)
    val hashedpass = hashit(password, salt1, salt2)
    Database.connect(baseURL, baseDriver, baseRoot, basePass)
    System.out.println("[MySQL] Connected")
        try {
            transaction {
                user_list.insert {
                    it[userEmail] = email
                    it[userName] = nickname
                    it[userPass] = hashedpass
                    it[baseSalt1] = salt1
                    it[baseSalt2] = salt2
                }
            }
        } catch (e: java.sql.SQLIntegrityConstraintViolationException) {
            return HttpStatusCode.BadRequest
        }
        return HttpStatusCode.OK
}