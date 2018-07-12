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
        if (SQLDelete(SQLGetID(ud.email), ud.userpass)) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}

fun Route.EditUser() {
    post<UpdatePublicData> {upd ->
        val session = call.sessions.get<SessionData>() ?: SessionData(0)
        when(upd.datatype) {
            "user_name" -> if (SQLUserNameUpdate(session.user_id, upd.data)) {call.respond(HttpStatusCode.OK)}
            else {call.respond(HttpStatusCode.BadRequest)}
            "avatar_url" -> if (SQLAvatarUpdate(session.user_id, upd.data)) {call.respond(HttpStatusCode.OK)}
            else {call.respond(HttpStatusCode.BadRequest)}

        }
    }
    post<UpdatePrivateData> {upd ->
        val session = call.sessions.get<SessionData>() ?: SessionData(0)
        when(upd.datatype) {
            "user_pass" -> if(SQLPassUpdate(session.user_id, upd.data, upd.password)) {call.respond(HttpStatusCode.OK)}
            else {call.respond(HttpStatusCode.BadRequest)}
            "user_email" -> if(SQLEmailUpdate(session.user_id, upd.data, upd.password)) {call.respond(HttpStatusCode.OK)}
            else {call.respond(HttpStatusCode.BadRequest)}
        }
    }
}



