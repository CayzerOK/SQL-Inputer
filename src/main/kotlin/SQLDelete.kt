import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.transactions.transaction

fun SQLDelete(userID:Int): HttpStatusCode {
    var result = HttpStatusCode.BadRequest
    try {
        transaction {
            val target = UserData.findById(userID)
            if (target != null) {
                target.userRole = "DELETED"
                result = HttpStatusCode.OK
            }
        }
    } catch (e: Exception) {result = HttpStatusCode.BadRequest}
    return result
}