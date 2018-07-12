import io.ktor.application.*
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.*
import org.apache.http.HttpStatus

fun Routing.upload(middlewares: List<Middleware<Unit, HttpStatus>>): Route {
    return post("upload") {

        val eval = middlewares.evaluate(this)
        when(eval.success) {
            true -> success(call)
            else -> call.respondText(eval.result!!.gsonify(), ContentType.Application.Json)
        }
    }
}

private fun success(call: ApplicationCall) {
    println("SUCCESS")
}
