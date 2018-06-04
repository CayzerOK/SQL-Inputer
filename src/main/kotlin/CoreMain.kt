import io.ktor.application.*
import io.ktor.features.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.sessions.SessionStorageMemory
import io.ktor.sessions.Sessions
import io.ktor.sessions.header
import org.jetbrains.exposed.sql.Table



val base_url:String = "jdbc:mysql://localhost:3306/user_base?useUnicode=true&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
val base_driver:String = "com.mysql.cj.jdbc.Driver"
val base_root:String = "root"
val base_pass:String = "3CE0DE8545098E16CDB"

object user_list : Table() {
    val user_id = integer("user_id").primaryKey().autoIncrement().uniqueIndex()
    var user_email = varchar("user_email",45).uniqueIndex()
    val user_name = varchar("user_name",45)
    val user_pass = varchar("user_pass",255)
    val avatar_url = varchar("user_url",255).default("http://nvsdushor.ru/wp-content/uploads/2018/02/шаблон3.jpg")
    val base_salt1 = varchar("salt1", 45)
    val base_salt2 = varchar("salt2", 45)
}

data class HeaderData(val session_id:String)

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Locations)
    install(Sessions) {
        header<HeaderData>("Header")
    }
    routing{
        AddUser()
        DeleteUser()
        LoginUser()
        LogoutUser()

    }
}


