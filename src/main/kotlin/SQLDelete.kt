import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

fun SQLDelete(user_id:Int): HttpStatusCode {
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
    var result = HttpStatusCode.BadRequest
    try {
            transaction {
            user_list.deleteWhere { user_list.user_id eq user_id }
                result = HttpStatusCode.OK
        }
    } catch (e: java.sql.SQLIntegrityConstraintViolationException) {result = HttpStatusCode.BadRequest}
    return result
}