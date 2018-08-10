import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun SQLUpdate(userID:Int, dataType:List<String>, newValue:List<String>): HttpStatusCode {
    var result = HttpStatusCode.OK
    var newEmail=""
    var newUsername=""
    var newAvatarURL =""
    var newPassword =""
    var newRole=""
    var salt1=""
    var salt2=""



    try {
        dataType.forEachIndexed { dataIndex, element ->
            when (dataType.get(dataIndex)) {
                "email" -> if(isEmailValid(newValue.get(dataIndex))) {
                            newEmail = newValue.get(dataIndex)}
                "role" -> newRole = newValue.get(dataIndex)
                "username" -> newUsername = newValue.get(dataIndex)
                "password" -> {
                    newPassword = newValue.get(dataIndex)
                    salt1 = saltGenerator(6)
                    salt2 = saltGenerator(6)
                }
                "avatarURL" -> newAvatarURL = newValue.get(dataIndex)
            }
        }
    } catch (e:Exception) {result=HttpStatusCode.BadRequest}
    try {
        Database.connect(baseURL, baseDriver, baseRoot, basePass)
        transaction {
            user_list.update({ user_list.userID eq userID}) {


                if(!newEmail.equals("")){it[userEmail] = newEmail}
                if(!newRole.equals("")){it[role] = newRole}
                if(!newUsername.equals("")){it[userName] = newUsername}
                if(!newPassword.equals("")){it[userPass] = hashit(newPassword, salt1, salt2)
                                            it[baseSalt1] = salt1
                                            it[baseSalt2] = salt2}
                if(!newAvatarURL.equals("")){it[avatarURL] = newAvatarURL}
            }
            println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)+" User updated")
        }
    }catch (e:Exception) {result = HttpStatusCode.BadRequest}
    return result
}