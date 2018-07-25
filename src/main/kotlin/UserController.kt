import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.*

fun Route.AddUser() {
    put<UserData> { ud ->
        when {
            !isEmailValid(ud.email) -> call.respond(HttpStatusCode.BadRequest)
            !sqlInsert(ud.email, ud.username, ud.userpass) -> call.respond(HttpStatusCode.Conflict)
            else -> call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.DeleteUser() {
    delete<UserData> {ud ->
        val session = call.sessions.get<SessionData>() ?: SessionData(0,"Guest")
        when {
            session.user_id==0 -> call.respond(HttpStatusCode.Unauthorized)
            SQLDelete(session.user_id, ud.userpass) -> call.respond(HttpStatusCode.OK)
            else -> call.respond(HttpStatusCode.BadRequest)
        }
    }
}

fun Route.EditUser() {
    post<UpdateData> { upd ->
        val session = call.sessions.get<SessionData>() ?: SessionData(0, "Guest")
        if (User.canUpdate||upd.user_id==session.user_id) {
            when(upd.datatype){
                "email" ->
                    if ((User.haveFullAccess || upd.user_id==session.user_id)
                            && SQLEmailUpdate(upd.user_id, upd.new_data)) {
                        call.respond(HttpStatusCode.OK)}
                    else call.respond(HttpStatusCode.BadRequest)
                "username" ->
                    if (SQLUserNameUpdate(upd.user_id,upd.new_data)){
                        call.respond(HttpStatusCode.OK)}
                    else call.respond(HttpStatusCode.BadRequest)
                "avatar_url" ->
                    if (SQLAvatarUpdate(upd.user_id,upd.new_data)) {
                        call.respond(HttpStatusCode.OK)}
                    else call.respond(HttpStatusCode.BadRequest)
                "role" ->
                    if (User.haveFullAccess && SQLRoleUpdate(upd.user_id,upd.new_data)) {
                        call.respond(HttpStatusCode.OK)}
                    else call.respond(HttpStatusCode.BadRequest)
            }
        } else call.respond(HttpStatusCode.BadRequest)
    }
}