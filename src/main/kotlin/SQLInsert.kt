import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.*

import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun SQLInsert(email:String, username:String, password:String): HttpStatusCode {
    val salt1 = saltGenerator(6)
    val salt2 = saltGenerator(6)
    val hashedpass = hashit(password, salt1, salt2)
    try {
        transaction {
            val NewUser = UserData.new {
                userEmail = email
                userName = username
                userPass = hashedpass
                baseSalt1 = salt1
                baseSalt2 = salt2
            }
        }
    } catch (e: java.sql.SQLIntegrityConstraintViolationException) {
        println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)+" User $username not registred")
        return HttpStatusCode.BadRequest
    }
    println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)+" User $username registred. UserID = ${SQLGetID(email)}")
    return HttpStatusCode.OK
}