import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.transactions.transaction

fun SQLDelete(userID:Int): HttpStatusCode {
    var result = HttpStatusCode.BadRequest
    try {
        transaction {
            var target = UserData.findById(userID)
            if (target != null) {
                target.delete()
                result = HttpStatusCode.OK
            }
        }
    } catch (e: Exception) {result = HttpStatusCode.BadRequest}
    return result
}