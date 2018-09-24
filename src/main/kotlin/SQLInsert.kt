import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.*

import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun SQLInsert(email:String, username:String, password:String): HttpStatusCode {
    when {
        !isEmailValid(email) -> return HttpStatusCode(400, "Email Is Not Valid")
        username.length < 6 -> return HttpStatusCode(400, "UserName Is Not Valid")
        password.length < 6 -> return HttpStatusCode(400, "Password Is Not Valid")
    }
    val salt1 = saltGenerator(6)
    val salt2 = saltGenerator(6)
    val hashedpass = hashit(password, salt1, salt2)
    transaction {
        UserData.new {
            userEmail = email
            userName = username
            userPass = hashedpass
            baseSalt1 = salt1
            baseSalt2 = salt2
        }
    }
    println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)+" User $username registred. UserID = ${SQLGetID(email)}")
    return HttpStatusCode.OK
}