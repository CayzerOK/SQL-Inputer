import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.respond
import io.ktor.routing.Route

@Location("/users") data class UserData(val email: String,
                                        val username:String,
                                        val userpass:String)

fun Route.AddUser() {
    put<UserData> { ud ->
        println(ud.username + " is connected.")
        when {
            !isEmailValid(ud.email) -> call.respond(HttpStatusCode.BadRequest)
            !sqlInsert(ud.email, ud.username, ud.userpass) -> call.respond(HttpStatusCode.Conflict)
            else -> call.respond(HttpStatusCode.OK)
        }
    }
}
fun Route.DeleteUser() {
            delete<UserData> { ud ->
                if (sqlDelete(ud.email, ud.userpass)) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }

