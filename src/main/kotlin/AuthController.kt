
import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.sessions.SessionStorage
import kotlinx.coroutines.experimental.io.*
import java.io.ByteArrayOutputStream
import kotlin.coroutines.experimental.coroutineContext

@Location("/login") data class LoginData(val email: String, val userpass:String)

data class SessionData (val session_id:String, val email: String)

fun Route.LoginUser() {
    get<LoginData> { ld ->
        if (checkPass(ld.email,ld.userpass))  {
            val session_id = saltGenerator(10)
            call.sessions.set(HeaderData(session_id))

            val sesdata = SessionData(session_id,ld.email)

            call.respond(HttpStatusCode.OK)
        } else {
        call.respond(HttpStatusCode.BadRequest)
    }
    }
}
fun Route.LogoutUser() {
    get("/logout") {
        call.respond(HttpStatusCode.OK)
    }
}

interface SessionInterface {
    suspend fun write(id: String, provider: suspend (ByteWriteChannel) -> Unit)
    suspend fun invalidate(id: String)
    suspend fun <R> read(id: String, consumer: suspend (ByteReadChannel) -> R): R
}

abstract class SimplifiedSessionStorage : SessionStorage {
    abstract suspend fun read(id: String): ByteArray?
    abstract suspend fun write(id: String, data: ByteArray?): Unit

    override suspend fun invalidate(id: String) {
        write(id, null)
    }

    override suspend fun <R> read(id: String, consumer: suspend (ByteReadChannel) -> R): R {
        val data = read(id) ?: throw NoSuchElementException("Session $id not found")
        return consumer(ByteReadChannel(data))
    }

    override suspend fun write(id: String, provider: suspend (ByteWriteChannel) -> Unit) {
        return provider(reader(coroutineContext, autoFlush = true) {
            write(id, channel.readAvailable())
        }.channel)
    }
}

suspend fun ByteReadChannel.readAvailable(): ByteArray {
    val data = ByteArrayOutputStream()
    val temp = ByteArray(1024)
    while (!isClosedForRead) {
        val read = readAvailable(temp)
        if (read <= 0) break
        data.write(temp, 0, read)
    }
    return data.toByteArray()
}