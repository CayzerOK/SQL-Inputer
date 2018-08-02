import io.ktor.application.*
import io.ktor.auth.authentication
import io.ktor.features.conversionService
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.locations.*
import io.ktor.pipeline.*
import io.ktor.request.path
import io.ktor.request.queryString
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.AttributeKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.javaType

data class UserRights(
        val haveFullAccess:Boolean,
        val accessTo:List<String>,
        val canUpdate:Boolean,
        val canDelete:Boolean,
        val canBan:Boolean,
        val canMute:Boolean)

var User = UserRights(false, listOf(""),false,false,false,false)

class RightsChecker() {
    class Configuration {
        var prop = "value"
    }
    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, RightsChecker> {
        override val key = AttributeKey<RightsChecker>("RightsChecker")
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): RightsChecker {
            val configuration = RightsChecker.Configuration().apply(configure)
            val feature = RightsChecker()

            val FilterPhase = PipelinePhase("CallFilter")
            pipeline.insertPhaseAfter(ApplicationCallPipeline.Infrastructure, FilterPhase)

            pipeline.intercept(FilterPhase) {
                val session = call.sessions.get<SessionData>() ?: SessionData(0, "Guest")
                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + " User ${session.userID}, ${session.role} connected")
                when (session.role) {
                    "Guest" -> User = UserRights(
                            haveFullAccess = false,
                            accessTo = listOf(""),
                            canUpdate = false,
                            canDelete = false,
                            canBan = false,
                            canMute = false)
                    "User" -> User = UserRights(
                            haveFullAccess = false,
                            accessTo = listOf("lUsers"),
                            canUpdate = false,
                            canDelete = false,
                            canBan = false,
                            canMute = false)
                    "Moder" -> User = UserRights(
                            haveFullAccess = false,
                            accessTo = listOf("lUsers"),
                            canUpdate = true,
                            canDelete = false,
                            canBan = false,
                            canMute = true)
                    "Admin" -> User = UserRights(
                            haveFullAccess = true,
                            accessTo = listOf("lUsers"),
                            canUpdate = true,
                            canDelete = true,
                            canBan = true,
                            canMute = true)
                }
                application.environment.monitor.subscribe(Routing.RoutingCallStarted) {
                    call: RoutingApplicationCall ->
                    println("Route started: ${call.route}")
                    println()
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