import io.ktor.application.application
import io.ktor.application.call
import io.ktor.routing.*
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class UserRights(
        val haveFullAccess:Boolean,
        val accessToGET:List<String>,
        val accessToPUT:List<String>,
        val accessToPOST:List<String>,
        val accessToDELETE:List<String>,
        val canBan:Boolean,
        val canMute:Boolean)

var User = UserRights(false, listOf(""), listOf(""), listOf(""), listOf(""),false,false)

class GETException(override var message:String): Exception(message)
class POSTException(override var message:String): Exception(message)
class PUTException(override var message:String): Exception(message)
class DELException(override var message:String): Exception(message)

fun Route.CheckRights() {
    application.environment.monitor.subscribe(Routing.RoutingCallStarted) { call: RoutingApplicationCall ->
        val session = call.sessions.get<SessionData>() ?: SessionData(0, "Guest")
        when (session.role) {
            "Guest" -> User = UserRights(
                    haveFullAccess = false,
                    accessToGET = listOf("/users/[page]/[limit]", "/users/[email]"),
                    accessToPUT = listOf("/users/[email]/[username]/[password]"),
                    accessToPOST = listOf("/login/[email]/[password]"),
                    accessToDELETE = listOf(""),
                    canBan = false,
                    canMute = false)
            "User" -> User = UserRights(
                    haveFullAccess = false,
                    accessToGET = listOf("/logout", "/profile", "/users/[page]/[limit]", "/users/[email]"),
                    accessToPUT = listOf(""),
                    accessToPOST = listOf("/profile/[dataType]/[newValue]"),
                    accessToDELETE = listOf(""),
                    canBan = false,
                    canMute = false)
            "Moder" -> User = UserRights(
                    haveFullAccess = false,
                    accessToGET = listOf("/logout", "/profile", "/users/[page]/[limit]", "/users/[email]"),
                    accessToPUT = listOf(""),
                    accessToPOST = listOf("/users/[userID]/[dataType]/[newValue]", "/profile/[dataType]/[newValue]"),
                    accessToDELETE = listOf(""),
                    canBan = false,
                    canMute = true)
            "Admin" -> User = UserRights(
                    haveFullAccess = true,
                    accessToGET = listOf(""),
                    accessToPUT = listOf(""),
                    accessToPOST = listOf(""),
                    accessToDELETE = listOf(""),
                    canBan = true,
                    canMute = true)
        }
        var RouteString = call.route.parent.toString()
        when (call.request.local.method.value) {
            "GET" -> if (User.accessToGET.contains(RouteString) || User.haveFullAccess) {
                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                        " ${session.role} ${session.userID}: GET " + RouteString)
            } else {
                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                        " ${session.role} ${session.userID}: GET " + RouteString + " [ERROR!]")
                throw GETException(call.route.parent.toString())
            }

            "PUT" -> if (User.accessToPUT.contains(RouteString) || User.haveFullAccess) {
                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                        " ${session.role} ${session.userID}: PUT " + RouteString)
            } else {
                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                        " ${session.role} ${session.userID}: PUT " + RouteString + " [ERROR!]")
                throw PUTException(call.route.parent.toString())
            }

            "POST" -> if (User.accessToPOST.contains(RouteString) || User.haveFullAccess) {
                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                        " ${session.role} ${session.userID}: POST " + RouteString)
            } else {
                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                        " ${session.role} ${session.userID}: POST " + RouteString + " [ERROR!]")
                throw POSTException(call.route.parent.toString())
            }

            "DELETE" -> if (User.accessToDELETE.contains(RouteString) || User.haveFullAccess) {
                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                        " ${session.role} ${session.userID}: DELETE " + RouteString)
            } else {
                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                        " ${session.role} ${session.userID}: DELETE " + RouteString + " [ERROR!]")
                throw DELException(call.route.parent.toString())
            }
        }
    }
}

