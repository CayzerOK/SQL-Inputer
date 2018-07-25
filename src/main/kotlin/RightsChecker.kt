import io.ktor.application.*
import io.ktor.http.toHttpDateString
import io.ktor.sessions.*
import io.ktor.util.AttributeKey
import kotlinx.html.currentTimeMillis

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
        var prop = "value" // mutable property
    }

    // implement ApplicationFeature in a companion object
    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, RightsChecker> {
        // create unique key for the feature
        override val key = AttributeKey<RightsChecker>("RightsChecker")

        // implement installation script
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): RightsChecker {

            // run configuration script
            val configuration = RightsChecker.Configuration().apply(configure)
            val feature = RightsChecker(configuration)

            pipeline.intercept(ApplicationCallPipeline.Infrastructure) {
                val session = call.sessions.get<SessionData>() ?: SessionData(0,"Guest")
                when(session.role){
                    "Guest" -> User = UserRights(
                            haveFullAccess = false,
                            accessTo = listOf("Users"),
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
            return feature
        }
    }
}