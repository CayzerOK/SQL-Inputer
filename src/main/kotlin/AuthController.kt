import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Route.LogoutUser() {
    get("/logout") {
        val session = call.sessions.get<SessionData>() ?: SessionData(0,"Guest")
        if (session.user_id == 0){
            call.sessions.clear<SessionData>()
            call.respond(HttpStatusCode.OK)
        } else call.respond(HttpStatusCode.Unauthorized)
    }
}
fun Route.LoginUser() {
    get<LoginData> { ld ->
        val session = call.sessions.get<SessionData>() ?: SessionData(0,"Guest")
        if (checkPass(SQLGetID(ld.email), ld.password)) {
            val userData = SQLGetFullData(SQLGetID(ld.email))
            call.sessions.set(session.copy(userData.user_id,userData.role))
            call.respond(HttpStatusCode.OK)
            println()
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}
fun Route.Users() {

    get("/profile") {
        val session = call.sessions.get<SessionData>() ?: SessionData(0,"Guest")

        when {
            session.user_id == 0 -> call.respond(HttpStatusCode.Unauthorized)
            User.haveFullAccess -> call.respond(SQLGetFullUserData(session.user_id))
            !User.haveFullAccess -> call.respond(SQLGetUserData(session.user_id))
        }
    }
    get<GetUsers> {gu ->
        val session = call.sessions.get<SessionData>() ?: SessionData(0,"Guest")
        when(User.haveFullAccess){
            true -> call.respond(SQLGetFullUsers(gu.page, gu.limit))
            false -> call.respond(SQLGetUsers(gu.page, gu.limit))
        }
    }
    get<User> {gud ->
        val session = call.sessions.get<SessionData>() ?: SessionData(0,"Guest")
        when(User.haveFullAccess){
            true -> call.respond(SQLGetFullUserData(SQLGetID(gud.email)))
            false -> call.respond(SQLGetUserData(SQLGetID(gud.email)))
        }
    }
}