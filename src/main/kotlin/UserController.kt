import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.header
import io.ktor.sessions.*

fun Route.AddUser() {
    put<lRegData> { regCall ->
        when {
            !isEmailValid(regCall.email) -> call.respond(HttpStatusCode.BadRequest, "Email Is Not Valid")
            regCall.username.length < 6 -> call.respond(HttpStatusCode.BadRequest, "UserName Is Not Valid")
            regCall.password.length < 6 -> call.respond(HttpStatusCode.BadRequest, "Password Is Not Valid")
        }
        call.respond(SQL.Insert(regCall.email, regCall.username, regCall.password))
    }
}
fun Route.DeleteUser() {
    delete("/profile") {
        val session = call.sessions.get<SessionData>() ?: SessionData(0, "Guest")
        call.respond(SQL.Delete(session.userID!!))
    }
}
fun Route.EditUser() {
    post<lUpdateData> { editCall ->
        if (editCall.dataType.contains("role") && !user.haveFullAccess) {
            throw CallException(403,"Access Denied")
        }
        if(editCall.dataType.lastIndex != editCall.newValue.lastIndex){ throw CallException(400, "Lists Are Not Equal")}
        call.respond(SQL.Update(editCall.userID,editCall.dataType,editCall.newValue))
    }
    post<lUpdateMe> { editCall ->
        when {
            editCall.dataType.lastIndex != editCall.newValue.lastIndex -> throw CallException(400, "Lists Are Not Equal")
            editCall.dataType.contains("role") -> throw CallException(403,"Access Denied")
            editCall.dataType.contains("userID") -> throw CallException(403,"Access Denied")
            editCall.dataType.contains("mute") -> throw CallException(403,"Access Denied")
        }
        val session = call.sessions.get<SessionData>() ?: SessionData(0, "Guest")
        call.respond(SQL.Update(session.userID!!,editCall.dataType,editCall.newValue))
    }
}
fun Route.Users() {
    get("/profile") {
        val session = call.sessions.get<SessionData>() ?: SessionData(0, "Guest")
        when {
            user.haveFullAccess -> call.respond(SQL.GetFullUserData(session.userID!!))
            !user.haveFullAccess -> call.respond(SQL.GetUserData(session.userID!!))
        }
    }
    get<lGetUsers> { usersCall ->
        when {
            usersCall.limit<1 -> throw CallException(400, "Wrong Sintax(limit<=0)")
            usersCall.page<1 -> throw CallException(400, "Wrong Sintax(page<=0)")
            else-> when (user.haveFullAccess) {
                true -> call.respond(SQL.GetFullUserList(usersCall.page, usersCall.limit))
                false -> call.respond(SQL.GetUserList(usersCall.page, usersCall.limit))
            }
        }
    }
    get<lUser> {userCall ->
        if(!isEmailValid(userCall.email)){throw CallException(400, "Email Is not valid")}
        when(user.haveFullAccess){
            true -> call.respond(SQL.GetFullUserData(SQL.GetUserID(userCall.email)))
            false -> {
                val data = SQL.GetUserData(SQL.GetUserID(userCall.email))
                when (data.role) {
                    "BANNED" -> throw CallException(410, "Banned")
                    "DELETED" -> throw CallException(410, "Deleted")
                }
                call.respond(data)
            }
        }
    }
}