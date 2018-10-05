import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.transactions.transaction

fun SQLDelete(userID:Int): HttpStatusCode {
    transaction {
        val target = UserData.findById(userID)
        if (target != null) {
            target.userRole = "DELETED"
        }
    }
    return HttpStatusCode.OK
}