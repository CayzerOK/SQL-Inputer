import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.routing.*
import io.ktor.sessions.*
import org.jetbrains.exposed.sql.Table
import java.text.DateFormat

val baseURL:String = "jdbc:mysql://localhost:3306/user_base?useUnicode=true&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
val baseDriver:String = "com.mysql.cj.jdbc.Driver"
val baseRoot:String = "root"
val basePass:String = "3CE0DE8545098E16CDB"

object user_list : Table() {
    val userID = integer("user_id").primaryKey().autoIncrement().uniqueIndex()
    var userEmail = varchar("user_email",45).uniqueIndex()
    val userName = varchar("user_name",45)
    val userPass = varchar("user_pass",255)
    val avatarURL = varchar("user_url",255).default("http://nvsdushor.ru/wp-content/uploads/2018/02/шаблон3.jpg")
    val baseSalt1 = varchar("salt1", 45)
    val baseSalt2 = varchar("salt2", 45)
    val role = varchar("role", 10).default("User")
    val ban = bool("ban").default(false)
    val mute = bool("mute").default(false)
}

@Location("/login") data class lLoginData(val email:String, val password: String)
@Location("/users") data class lGetUsers(val page:Int, val limit:Int)
@Location("/users/user") data class lUser(val email: String)
@Location("/users") data class lUpdateData(val userID: Int, val datatype:String, val newData:String)
@Location("/users") data class lRegData(val email: String, val username:String, val userpass:String)


data class SessionData(val userID: Int, val role:String = "Guest")

fun Application.main() {
    install(Routing)
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(Sessions) {
        cookie<SessionData>("SESSION_FEATURE_SESSION_ID", SessionStorageMemory()) {
            cookie.path = "/"
        }
    }
    install(RightsChecker)
    install(Locations)
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
    routing{
        LoginUser()
        LogoutUser()
        AddUser()
        DeleteUser()
        EditUser()
        Users()
    }

}










