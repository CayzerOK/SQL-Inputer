import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.*

fun Route.AddUser() {
    put<lRegData> { rd ->
        when {
            !isEmailValid(rd.email) -> call.respond(HttpStatusCode.BadRequest)
            else -> call.respond(SQLInsert(rd.email, rd.username, rd.userpass))
        }
    }
}

fun Route.DeleteUser() {
    delete<lUser> {ud ->
        val session = call.sessions.get<SessionData>() ?: SessionData(0,"Guest")
        when {
            session.user_id==0 -> call.respond(HttpStatusCode.Unauthorized)
            User.canDelete -> call.respond(SQLDelete(SQLGetID(ud.email)))
            session.user_id.equals(ud.email) -> call.respond(SQLDelete(SQLGetID(ud.email)))
            else -> call.respond(HttpStatusCode.BadRequest)
        }
    }
}

fun Route.EditUser() {
    post<lUpdateData> { upd ->
        val session = call.sessions.get<SessionData>() ?: SessionData(0, "Guest")
        if (User.canUpdate||upd.user_id==session.user_id) {
            when(upd.datatype){
                "email" -> if ((User.haveFullAccess || upd.user_id==session.user_id)) {
                    call.respond(SQLEmailUpdate(upd.user_id, upd.new_data))
                } else call.respond(HttpStatusCode.BadRequest)
                "role" -> if (User.haveFullAccess) {
                    call.respond(SQLRoleUpdate(upd.user_id,upd.new_data))
                } else call.respond(HttpStatusCode.BadRequest)
                "username" -> call.respond(SQLUserNameUpdate(upd.user_id,upd.new_data))
                "password" -> call.respond(SQLPassUpdate(upd.user_id, upd.new_data))
                "avatar_url" -> call.respond(SQLAvatarUpdate(upd.user_id,upd.new_data))
            }
        }
    }
}