import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Route.LogoutUser() {
    get("/logout") {
        val session = call.sessions.get<SessionData>() ?: SessionData(0, "Guest")
        println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                " ${session.role} ${session.userID}: logout")
        call.sessions.clear<SessionData>()
        call.respond(HttpStatusCode.OK)
    }
}
fun Route.LoginUser() {
    post<lLoginData> { loginCall ->
        if(!isEmailValid(loginCall.email)) {throw CallException(400, "Invalid Email")}
        val userData = SQL.GetFullUserData(SQL.GetUserID(loginCall.email))
        when{
            !SQL.CheckPass(userData.userID!!, loginCall.password) -> throw CallException(400, "Wrong Password")
            userData.role == "BANNED" -> throw CallException(410, "Banned")
            userData.role == "DELETED" -> throw CallException(410, "Deleted")
        }
        call.sessions.set(SessionData(userData.userID,userData.role!!))
        call.respond(HttpStatusCode.OK)
    }
}