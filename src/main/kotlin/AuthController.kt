
import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

@Location("/login") data class LoginData(val email: String, val userpass:String)

fun Route.LoginUser() {
    get<LoginData> { ld ->
        if (checkPass(ld.email,ld.userpass))  {
            val session_id = saltGenerator(10)
            call.sessions.set(session_id)
            call.respond(HttpStatusCode.OK)
        } else {
        call.respond(HttpStatusCode.BadRequest)
    }
    }
}

fun Route.LogoutUser() {
    get("/logout") {
        call.respond(HttpStatusCode.OK)
    }
}