
import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

@Location("/login") data class LoginData(val email: String, val userpass:String)

data class SessionData (val session_id:String, val email: String)

fun Route.LogoutUser() {
    get("/logout") {
        call.respond(HttpStatusCode.OK)
    }
}

fun Route.LoginUser() {
    get<LoginData> { ld ->
        if (checkPass(ld.email,ld.userpass))  {
            val session_id = saltGenerator(10)
            call.sessions.set(ServerData(session_id, ld.email))

  //          val sesdata = SessionData(session_id,ld.email)

            call.respond(HttpStatusCode.OK)
        } else {
        call.respond(HttpStatusCode.BadRequest)
    }
    }
}

