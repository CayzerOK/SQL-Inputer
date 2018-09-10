/**
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

class RightsChecker {
    class Configuration
    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, RightsChecker> {
        override val key = AttributeKey<RightsChecker>("RightsChecker")
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): RightsChecker {
            val feature = RightsChecker()
            pipeline.intercept(ApplicationCallPipeline.Infrastructure) {
                val session = call.sessions.get<SessionData>() ?: SessionData(0, "Guest")
                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + " ${session.role} ${session.userID}: connected")
                when (session.role) {
                    "Guest" -> User = UserRights(
                            haveFullAccess = false,
                            accessToGET = listOf("/users/[page]/[limit]","/users/[email]"),
                            accessToPUT = listOf("/users/[email]/[username]/[password]"),
                            accessToPOST = listOf("/login/[email]/[password]"),
                            accessToDELETE = listOf(""),
                            canBan = false,
                            canMute = false)
                    "User" -> User = UserRights(
                            haveFullAccess = false,
                            accessToGET = listOf("/logout","/profile","/users/[page]/[limit]","/users/[email]"),
                            accessToPUT = listOf(""),
                            accessToPOST = listOf("/profile/[dataType]/[newValue]"),
                            accessToDELETE = listOf(""),
                            canBan = false,
                            canMute = false)
                    "Moder" -> User = UserRights(
                            haveFullAccess = false,
                            accessToGET = listOf("/logout","/profile","/users/[page]/[limit]","/users/[email]"),
                            accessToPUT = listOf(""),
                            accessToPOST = listOf("/users/[userID]/[dataType]/[newValue]","/profile/[dataType]/[newValue]"),
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

                application.environment.monitor.subscribe(Routing.RoutingCallStarted) {
                    val call:RoutingApplicationCall = it
                    var RouteString = call.route.parent.toString()
                    when(call.request.local.method.value) {
                        "GET" -> if (User.accessToGET.contains(RouteString)||User.haveFullAccess) {
                                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                                        " ${session.role} ${session.userID}: GET "+RouteString)
                                } else {
                                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                                        " ${session.role} ${session.userID}: GET "+RouteString+" [ERROR]")
                                throw AccessErrorException(" ${session.role} ${session.userID}: GET "+RouteString+" [ERROR]")}

                        "PUT" -> if (User.accessToPUT.contains(RouteString)||User.haveFullAccess) {
                                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                                        " ${session.role} ${session.userID}: PUT "+RouteString)
                                } else {
                                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                                        " ${session.role} ${session.userID}: PUT "+RouteString+" [ERROR]")
                                throw AccessErrorException(" ${session.role} ${session.userID}: PUT "+RouteString+" [ERROR]")}

                        "POST" -> if (User.accessToPOST.contains(RouteString)||User.haveFullAccess) {
                                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                                        " ${session.role} ${session.userID}: POST "+RouteString)
                                } else {
                                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                                        " ${session.role} ${session.userID}: POST "+RouteString+" [ERROR]")
                                throw AccessErrorException(" ${session.role} ${session.userID}: POST "+RouteString+" [ERROR]")}

                        "DELETE" -> if (User.accessToDELETE.contains(RouteString)||User.haveFullAccess) {
                                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                                        " ${session.role} ${session.userID}: DELETE "+RouteString)
                                } else {
                                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) +
                                        " ${session.role} ${session.userID}: DELETE "+RouteString+" [ERROR]")
                            throw AccessErrorException(" ${session.role} ${session.userID}: DELETE "+RouteString+" [ERROR]")
                        }
                    }
                }
            }
            return feature
        }
    }
}
        */