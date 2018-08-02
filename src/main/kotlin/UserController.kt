import io.ktor.application.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.accept
import io.ktor.sessions.*

fun Route.AddUser() {
    put<lRegData> { regCall ->
        when {
            !isEmailValid(regCall.email) -> call.respond(HttpStatusCode.BadRequest)
            else -> call.respond(SQLInsert(regCall.email, regCall.username, regCall.userpass))
        }
    }
}

fun Route.DeleteUser() {
    delete<lUser> {delCall ->
        val session = call.sessions.get<SessionData>() ?: SessionData(0,"Guest")
        when {
            session.userID==0 -> call.respond(HttpStatusCode.Unauthorized)
            User.canDelete -> call.respond(SQLDelete(SQLGetID(delCall.email)))
            session.userID.equals(delCall.email) -> call.respond(SQLDelete(SQLGetID(delCall.email)))
            else -> call.respond(HttpStatusCode.BadRequest)
        }
    }
}

fun Route.EditUser() {
    post<lUpdateData> { editCall ->
        val session = call.sessions.get<SessionData>() ?: SessionData(0, "Guest")
        if (User.canUpdate||editCall.userID==session.userID) {
            when(editCall.datatype){
                "email" -> if ((User.haveFullAccess || editCall.userID==session.userID)) {
                    call.respond(SQLEmailUpdate(editCall.userID, editCall.newData))
                } else call.respond(HttpStatusCode.BadRequest)
                "role" -> if (User.haveFullAccess) {
                    call.respond(SQLRoleUpdate(editCall.userID,editCall.newData))
                } else call.respond(HttpStatusCode.BadRequest)
                "username" -> call.respond(SQLUserNameUpdate(editCall.userID,editCall.newData))
                "password" -> call.respond(SQLPassUpdate(editCall.userID, editCall.newData))
                "avatarURL" -> call.respond(SQLAvatarUpdate(editCall.userID,editCall.newData))
            }
        }
    }
}