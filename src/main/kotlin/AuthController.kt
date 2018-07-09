import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.clear
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import kotlinx.coroutines.experimental.*

@Location("/login") data class LoginData(val email:String, val password: String)

fun Route.LogoutUser() {
    get("/logout") {
        val session = call.sessions.get<SessionData>() ?: SessionData(0)
        call.sessions.clear<SessionData>()
        call.respond(HttpStatusCode.OK)
    }
}
fun Route.LoginUser() {
    get<LoginData> { ld ->
        val session = call.sessions.get<SessionData>() ?: SessionData(0)
        if (checkPass(ld.email, ld.password)) {
            call.sessions.set(session.copy(userID = SQLGetID(ld.email)))
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
    get("/status") {
        val session = call.sessions.get<SessionData>() ?: SessionData(0)
        call.respondText(SQLGetUserData(session.userID, "email"))
    }
}