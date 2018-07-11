import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Route.LogoutUser() {
    get("/logout") {
        call.sessions.clear<SessionData>()
        call.respond(HttpStatusCode.OK)
    }
}
fun Route.LoginUser() {
    get<LoginData> { ld ->
        val session = call.sessions.get<SessionData>() ?: SessionData(0)
        if (checkPass(SQLGetID(ld.email), ld.password)) {
            call.sessions.set(session.copy(user_id = SQLGetID(ld.email)))
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
    get("/status") {
        val session = call.sessions.get<SessionData>() ?: SessionData(0)
        call.respondText(SQLGetUserData(session.user_id, "user_email")+" | "+
                              SQLGetUserData(session.user_id, "user_name"))
    }
}