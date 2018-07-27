import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun SQLUserNameUpdate(user_id:Int, name:String): HttpStatusCode {
    var result = HttpStatusCode.BadRequest
    try {
        Class.forName("com.mysql.cj.jdbc.Driver")
        Database.connect(baseURL, baseDriver, baseRoot, basePass)
        System.out.println("[MySQL] Connected")
        transaction {
            user_list.update({ user_list.userID eq user_id}) {
                it[userName] = name
                result = HttpStatusCode.OK
            }
        }
    }catch (e:ExceptionInInitializerError) {result = HttpStatusCode.BadRequest}
    return result
}

fun SQLAvatarUpdate(user_id:Int, url:String): HttpStatusCode {
    var result = HttpStatusCode.BadRequest
    try {
        Class.forName("com.mysql.cj.jdbc.Driver")
        Database.connect(baseURL, baseDriver, baseRoot, basePass)
        System.out.println("[MySQL] Connected")
        transaction {
            user_list.update({ user_list.userID eq user_id }) {
                it[avatarURL] = url
                result = HttpStatusCode.OK
            }
        }
    } catch (e:ExceptionInInitializerError) {result = HttpStatusCode.BadRequest}
    return result
}

fun SQLPassUpdate(user_id:Int, new_pass:String): HttpStatusCode {
    val salt1 = saltGenerator(6)
    val salt2 = saltGenerator(6)
    var result = HttpStatusCode.BadRequest
    try {
        Class.forName("com.mysql.cj.jdbc.Driver")
        Database.connect(baseURL, baseDriver, baseRoot, basePass)
        System.out.println("[MySQL] Connected")
        transaction {
            user_list.update({ user_list.userID eq user_id }) {
                it[userPass] = hashit(new_pass, salt1, salt2)
                it[baseSalt1] = salt1
                it[baseSalt2] = salt2
                result = HttpStatusCode.OK
            }
        }
    } catch (e:ExceptionInInitializerError){result = HttpStatusCode.BadRequest}
    return result
}

fun SQLEmailUpdate(user_id:Int, email:String): HttpStatusCode {
    var result = HttpStatusCode.BadRequest
    if (isEmailValid(email)) {
        Class.forName("com.mysql.cj.jdbc.Driver")
        Database.connect(baseURL, baseDriver, baseRoot, basePass)
        System.out.println("[MySQL] Connected")
        transaction {
            user_list.update({ user_list.userID eq user_id }) {
                it[userEmail] = email
                result = HttpStatusCode.OK
            }
        }
    } else result = HttpStatusCode.BadRequest
    return result
}

fun SQLRoleUpdate(userID:Int, new_role:String): HttpStatusCode {
    var result = HttpStatusCode.BadRequest
    try {
        Class.forName("com.mysql.cj.jdbc.Driver")
        Database.connect(baseURL, baseDriver, baseRoot, basePass)
        System.out.println("[MySQL] Connected")
        transaction {
            user_list.update({ user_list.userID eq userID }) {
                it[role] = new_role
                result = HttpStatusCode.OK
            }
        }
    } catch (e:Exception) {result = HttpStatusCode.BadRequest}
    return result
}