import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.*

fun Route.AddUser() {
    put<lRegData> { regCall ->
        when {
            !isEmailValid(regCall.email) -> call.respond(HttpStatusCode.BadRequest)
            else -> call.respond(SQLInsert(regCall.email, regCall.username, regCall.password))
        }
    }
}

fun Route.DeleteUser() {
    delete<lUser> {delCall ->
        call.respond(SQLDelete(SQLGetID(delCall.email)))
    }
}

fun Route.EditUser() {
    post<lUpdateData> { editCall ->
        call.respond(SQLUpdate(editCall.userID,editCall.dataType,editCall.newValue))
    }
    post<lUpdateMe> { editCall ->
        val session = call.sessions.get<SessionData>() ?: SessionData(0, "Guest")
        call.respond(SQLUpdate(session.userID,editCall.dataType,editCall.newValue))
    }
}