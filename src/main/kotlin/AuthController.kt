import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Route.LogoutUser() {
    get("/logout") {
        val session = call.sessions.get<SessionData>() ?: SessionData(0,"Guest")
        if (session.userID == 0){
            call.sessions.clear<SessionData>()
            call.respond(HttpStatusCode.OK)
        } else call.respond(HttpStatusCode.Unauthorized)
    }
}
fun Route.LoginUser() {
    get<lLoginData> { loginCall ->
        val session = call.sessions.get<SessionData>() ?: SessionData(0,"Guest")
        if (checkPass(SQLGetID(loginCall.email), loginCall.password)) {
            val userData = SQLGetFullData(SQLGetID(loginCall.email))
            call.sessions.set(session.copy(userData.userID,userData.role))
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
            session.userID == 0 -> call.respond(HttpStatusCode.Unauthorized)
            User.haveFullAccess -> call.respond(SQLGetFullUserData(session.userID))
            !User.haveFullAccess -> call.respond(SQLGetUserData(session.userID))
        }
    }
    get<lGetUsers> {usersCall ->
        when(User.haveFullAccess){
            true -> call.respond(SQLGetFullUsers(usersCall.page, usersCall.limit))
            false -> call.respond(SQLGetUsers(usersCall.page, usersCall.limit))
        }
    }
    get<lUser> {userCall ->
        when(User.haveFullAccess){
            true -> call.respond(SQLGetFullUserData(SQLGetID(userCall.email)))
            false -> call.respond(SQLGetUserData(SQLGetID(userCall.email)))
        }
    }
}