import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.request.receiveParameters
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.*
import java.time.*
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
class AccessErrorException(override var message:String): Exception(message)
class RightsChecker() {
    class Configuration {
        var prop = "value"
    }
    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, RightsChecker> {
        override val key = AttributeKey<RightsChecker>("RightsChecker")
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): RightsChecker {
            val configuration = RightsChecker.Configuration().apply(configure)
            val feature = RightsChecker()

            pipeline.intercept(ApplicationCallPipeline.Infrastructure) {

                val session = call.sessions.get<SessionData>() ?: SessionData(0, "Guest")
                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + " User ${session.userID}, ${session.role}, connected")
                when (session.role) {
                    "Guest" -> User = UserRights(
                            haveFullAccess = false,
                            accessToGET = listOf(""),
                            accessToPUT = listOf(""),
                            accessToPOST = listOf("/login/[email]/[password]"),
                            accessToDELETE = listOf(""),
                            canBan = false,
                            canMute = false)
                    "User" -> User = UserRights(
                            haveFullAccess = false,
                            accessToGET = listOf("/logout"),
                            accessToPUT = listOf(""),
                            accessToPOST = listOf(""),
                            accessToDELETE = listOf(""),
                            canBan = false,
                            canMute = false)
                    "Moder" -> User = UserRights(
                            haveFullAccess = false,
                            accessToGET = listOf("/logout"),
                            accessToPUT = listOf(""),
                            accessToPOST = listOf(""),
                            accessToDELETE = listOf(""),
                            canBan = false,
                            canMute = true)
                    "Admin" -> User = UserRights(
                            haveFullAccess = true,
                            accessToGET = listOf("/logout"),
                            accessToPUT = listOf(""),
                            accessToPOST = listOf(""),
                            accessToDELETE = listOf(""),
                            canBan = true,
                            canMute = true)
                }
                application.environment.monitor.subscribe(Routing.RoutingCallStarted) { call: RoutingApplicationCall ->
                    var RouteString = call.route.parent.toString()
                    when(call.request.local.method.value) {
                        "GET" -> if (User.accessToGET.contains(RouteString)||User.haveFullAccess) {
                                println("USER CAN GET "+RouteString)
                                } else {throw AccessErrorException("USER CAN NOT GET "+RouteString)}

                        "PUT" -> if (User.accessToPUT.contains(RouteString)||User.haveFullAccess) {
                                println("USER CAN PUT "+RouteString)
                                } else {throw AccessErrorException("USER CAN NOT PUT "+RouteString)}

                        "POST" -> if (User.accessToPOST.contains(RouteString)||User.haveFullAccess) {
                                println("USER CAN POST "+RouteString)
                                } else {throw AccessErrorException("USER CAN NOT POST "+RouteString)}

                        "DELETE" -> if (User.accessToDELETE.contains(RouteString)||User.haveFullAccess) {
                                println("USER CAN DELETE ")
                                } else {throw AccessErrorException("USER CAN NOT DELETE "+RouteString)}
                    }
                }
            }
            return feature
        }
    }
}