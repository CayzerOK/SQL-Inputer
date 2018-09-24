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
        val userData = SQL.GetFullUserData(SQL.GetUserID(loginCall.email))
        if (userData.ban == true) {
            call.respond(HttpStatusCode(410, "Banned"))
        }
        if (SQL.CheckPass(userData.userID!!, loginCall.password)) {
            call.sessions.set(SessionData(userData.userID, userData.role!!))
            val session = call.sessions.get<SessionData>() ?: SessionData(0, "Guest")
            call.respond(HttpStatusCode.OK)
            println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                    " ${session.role} ${session.userID}: login")
        } else {
            call.respond(HttpStatusCode(400,"Wrong Password"))
            println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                    "Guest 0: login denied")
        }
    }
}