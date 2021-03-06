import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.sessions.*
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.*
import java.sql.BatchUpdateException
import java.text.DateFormat

val baseURL:String = "jdbc:mysql://localhost:3306/user_base?useUnicode=true&autoReconnect=true&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
val baseDriver:String = "com.mysql.cj.jdbc.Driver"
val baseRoot:String = "root"
val basePass:String = "3CE0DE8545098E16CDB"

object UserList : IntIdTable() {
    val userID:Column<Int> = integer("id").primaryKey().autoIncrement().uniqueIndex()
    val userEmail: Column<String> = varchar("user_email",45).uniqueIndex()
    val userName: Column<String> = varchar("user_name",45)
    val userPass: Column<String> = varchar("user_pass",255)
    val avatarURL: Column<String> = varchar("user_url",255).default("http://nvsdushor.ru/wp-content/uploads/2018/02/шаблон3.jpg")
    val baseSalt1: Column<String> = varchar("salt1", 45)
    val baseSalt2: Column<String> = varchar("salt2", 45)
    val role: Column<String> = varchar("role", 10).default("User")
    val ban: Column<Boolean> = bool("ban").default(false)
    val mute: Column<Boolean> = bool("mute").default(false)
}

class UserData(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserData>(UserList)
    var userID by UserList.userID
    var userEmail by UserList.userEmail
    var userName by UserList.userName
    var userPass by UserList.userPass
    var avatarURL by UserList.avatarURL
    var baseSalt1 by UserList.baseSalt1
    var baseSalt2 by UserList.baseSalt2
    var userRole by UserList.role
    var ban by UserList.ban
    var mute by UserList.mute
}

@Location("/login") data class lLoginData(val email:String, val password: String)
@Location("/users") data class lGetUsers(val page:Int, val limit:Int)
@Location("/users/") data class lUser(val email: String)
@Location("/users/") data class lUpdateData(val userID: Int, val dataType:List<String>, val newValue:List<String>)
@Location("/users/") data class lRegData(val email: String, val username:String, val password:String)
@Location("/profile") data class lUpdateMe(val dataType:List<String>, val newValue:List<String>)

data class SessionData(val userID: Int? = 0, val role:String = "Guest")
fun Application.main() {
    Database.connect(baseURL,baseDriver, baseRoot, basePass)
    install(Routing)
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(Sessions) {
        cookie<SessionData>("SESSION_ID", SessionStorageMemory()) {
            cookie.path = "/"
        }
    }
    install(DataConversion)
    install(StatusPages) {
        exception<ExceptionInInitializerError> {
            call.respond(HttpStatusCode.InternalServerError,"Database Not Found")
        }
        exception<BatchUpdateException> { exception ->
            call.respond(HttpStatusCode.Conflict, "Email Alredy Used")
        }
        exception<CallException> {exception ->
            call.respond(HttpStatusCode.fromValue(exception.code), exception.message)
        }
        exception<GETException> { exception ->
            when(exception.message) {
                "/logout" -> call.respond(HttpStatusCode.Unauthorized,"Unauthorized")
                "/profile" -> call.respond(HttpStatusCode.Unauthorized,"Unauthorized")
                else -> call.respond(HttpStatusCode.NotFound, "Not Found")
            }
        }
        exception<PUTException> { exception ->
            when(exception.message) {
                "/users/[email]/[username]/[password]" -> call.respond(HttpStatusCode.Conflict, "Alredy Logined")
                else -> call.respond(HttpStatusCode.NotFound,"Not Found")
            }
        }
        exception<POSTException> { exception ->
            when(exception.message) {
                "/login/[email]/[password]" -> call.respond(HttpStatusCode.Conflict,"Alredy Logined")
                "/users/[userID]/[dataType]/[newValue]" -> call.respond(HttpStatusCode.Forbidden, "Forbidden")
                "/profile/[dataType]/[newValue]" -> call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                else -> call.respond(HttpStatusCode.NotFound,"Not Found")
            }
        }
        exception<DELException> {
            call.respond(HttpStatusCode.Forbidden,"Forbitten")
        }
    }
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
        CheckRights()
    }
}