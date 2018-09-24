import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.sessions.*

fun Route.AddUser() {
    put<lRegData> { regCall ->
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
        call.respond(SQL.Update(editCall.userID,editCall.dataType,editCall.newValue))
    }
    post<lUpdateMe> { editCall ->
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
    get<lGetUsers> {usersCall ->
        when(user.haveFullAccess){
            true -> call.respond(SQL.GetFullUserList(usersCall.page, usersCall.limit))
            false -> call.respond(SQL.GetUserList(usersCall.page, usersCall.limit))
        }
    }
    get<lUser> {userCall ->
        when(user.haveFullAccess){
            true -> call.respond(SQL.GetFullUserData(SQL.GetUserID(userCall.email)))
            false -> call.respond(SQL.GetUserData(SQL.GetUserID(userCall.email)))
        }
    }
}