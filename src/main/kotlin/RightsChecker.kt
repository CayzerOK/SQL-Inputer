import io.ktor.application.*
import io.ktor.http.toHttpDateString
import io.ktor.sessions.*
import io.ktor.util.AttributeKey
import io.ktor.util.GreenwichMeanTime
import kotlinx.html.currentTimeMillis
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class UserRights(
        val haveFullAccess:Boolean,
        val accessTo:List<String>,
        val canUpdate:Boolean,
        val canDelete:Boolean,
        val canBan:Boolean,
        val canMute:Boolean)

var User = UserRights(false, listOf(""),false,false,false,false)

class RightsChecker(configuration: Configuration) {
    val prop = configuration.prop // get snapshot of config into immutable property
    class Configuration {
        var prop = "value"
    }
    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, RightsChecker> {
        override val key = AttributeKey<RightsChecker>("RightsChecker")
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): RightsChecker {
            val configuration = RightsChecker.Configuration().apply(configure)
            val feature = RightsChecker(configuration)
            pipeline.intercept(ApplicationCallPipeline.Infrastructure) {
                val session = call.sessions.get<SessionData>() ?: SessionData(0,"Guest")
                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)+" User ${session.userID}, ${session.role} connected")
                when(session.role){
                    "Guest" -> User = UserRights(
                            haveFullAccess = false,
                            accessTo = listOf(""),
                            canUpdate = false,
                            canDelete = false,
                            canBan = false,
                            canMute = false)
                    "User" -> User = UserRights(
                            haveFullAccess = false,
                            accessTo = listOf("Users"),
                            canUpdate = false,
                            canDelete = false,
                            canBan = false,
                            canMute = false)
                    "Moder" -> User = UserRights(
                            haveFullAccess = false,
                            accessTo = listOf("Users"),
                            canUpdate = true,
                            canDelete = false,
                            canBan = false,
                            canMute = true)
                    "Admin" -> User = UserRights(
                            haveFullAccess = true,
                            accessTo = listOf("Users"),
                            canUpdate = true,
                            canDelete = true,
                            canBan = true,
                            canMute = true)
                }
            }
            pipeline.intercept(ApplicationCallPipeline.Call) {
                val session = call.sessions.get<SessionData>() ?: SessionData(0,"Guest")
                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)+" User ${session.userID}, ${session.role} call answered")
            }
            return feature
        }
    }
}