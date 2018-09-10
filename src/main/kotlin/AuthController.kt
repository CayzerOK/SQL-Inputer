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
        when {
            User.haveFullAccess -> call.respond(SQLGetFullUserData(session.userID))
            !User.haveFullAccess -> call.respond(SQLGetUserData(session.userID))
        }
    }
}
fun Route.LoginUser() {
    post<lLoginData> { loginCall ->
        val userData = SQLGetFullUserData(SQLGetID(loginCall.email))
        if (checkPass(userData.userID, loginCall.password)) {
            call.sessions.set(SessionData(userData.userID, userData.role!!))
            val session = call.sessions.get<SessionData>() ?: SessionData(0, "Guest")
            call.respond(HttpStatusCode.OK)
            println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                    " ${session.role} ${session.userID}: login")
        } else {
            call.respond(HttpStatusCode.BadRequest)
            println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                    "Guest 0: login denied")
        }
    }
}
fun Route.Users() {
    get("/profile") {
        val session = call.sessions.get<SessionData>() ?: SessionData(0, "Guest")
        when {
            session.userID == 0 -> call.respond(HttpStatusCode.Unauthorized )
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