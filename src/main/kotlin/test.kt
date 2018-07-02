import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.sessions.*
import kotlinx.html.*


data class SampleSession(
        val counter: Int
)


enum class SessionType {
    CLIENT_SIGNED,
    SERVER_MEMORY,
    SERVER_DIRECTORY
}

fun Route.TestFun() {
    get("/") {
        call.respondRedirect("/view")
    }
    get("/view") {
        val session = call.sessions.get<SampleSession>() ?: SampleSession(0)
            call.respondHtml {
                head {
                    title { +"Ktor: sessions" }
                }
                body {
                    p {
                        +"Hello from Ktor Sessions sample application"
                    }
                    p {
                        +"Counter: ${session.counter}"
                    }
                    nav {
                        ul {
                            li { a("/increment") { +"increment" } }
                            li { a("/view") { +"view" } }
                        }
                    }
                }
            }
        }
    get("/increment") {
        val session = call.sessions.get<SampleSession>() ?: SampleSession(0)
        call.sessions.set(session.copy(counter = session.counter + 1))
        call.respondRedirect("/view")
    }
}