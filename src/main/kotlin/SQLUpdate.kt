import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun SQLUpdate(userID: Int, dataType:List<String>, newValue:List<String>): HttpStatusCode {
    var newEmail=""
    var newUserName=""
    var newAvatarURL =""
    var newPassword =""
    var newRole=""
    var salt1=""
    var salt2=""

    dataType.forEachIndexed { dataIndex, element ->
        when (element) {
            "email" -> if(isEmailValid(newValue[dataIndex])) {
                newEmail = newValue[dataIndex]}
            "role" -> newRole = newValue[dataIndex]
            "username" -> newUserName = newValue[dataIndex]
            "password" -> {
                newPassword = newValue[dataIndex]
                salt1 = saltGenerator(6)
                salt2 = saltGenerator(6)
            }
            "avatarURL" -> newAvatarURL = newValue.get(dataIndex)
            else -> throw CallException(400,"Ivalid dataType")
        }
        if(newValue.get(dataIndex) == "") throw CallException(400, "Empty Value")
    }
    transaction {
        val target = UserData.findById(userID)
        if (target==null) throw CallException(404, "User Not Found")
        if(!newEmail.equals("")){target.userEmail = newEmail}
        if(!newRole.equals("")){target.userRole = newRole}
        if(!newUserName.equals("")){target.userName = newUserName}
        if(!newPassword.equals("")){target.userPass = hashit(newPassword, salt1, salt2)
            target.baseSalt1 = salt1
            target.baseSalt2 = salt2}
        if(!newAvatarURL.equals("")){target.avatarURL = newAvatarURL}
    }
    println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)+" User $userID updated")
    return HttpStatusCode.OK
}