import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

fun SQLDelete(userID:Int): HttpStatusCode {
    Database.connect(baseURL, baseDriver, baseRoot, basePass)
    System.out.println("[MySQL] Connected")
    var result = HttpStatusCode.BadRequest
    try {
            transaction {
            user_list.deleteWhere { user_list.userID eq userID }
                result = HttpStatusCode.OK
        }
    } catch (e: java.sql.SQLIntegrityConstraintViolationException) {result = HttpStatusCode.BadRequest}
    return result
}