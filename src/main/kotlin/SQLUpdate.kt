import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun SQLUpdate(userID: Int?, dataType:List<String>, newValue:List<String>): HttpStatusCode {
    var result = HttpStatusCode.OK
    var newEmail=""
    var newUserName=""
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
                "username" -> newUserName = newValue.get(dataIndex)
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
        transaction {
            val target = UserData.findById(userID!!)
                if(!newEmail.equals("")){target!!.userEmail = newEmail}
                if(!newRole.equals("")){target!!.userRole = newRole}
                if(!newUserName.equals("")){target!!.userName = newUserName}
                if(!newPassword.equals("")){target!!.userPass = hashit(newPassword, salt1, salt2)
                                            target!!.baseSalt1 = salt1
                                            target!!.baseSalt2 = salt2}
                if(!newAvatarURL.equals("")){target!!.avatarURL = newAvatarURL}
            }
            println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)+" User updated")
    }catch (e:Exception) {result = HttpStatusCode.BadRequest}
    return result
}