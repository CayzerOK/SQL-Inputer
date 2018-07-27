import io.ktor.http.HttpStatusCode
import io.ktor.http.toHttpDateString
import org.jetbrains.exposed.sql.CurrentDateTime
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun SQLDelete(userID:Int): HttpStatusCode {
    Database.connect(baseURL, baseDriver, baseRoot, basePass)
    var result = HttpStatusCode.BadRequest
    try {
            transaction {
            user_list.deleteWhere { user_list.userID eq userID }
                result = HttpStatusCode.OK
                println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)+" User $userID deleted")
        }
    } catch (e: Exception) {result = HttpStatusCode.BadRequest}
    return result
}